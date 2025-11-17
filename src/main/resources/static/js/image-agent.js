/**
 * Image Generation Agent - JavaScript Client
 * Handles image generation interface integrated with article editing chat.
 */

(function() {
    'use strict';

    let csrfToken = null;
    let csrfHeaderName = null;
    let currentImageJobId = null;
    let imagePollingInterval = null;
    let currentTempImageId = null;

    // Configuration
    const IMAGE_POLL_INTERVAL_MS = 3000; // Poll every 3 seconds
    const MAX_IMAGE_POLL_ATTEMPTS = 60; // 60 * 3s = 3 minutes max

    // Initialize when DOM is ready
    ready(function() {
        initializeCsrfToken();
        initializeImageAgent();
    });

    /**
     * Initialize CSRF token from meta tags or hidden input
     */
    function initializeCsrfToken() {
        const tokenMeta = document.querySelector('meta[name="_csrf"]');
        const headerMeta = document.querySelector('meta[name="_csrf_header"]');

        if (tokenMeta && headerMeta) {
            csrfToken = tokenMeta.getAttribute('content');
            csrfHeaderName = headerMeta.getAttribute('content');
        } else {
            const csrfInput = document.querySelector('input[name="_csrf"]');
            if (csrfInput) {
                csrfToken = csrfInput.value;
                csrfHeaderName = 'X-CSRF-TOKEN';
            }
        }
    }

    function initializeImageAgent() {
        const generateImageBtn = document.getElementById('agent-generate-image-btn');

        if (generateImageBtn) {
            generateImageBtn.addEventListener('click', generateImage);
        }
    }

    function generateImage() {
        const messageInput = document.getElementById('agent-message-input');
        const prompt = messageInput.value.trim();

        if (!prompt) {
            alert('Zadejte prosím popis obrázku, který chcete vygenerovat.');
            return;
        }

        // Display user request message
        appendUserMessage('🎨 Vygeneruj obrázek: ' + prompt);

        // Clear input
        messageInput.value = '';

        // Show generating indicator
        showImageGenerating(true);
        hideError();

        const headers = {
            'Content-Type': 'application/x-www-form-urlencoded',
        };

        // Add CSRF token if available
        if (csrfToken && csrfHeaderName) {
            headers[csrfHeaderName] = csrfToken;
        }

        // Send image generation request
        fetch('/admin/articles/agent/image/generate', {
            method: 'POST',
            headers: headers,
            body: new URLSearchParams({
                'prompt': prompt
            })
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('HTTP error ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            if (data.error) {
                showError(data.error);
                showImageGenerating(false);
                return;
            }

            if (data.jobId) {
                currentImageJobId = data.jobId;
                startImagePolling(currentImageJobId);
            } else {
                throw new Error('No job ID received');
            }
        })
        .catch(error => {
            console.error('Fetch error:', error);
            showImageGenerating(false);
            showError('Chyba při komunikaci se serverem.');
        });
    }

    let imagePollAttempts = 0;

    function startImagePolling(jobId) {
        imagePollAttempts = 0;

        // Clear any existing polling interval
        if (imagePollingInterval) {
            clearInterval(imagePollingInterval);
        }

        // Poll immediately, then every IMAGE_POLL_INTERVAL_MS
        pollImageJobStatus(jobId);
        imagePollingInterval = setInterval(() => {
            pollImageJobStatus(jobId);
        }, IMAGE_POLL_INTERVAL_MS);
    }

    function stopImagePolling() {
        if (imagePollingInterval) {
            clearInterval(imagePollingInterval);
            imagePollingInterval = null;
        }
    }

    function pollImageJobStatus(jobId) {
        imagePollAttempts++;

        if (imagePollAttempts > MAX_IMAGE_POLL_ATTEMPTS) {
            stopImagePolling();
            showImageGenerating(false);
            showError('Časový limit vypršel. Generování obrázku trvá příliš dlouho.');
            return;
        }

        fetch(`/admin/articles/agent/image/job/status/${jobId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('HTTP error ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            if (data.error) {
                stopImagePolling();
                showImageGenerating(false);
                showError(data.error);
                return;
            }

            // Check status
            if (data.status === 'completed') {
                stopImagePolling();
                showImageGenerating(false);

                // Display generated image
                if (data.tempImageId) {
                    currentTempImageId = data.tempImageId;
                    displayGeneratedImage(data.tempImageId);
                } else {
                    showError('Obrázek byl vygenerován, ale nepodařilo se ho načíst.');
                }
            } else if (data.status === 'error') {
                stopImagePolling();
                showImageGenerating(false);
                showError(data.error || 'Neznámá chyba při generování obrázku');
            }
            // If status is 'processing', continue polling
        })
        .catch(error => {
            console.error('Polling error:', error);
            stopImagePolling();
            showImageGenerating(false);
            showError('Chyba při komunikaci se serverem.');
        });
    }

    function displayGeneratedImage(tempImageId) {
        const messagesContainer = document.getElementById('agent-chat-messages');
        const imageContainer = document.createElement('div');
        imageContainer.className = 'agent-message agent-assistant-message';

        const iconDiv = document.createElement('div');
        iconDiv.className = 'agent-message-icon';
        iconDiv.innerHTML = '<i class="fas fa-image"></i>';

        const contentDiv = document.createElement('div');
        contentDiv.className = 'agent-message-content';
        contentDiv.style.maxWidth = '85%';

        const previewDiv = document.createElement('div');
        previewDiv.className = 'image-preview-container';

        // Create image element
        const img = document.createElement('img');
        img.src = `/admin/articles/agent/image/temp/${tempImageId}`;
        img.alt = 'Vygenerovaný obrázek';
        img.className = 'image-preview-thumbnail';
        img.loading = 'lazy';

        // Click to view full size
        img.addEventListener('click', function() {
            viewFullSizeImage(tempImageId);
        });

        // Info badge
        const infoBadge = document.createElement('div');
        infoBadge.className = 'image-info-badge';
        infoBadge.textContent = '1024×1024 WebP';

        // Action buttons
        const actionsDiv = document.createElement('div');
        actionsDiv.className = 'image-preview-actions';

        const viewBtn = document.createElement('button');
        viewBtn.type = 'button';
        viewBtn.className = 'btn btn-sm btn-secondary';
        viewBtn.innerHTML = '<i class="fas fa-search-plus"></i> Zobrazit v plné velikosti';
        viewBtn.addEventListener('click', function() {
            viewFullSizeImage(tempImageId);
        });

        const saveBtn = document.createElement('button');
        saveBtn.type = 'button';
        saveBtn.className = 'btn btn-sm btn-success';
        saveBtn.innerHTML = '<i class="fas fa-save"></i> Uložit do galerie';
        saveBtn.addEventListener('click', function() {
            saveImageToGallery(tempImageId, saveBtn);
        });

        actionsDiv.appendChild(viewBtn);
        actionsDiv.appendChild(saveBtn);

        previewDiv.appendChild(img);
        previewDiv.appendChild(infoBadge);
        previewDiv.appendChild(actionsDiv);

        contentDiv.appendChild(previewDiv);
        imageContainer.appendChild(iconDiv);
        imageContainer.appendChild(contentDiv);
        messagesContainer.appendChild(imageContainer);

        scrollToBottom();
    }

    function viewFullSizeImage(tempImageId) {
        // Create modal if it doesn't exist
        let modal = document.getElementById('imageViewModal');
        if (!modal) {
            modal = document.createElement('div');
            modal.id = 'imageViewModal';
            modal.className = 'modal fade';
            modal.setAttribute('tabindex', '-1');
            modal.setAttribute('role', 'dialog');
            modal.innerHTML = `
                <div class="modal-dialog modal-xl" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">Vygenerovaný obrázek</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Zavřít"></button>
                        </div>
                        <div class="modal-body">
                            <img id="fullSizeImage" src="" alt="Vygenerovaný obrázek v plné velikosti" style="width: 100%; height: auto;">
                        </div>
                    </div>
                </div>
            `;
            document.body.appendChild(modal);
        }

        // Set image source
        const fullSizeImg = document.getElementById('fullSizeImage');
        fullSizeImg.src = `/admin/articles/agent/image/temp/${tempImageId}`;

        // Show modal
        const bsModal = new bootstrap.Modal(modal);
        bsModal.show();
    }

    function saveImageToGallery(tempImageId, button) {
        // Disable button and show loading state
        button.disabled = true;
        const originalHtml = button.innerHTML;
        button.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Ukládám...';

        const headers = {};

        // Add CSRF token if available
        if (csrfToken && csrfHeaderName) {
            headers[csrfHeaderName] = csrfToken;
        }

        fetch(`/admin/articles/agent/image/save/${tempImageId}`, {
            method: 'POST',
            headers: headers
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                button.innerHTML = '<i class="fas fa-check"></i> Uloženo';
                button.className = 'btn btn-sm btn-outline-success';
                alert('Obrázek byl úspěšně uložen do galerie jako: ' + data.filename);
            } else {
                button.disabled = false;
                button.innerHTML = originalHtml;
                showError(data.error || 'Nepodařilo se uložit obrázek');
            }
        })
        .catch(error => {
            console.error('Save error:', error);
            button.disabled = false;
            button.innerHTML = originalHtml;
            showError('Chyba při ukládání obrázku');
        });
    }

    function showImageGenerating(show) {
        let generatingDiv = document.getElementById('image-generating');

        if (show) {
            if (!generatingDiv) {
                const messagesContainer = document.getElementById('agent-chat-messages');
                generatingDiv = document.createElement('div');
                generatingDiv.id = 'image-generating';
                generatingDiv.className = 'image-generating';
                generatingDiv.innerHTML = `
                    <i class="fas fa-palette"></i>
                    <div class="image-generating-text">Generuji obrázek pomocí AI...</div>
                `;
                messagesContainer.appendChild(generatingDiv);
                scrollToBottom();
            }
        } else {
            if (generatingDiv) {
                generatingDiv.remove();
            }
        }
    }

    function appendUserMessage(message) {
        const messagesContainer = document.getElementById('agent-chat-messages');
        const messageDiv = document.createElement('div');
        messageDiv.className = 'agent-message agent-user-message';

        const contentDiv = document.createElement('div');
        contentDiv.className = 'agent-message-content';
        contentDiv.textContent = message;

        messageDiv.appendChild(contentDiv);
        messagesContainer.appendChild(messageDiv);
        scrollToBottom();
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
    window.ImageAgent = {
        generateImage: generateImage
    };
})();
