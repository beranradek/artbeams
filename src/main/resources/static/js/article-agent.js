/**
 * Article Editing AI Agent - JavaScript Client
 * Handles chat interface, streaming responses, and diff viewer.
 */

(function() {
    'use strict';

    let currentAssistantMessage = null;
    let currentArticleContent = null;
    let originalArticleBody = '';

    // Initialize when DOM is ready
    ready(function() {
        initializeAgentChat();
    });

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

        // Create EventSource for streaming
        const params = new URLSearchParams({
            message: message,
            articleTitle: articleTitle,
            articlePerex: articlePerex,
            articleBody: articleBody
        });

        const eventSource = new EventSource('/admin/articles/agent/message?' + params.toString());
        let assistantResponse = '';

        eventSource.addEventListener('message', function(e) {
            try {
                const data = JSON.parse(e.data);
                if (data.chunk) {
                    assistantResponse += data.chunk;
                    updateAssistantMessage(currentAssistantMessage, assistantResponse);
                    scrollToBottom();
                }
            } catch (error) {
                console.error('Error parsing message event:', error);
            }
        });

        eventSource.addEventListener('complete', function(e) {
            try {
                const data = JSON.parse(e.data);
                showLoading(false);
                eventSource.close();

                // Check if article content was detected
                if (data.hasArticleContent && data.articleContent) {
                    currentArticleContent = data.articleContent;
                    addDiffViewerButton(currentAssistantMessage);
                }
            } catch (error) {
                console.error('Error parsing complete event:', error);
                showLoading(false);
                eventSource.close();
            }
        });

        eventSource.addEventListener('error', function(e) {
            console.error('EventSource error:', e);
            showLoading(false);
            eventSource.close();

            try {
                const data = JSON.parse(e.data);
                if (data.error) {
                    showError(data.error);
                } else {
                    showError('Chyba při komunikaci s AI asistentem. Zkontrolujte, zda je nastavena proměnná prostředí OPENAI_API_KEY.');
                }
            } catch (parseError) {
                showError('Chyba při komunikaci s AI asistentem. Zkontrolujte, zda je nastavena proměnná prostředí OPENAI_API_KEY.');
            }
        });

        eventSource.onerror = function() {
            showLoading(false);
            eventSource.close();
            showError('Chyba při komunikaci s AI asistentem. Zkontrolujte, zda je nastavena proměnná prostředí OPENAI_API_KEY.');
        };
    }

    function clearConversation() {
        if (!confirm('Opravdu chcete vymazat historii konverzace a začít znovu?')) {
            return;
        }

        fetch('/admin/articles/agent/clear', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            }
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
        messageDiv.innerHTML = '<div class="agent-message-content">' + escapeHtml(message) + '</div>';
        messagesContainer.appendChild(messageDiv);
        scrollToBottom();
    }

    function appendAssistantMessage(message) {
        const messagesContainer = document.getElementById('agent-chat-messages');
        const messageDiv = document.createElement('div');
        messageDiv.className = 'agent-message agent-assistant-message';
        messageDiv.innerHTML = '<div class="agent-message-icon"><i class="fas fa-robot"></i></div>' +
                               '<div class="agent-message-content">' + escapeHtml(message) + '</div>';
        messagesContainer.appendChild(messageDiv);
        scrollToBottom();
        return messageDiv;
    }

    function updateAssistantMessage(messageDiv, content) {
        if (messageDiv) {
            const contentDiv = messageDiv.querySelector('.agent-message-content');
            if (contentDiv) {
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

    function generateDiff(original, updated) {
        const originalLines = original.split('\n');
        const updatedLines = updated.split('\n');
        const diffLines = [];

        // Simple line-by-line diff
        const maxLines = Math.max(originalLines.length, updatedLines.length);

        for (let i = 0; i < maxLines; i++) {
            const origLine = originalLines[i] || '';
            const updLine = updatedLines[i] || '';

            if (origLine !== updLine) {
                if (origLine && !updatedLines.includes(origLine)) {
                    diffLines.push('- ' + origLine);
                }
                if (updLine && !originalLines.includes(updLine)) {
                    diffLines.push('+ ' + updLine);
                }
            } else if (origLine) {
                diffLines.push('  ' + origLine);
            }
        }

        return diffLines.join('\n');
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
            diffRight.select();
            document.execCommand('copy');
            alert('Obsah byl zkopírován do schránky.');
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

    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    // Export functions for external use
    window.ArticleAgent = {
        openChat: function() {
            const agentModal = new bootstrap.Modal(document.getElementById('articleAgentModal'));
            agentModal.show();
        }
    };
})();
