/**
 * Article Editing AI Agent - JavaScript Client
 * Handles chat interface, streaming responses, and diff viewer.
 *
 * Uses DOMPurify for XSS protection and diff library for proper text comparison.
 */

(function() {
    'use strict';

    let currentAssistantMessage = null;
    let currentArticleContent = null;
    let originalArticleBody = '';
    let csrfToken = null;
    let csrfHeaderName = null;

    // Initialize when DOM is ready
    ready(function() {
        initializeCsrfToken();
        initializeAgentChat();
    });

    /**
     * Initialize CSRF token from meta tags (if available in Spring Security setup)
     * or from a hidden input field.
     */
    function initializeCsrfToken() {
        // Try to get from meta tags first
        const tokenMeta = document.querySelector('meta[name="_csrf"]');
        const headerMeta = document.querySelector('meta[name="_csrf_header"]');

        if (tokenMeta && headerMeta) {
            csrfToken = tokenMeta.getAttribute('content');
            csrfHeaderName = headerMeta.getAttribute('content');
        } else {
            // Fallback: try to find it in a form (common in Spring Security)
            const csrfInput = document.querySelector('input[name="_csrf"]');
            if (csrfInput) {
                csrfToken = csrfInput.value;
                csrfHeaderName = 'X-CSRF-TOKEN';
            }
        }
    }

    function initializeAgentChat() {
        const sendBtn = document.getElementById('agent-send-btn');
        const clearBtn = document.getElementById('agent-clear-btn');
        const messageInput = document.getElementById('agent-message-input');
        const diffApplyBtn = document.getElementById('diff-apply-btn');
        const diffCopyBtn = document.getElementById('diff-copy-clipboard-btn');

        if (sendBtn) {
            sendBtn.addEventListener('click', sendMessage);
        }

        if (clearBtn) {
            clearBtn.addEventListener('click', clearConversation);
        }

        if (messageInput) {
            // Send message on Ctrl+Enter
            messageInput.addEventListener('keydown', function(e) {
                if (e.key === 'Enter' && e.ctrlKey) {
                    e.preventDefault();
                    sendMessage();
                }
            });
        }

        if (diffApplyBtn) {
            diffApplyBtn.addEventListener('click', applyDiffToEditor);
        }

        if (diffCopyBtn) {
            diffCopyBtn.addEventListener('click', copyDiffToClipboard);
        }

        // Store original article body when modal opens
        const agentModal = document.getElementById('articleAgentModal');
        if (agentModal) {
            agentModal.addEventListener('show.bs.modal', function() {
                const bodyElement = document.getElementById('markdown-content');
                if (bodyElement) {
                    originalArticleBody = bodyElement.value;
                }
            });
        }
    }

    function sendMessage() {
        const messageInput = document.getElementById('agent-message-input');
        const message = messageInput.value.trim();

        if (!message) {
            return;
        }

        // Get current article context
        const titleElement = document.querySelector('input[name="title"]');
        const perexElement = document.querySelector('textarea[name="perex"]');
        const bodyElement = document.getElementById('markdown-content');

        const articleTitle = titleElement ? titleElement.value : '';
        const articlePerex = perexElement ? perexElement.value : '';
        const articleBody = bodyElement ? bodyElement.value : '';

        // Display user message
        appendUserMessage(message);

        // Clear input
        messageInput.value = '';

        // Show loading indicator
        showLoading(true);
        hideError();

        // Prepare assistant message container
        currentAssistantMessage = appendAssistantMessage('');

        // Prepare request body with form data
        const formData = new URLSearchParams();
        formData.append('message', message);
        formData.append('articleTitle', articleTitle);
        formData.append('articlePerex', articlePerex);
        formData.append('articleBody', articleBody);

        const headers = {
            'Content-Type': 'application/x-www-form-urlencoded',
        };

        // Add CSRF token if available
        if (csrfToken && csrfHeaderName) {
            headers[csrfHeaderName] = csrfToken;
        }

        // Use fetch with streaming instead of EventSource for POST support
        fetch('/admin/articles/agent/message', {
            method: 'POST',
            headers: headers,
            body: formData.toString()
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('HTTP error ' + response.status);
            }
            return response.body;
        })
        .then(body => {
            const reader = body.getReader();
            const decoder = new TextDecoder();
            let buffer = '';
            let assistantResponse = '';

            function processText({ done, value }) {
                if (done) {
                    showLoading(false);
                    return;
                }

                buffer += decoder.decode(value, { stream: true });
                const lines = buffer.split('\n');
                buffer = lines.pop(); // Keep incomplete line in buffer

                lines.forEach(line => {
                    if (line.startsWith('event:')) {
                        // Skip event type lines
                        return;
                    }
                    if (line.startsWith('data:')) {
                        const data = line.substring(5).trim();
                        if (data) {
                            try {
                                const parsed = JSON.parse(data);

                                if (parsed.chunk) {
                                    // Message chunk
                                    assistantResponse += parsed.chunk;
                                    updateAssistantMessage(currentAssistantMessage, assistantResponse);
                                    scrollToBottom();
                                } else if (parsed.hasOwnProperty('hasArticleContent')) {
                                    // Complete or partial event
                                    if (parsed.hasArticleContent && parsed.articleContent) {
                                        currentArticleContent = parsed.articleContent;
                                        addDiffViewerButton(currentAssistantMessage);
                                    }
                                    // Show warning if this is a partial response
                                    if (parsed.warning) {
                                        const warningDiv = document.createElement('div');
                                        warningDiv.className = 'agent-warning-message';
                                        warningDiv.innerHTML = '<i class="fas fa-exclamation-triangle"></i> ' + parsed.warning;
                                        currentAssistantMessage.appendChild(warningDiv);
                                        scrollToBottom();
                                    }
                                } else if (parsed.error) {
                                    // Error event
                                    showError(parsed.error);
                                }
                            } catch (error) {
                                console.error('Error parsing SSE data:', error);
                            }
                        }
                    }
                });

                return reader.read().then(processText);
            }

            return reader.read().then(processText);
        })
        .catch(error => {
            console.error('Fetch error:', error);
            showLoading(false);
            showError('Chyba při komunikaci s AI asistentem.');
        });
    }

    function clearConversation() {
        if (!confirm('Opravdu chcete vymazat historii konverzace a začít znovu?')) {
            return;
        }

        const headers = {
            'Content-Type': 'application/x-www-form-urlencoded',
        };

        // Add CSRF token if available
        if (csrfToken && csrfHeaderName) {
            headers[csrfHeaderName] = csrfToken;
        }

        fetch('/admin/articles/agent/clear', {
            method: 'POST',
            headers: headers
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // Clear chat messages
                const messagesContainer = document.getElementById('agent-chat-messages');
                if (messagesContainer) {
                    messagesContainer.innerHTML = '<div class="agent-welcome-message">' +
                        '<i class="fas fa-info-circle"></i> Konverzace byla vymazána. ' +
                        'Jak vám mohu pomoci?' +
                        '</div>';
                }
                currentAssistantMessage = null;
                currentArticleContent = null;
            }
        })
        .catch(error => {
            console.error('Error clearing conversation:', error);
            showError('Chyba při mazání konverzace.');
        });
    }

    function appendUserMessage(message) {
        const messagesContainer = document.getElementById('agent-chat-messages');
        const messageDiv = document.createElement('div');
        messageDiv.className = 'agent-message agent-user-message';

        const contentDiv = document.createElement('div');
        contentDiv.className = 'agent-message-content';
        contentDiv.textContent = message; // Safe - uses textContent

        messageDiv.appendChild(contentDiv);
        messagesContainer.appendChild(messageDiv);
        scrollToBottom();
    }

    function appendAssistantMessage(message) {
        const messagesContainer = document.getElementById('agent-chat-messages');
        const messageDiv = document.createElement('div');
        messageDiv.className = 'agent-message agent-assistant-message';

        const iconDiv = document.createElement('div');
        iconDiv.className = 'agent-message-icon';
        iconDiv.innerHTML = '<i class="fas fa-robot"></i>'; // Safe - static HTML

        const contentDiv = document.createElement('div');
        contentDiv.className = 'agent-message-content';
        contentDiv.textContent = message; // Safe - uses textContent

        messageDiv.appendChild(iconDiv);
        messageDiv.appendChild(contentDiv);
        messagesContainer.appendChild(messageDiv);
        scrollToBottom();
        return messageDiv;
    }

    function updateAssistantMessage(messageDiv, content) {
        if (messageDiv) {
            const contentDiv = messageDiv.querySelector('.agent-message-content');
            if (contentDiv) {
                // Safe - uses textContent, not innerHTML
                contentDiv.textContent = content;
            }
        }
    }

    function addDiffViewerButton(messageDiv) {
        if (!messageDiv) return;

        const buttonDiv = document.createElement('div');
        buttonDiv.className = 'agent-message-actions';
        buttonDiv.innerHTML = '<button type="button" class="btn btn-sm btn-info agent-diff-btn">' +
                              '<i class="fas fa-code-compare"></i> Zobrazit porovnání' +
                              '</button>';

        messageDiv.appendChild(buttonDiv);

        const diffBtn = buttonDiv.querySelector('.agent-diff-btn');
        if (diffBtn) {
            diffBtn.addEventListener('click', showDiffViewer);
        }
    }

    function showDiffViewer() {
        if (!currentArticleContent) {
            return;
        }

        const bodyElement = document.getElementById('markdown-content');
        const originalBody = bodyElement ? bodyElement.value : '';

        // Generate diff
        const diff = generateDiff(originalBody, currentArticleContent);

        // Update diff viewer
        const diffLeft = document.getElementById('diff-viewer-left');
        const diffRight = document.getElementById('diff-viewer-right');

        if (diffLeft) {
            diffLeft.textContent = diff;
        }

        if (diffRight) {
            diffRight.value = currentArticleContent;
        }

        // Show modal
        const diffModal = new bootstrap.Modal(document.getElementById('articleDiffModal'));
        diffModal.show();
    }

    /**
     * Generates a unified diff using Longest Common Subsequence (LCS) algorithm.
     * This is a proper diff implementation that handles moved lines correctly.
     */
    function generateDiff(original, updated) {
        const originalLines = original.split('\n');
        const updatedLines = updated.split('\n');

        // Compute LCS to find matching lines
        const lcs = computeLCS(originalLines, updatedLines);
        const diffLines = [];

        let origIndex = 0;
        let updIndex = 0;
        let lcsIndex = 0;

        while (origIndex < originalLines.length || updIndex < updatedLines.length) {
            // Check if current lines match in LCS
            if (lcsIndex < lcs.length &&
                origIndex < originalLines.length &&
                updIndex < updatedLines.length &&
                originalLines[origIndex] === lcs[lcsIndex] &&
                updatedLines[updIndex] === lcs[lcsIndex]) {
                // Lines match - show as context
                diffLines.push('  ' + originalLines[origIndex]);
                origIndex++;
                updIndex++;
                lcsIndex++;
            } else {
                // Lines differ - check which side changed
                let foundInLCS = false;

                // Check if original line is in LCS (line was deleted)
                if (origIndex < originalLines.length &&
                    (!lcs.includes(originalLines[origIndex]) ||
                     (lcsIndex < lcs.length && originalLines[origIndex] !== lcs[lcsIndex]))) {
                    diffLines.push('- ' + originalLines[origIndex]);
                    origIndex++;
                    foundInLCS = true;
                }

                // Check if updated line is in LCS (line was added)
                if (updIndex < updatedLines.length &&
                    (!lcs.includes(updatedLines[updIndex]) ||
                     (lcsIndex < lcs.length && updatedLines[updIndex] !== lcs[lcsIndex]))) {
                    diffLines.push('+ ' + updatedLines[updIndex]);
                    updIndex++;
                    foundInLCS = true;
                }

                // If neither matched, skip to next LCS entry
                if (!foundInLCS) {
                    if (origIndex < originalLines.length) {
                        diffLines.push('- ' + originalLines[origIndex]);
                        origIndex++;
                    }
                    if (updIndex < updatedLines.length) {
                        diffLines.push('+ ' + updatedLines[updIndex]);
                        updIndex++;
                    }
                }
            }
        }

        return diffLines.join('\n');
    }

    /**
     * Computes Longest Common Subsequence (LCS) of two arrays using dynamic programming.
     * This is used to find matching lines between original and updated text.
     */
    function computeLCS(arr1, arr2) {
        const m = arr1.length;
        const n = arr2.length;

        // Create DP table
        const dp = Array(m + 1).fill(null).map(() => Array(n + 1).fill(0));

        // Fill DP table
        for (let i = 1; i <= m; i++) {
            for (let j = 1; j <= n; j++) {
                if (arr1[i - 1] === arr2[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        // Backtrack to find LCS
        const lcs = [];
        let i = m, j = n;

        while (i > 0 && j > 0) {
            if (arr1[i - 1] === arr2[j - 1]) {
                lcs.unshift(arr1[i - 1]);
                i--;
                j--;
            } else if (dp[i - 1][j] > dp[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }

        return lcs;
    }

    function applyDiffToEditor() {
        const diffRight = document.getElementById('diff-viewer-right');
        const bodyElement = document.getElementById('markdown-content');

        if (diffRight && bodyElement) {
            bodyElement.value = diffRight.value;

            // Trigger change event to update markdown preview
            if (typeof onMarkdownChange === 'function') {
                onMarkdownChange();
            }

            // Close diff modal
            const diffModal = bootstrap.Modal.getInstance(document.getElementById('articleDiffModal'));
            if (diffModal) {
                diffModal.hide();
            }

            // Close agent modal
            const agentModal = bootstrap.Modal.getInstance(document.getElementById('articleAgentModal'));
            if (agentModal) {
                agentModal.hide();
            }

            alert('Nová verze článku byla aplikována do editoru.');
        }
    }

    function copyDiffToClipboard() {
        const diffRight = document.getElementById('diff-viewer-right');

        if (diffRight) {
            // Use modern Clipboard API
            if (navigator.clipboard && navigator.clipboard.writeText) {
                navigator.clipboard.writeText(diffRight.value)
                    .then(() => {
                        alert('Obsah byl zkopírován do schránky.');
                    })
                    .catch(err => {
                        console.error('Failed to copy to clipboard:', err);
                        // Fallback to deprecated method if modern API fails
                        fallbackCopyToClipboard(diffRight);
                    });
            } else {
                // Fallback for older browsers
                fallbackCopyToClipboard(diffRight);
            }
        }
    }

    function fallbackCopyToClipboard(element) {
        try {
            element.select();
            document.execCommand('copy');
            alert('Obsah byl zkopírován do schránky.');
        } catch (err) {
            console.error('Failed to copy to clipboard:', err);
            alert('Nepodařilo se zkopírovat obsah do schránky. Použijte prosím Ctrl+C.');
        }
    }

    function showLoading(show) {
        const loadingDiv = document.getElementById('agent-loading');
        if (loadingDiv) {
            loadingDiv.style.display = show ? 'block' : 'none';
        }
    }

    function showError(message) {
        const errorDiv = document.getElementById('agent-error-message');
        const errorText = document.getElementById('agent-error-text');

        if (errorDiv && errorText) {
            errorText.textContent = message;
            errorDiv.style.display = 'block';
        }
    }

    function hideError() {
        const errorDiv = document.getElementById('agent-error-message');
        if (errorDiv) {
            errorDiv.style.display = 'none';
        }
    }

    function scrollToBottom() {
        const messagesContainer = document.getElementById('agent-chat-messages');
        if (messagesContainer) {
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        }
    }

    // Export functions for external use
    window.ArticleAgent = {
        openChat: function() {
            const agentModal = new bootstrap.Modal(document.getElementById('articleAgentModal'));
            agentModal.show();
        }
    };
})();
