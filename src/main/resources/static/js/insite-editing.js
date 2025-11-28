/**
 * In-site editing functionality for ArtBeams CMS
 * Allows admin users to edit localizations directly on the public website
 * @author ArtBeams
 */

(function() {
    'use strict';

    // Configuration
    const CONFIG = {
        localizationSelector: '[data-i18n-key]',
        articleSelector: 'article[data-article-id]',
        articleTitleSelector: '[data-article-title]',
        articlePerexSelector: '[data-article-perex]',
        editableClass: 'insite-editable',
        highlightClass: 'insite-highlight',
        editingClass: 'insite-editing',
        iconClass: 'insite-edit-icon',
        editorClass: 'insite-editor',
        apiEndpoint: '/admin/localisations/update-inline'
    };

    // State
    let currentlyEditing = null;
    let editableElements = [];

    /**
     * Initialize the in-site editing feature
     */
    function init() {
        // Find all editable elements
        findEditableElements();

        // Set up event listeners
        setupEventListeners();

        console.log('In-site editing initialized. Found', editableElements.length, 'editable elements');
    }

    /**
     * Find all elements that can be edited
     */
    function findEditableElements() {
        // Find all localization elements
        const localizationElements = document.querySelectorAll(CONFIG.localizationSelector);
        localizationElements.forEach(el => {
            if (!isElementEditable(el)) return;

            editableElements.push({
                type: 'localization',
                element: el,
                key: el.getAttribute('data-i18n-key')
            });

            el.classList.add(CONFIG.editableClass);
        });

        // Find article elements for navigation to editor
        const articleTitles = document.querySelectorAll(CONFIG.articleTitleSelector);
        articleTitles.forEach(el => {
            if (!isElementEditable(el)) return;

            editableElements.push({
                type: 'article-title',
                element: el,
                articleId: el.getAttribute('data-article-id')
            });

            el.classList.add(CONFIG.editableClass);
        });

        const articlePerexes = document.querySelectorAll(CONFIG.articlePerexSelector);
        articlePerexes.forEach(el => {
            if (!isElementEditable(el)) return;

            editableElements.push({
                type: 'article-perex',
                element: el,
                articleId: el.getAttribute('data-article-id')
            });

            el.classList.add(CONFIG.editableClass);
        });

        // Find full articles
        const articles = document.querySelectorAll(CONFIG.articleSelector);
        articles.forEach(el => {
            const articleBody = el.querySelector('.article-body');
            if (!articleBody || !isElementEditable(articleBody)) return;

            editableElements.push({
                type: 'article',
                element: articleBody,
                articleId: el.getAttribute('data-article-id')
            });

            articleBody.classList.add(CONFIG.editableClass);
        });
    }

    /**
     * Check if an element should be editable
     */
    function isElementEditable(el) {
        // Skip if element is inside an editor or already being edited
        if (el.closest('.' + CONFIG.editorClass)) return false;
        if (el.classList.contains(CONFIG.editingClass)) return false;

        // Skip if element is invisible
        const rect = el.getBoundingClientRect();
        if (rect.width === 0 || rect.height === 0) return false;

        return true;
    }

    /**
     * Set up event listeners for editable elements
     */
    function setupEventListeners() {
        editableElements.forEach(item => {
            const { element, type } = item;

            // Mouse enter - show edit icon
            element.addEventListener('mouseenter', (e) => {
                if (currentlyEditing) return;
                showEditIcon(element, type);
            });

            // Mouse leave - hide edit icon
            element.addEventListener('mouseleave', (e) => {
                if (currentlyEditing) return;
                hideEditIcon(element);
            });
        });

        // Close editor on Escape key
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && currentlyEditing) {
                cancelEditing();
            }
        });
    }

    /**
     * Show edit icon on hover
     */
    function showEditIcon(element, type) {
        element.classList.add(CONFIG.highlightClass);

        // Create edit icon if it doesn't exist
        let icon = element.querySelector('.' + CONFIG.iconClass);
        if (!icon) {
            icon = document.createElement('div');
            icon.className = CONFIG.iconClass;
            icon.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>';
            icon.title = type === 'localization' ? 'Edit localization' : 'Edit article';

            icon.addEventListener('click', (e) => {
                e.preventDefault();
                e.stopPropagation();
                handleEditClick(element, type);
            });

            element.appendChild(icon);
        }

        icon.style.display = 'block';
    }

    /**
     * Hide edit icon
     */
    function hideEditIcon(element) {
        element.classList.remove(CONFIG.highlightClass);

        const icon = element.querySelector('.' + CONFIG.iconClass);
        if (icon) {
            icon.style.display = 'none';
        }
    }

    /**
     * Handle edit icon click
     */
    function handleEditClick(element, type) {
        const item = editableElements.find(i => i.element === element);
        if (!item) return;

        if (type === 'localization') {
            startInlineEditing(element, item.key);
        } else if (type === 'article' || type === 'article-title' || type === 'article-perex') {
            navigateToArticleEditor(item.articleId);
        }
    }

    /**
     * Start inline editing for a localization
     */
    function startInlineEditing(element, key) {
        if (currentlyEditing) return;

        currentlyEditing = { element, key };
        element.classList.add(CONFIG.editingClass);

        // Get current text
        const currentText = element.textContent.trim();

        // Create editor
        const editor = document.createElement('div');
        editor.className = CONFIG.editorClass;

        const textarea = document.createElement('textarea');
        textarea.value = currentText;
        textarea.rows = Math.max(3, Math.ceil(currentText.length / 50));

        const buttonContainer = document.createElement('div');
        buttonContainer.className = 'insite-editor-buttons';

        const saveButton = document.createElement('button');
        saveButton.className = 'insite-btn insite-btn-success';
        saveButton.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"></polyline></svg>';
        saveButton.title = 'Save';
        saveButton.addEventListener('click', () => saveLocalization(key, textarea.value));

        const cancelButton = document.createElement('button');
        cancelButton.className = 'insite-btn insite-btn-secondary';
        cancelButton.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>';
        cancelButton.title = 'Cancel';
        cancelButton.addEventListener('click', cancelEditing);

        buttonContainer.appendChild(saveButton);
        buttonContainer.appendChild(cancelButton);

        editor.appendChild(textarea);
        editor.appendChild(buttonContainer);

        // Hide original content and show editor
        element.style.position = 'relative';
        const originalContent = element.innerHTML;
        element.innerHTML = '';
        element.appendChild(editor);

        // Store original content for restoration
        element._originalContent = originalContent;

        // Focus textarea
        textarea.focus();
        textarea.select();
    }

    /**
     * Save localization changes
     */
    function saveLocalization(key, newValue) {
        if (!currentlyEditing) return;

        const { element } = currentlyEditing;
        const originalValue = element.textContent.trim();

        // Show loading state
        const editor = element.querySelector('.' + CONFIG.editorClass);
        const saveButton = editor.querySelector('.insite-btn-success');
        const originalButtonContent = saveButton.innerHTML;
        saveButton.disabled = true;
        saveButton.innerHTML = '<span class="insite-spinner"></span>';

        // Get CSRF token
        const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content') ||
                         document.querySelector('input[name="_csrf"]')?.value;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content') || '_csrf';

        // Send update to server
        const headers = {
            'Content-Type': 'application/json'
        };
        if (csrfToken) {
            headers[csrfHeader] = csrfToken;
        }

        fetch(CONFIG.apiEndpoint, {
            method: 'POST',
            headers: headers,
            body: JSON.stringify({
                key: key,
                value: newValue
            })
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to save localization');
            }
            return response.json();
        })
        .then(data => {
            // Update element with new value
            element.innerHTML = element._originalContent;
            // Update the text content while preserving HTML structure
            const textNode = findTextNode(element);
            if (textNode) {
                textNode.textContent = newValue;
            } else {
                element.textContent = newValue;
            }

            // Show success feedback
            element.classList.add('insite-save-success');
            setTimeout(() => {
                element.classList.remove('insite-save-success');
            }, 2000);

            closeEditor();
        })
        .catch(error => {
            console.error('Error saving localization:', error);
            alert('Failed to save changes. Please try again or check the browser console for details.');

            // Restore button state
            saveButton.disabled = false;
            saveButton.innerHTML = originalButtonContent;
        });
    }

    /**
     * Find the primary text node in an element
     */
    function findTextNode(element) {
        for (let child of element.childNodes) {
            if (child.nodeType === Node.TEXT_NODE && child.textContent.trim()) {
                return child;
            }
        }
        return null;
    }

    /**
     * Cancel editing
     */
    function cancelEditing() {
        if (!currentlyEditing) return;

        const { element } = currentlyEditing;

        // Restore original content
        if (element._originalContent) {
            element.innerHTML = element._originalContent;
            delete element._originalContent;
        }

        closeEditor();
    }

    /**
     * Close editor and clean up
     */
    function closeEditor() {
        if (!currentlyEditing) return;

        const { element } = currentlyEditing;
        element.classList.remove(CONFIG.editingClass);

        currentlyEditing = null;
    }

    /**
     * Navigate to article editor in admin panel
     */
    function navigateToArticleEditor(articleId) {
        if (!articleId) return;

        // Navigate to article editor
        window.location.href = `/admin/articles/${articleId}`;
    }

    // Initialize when DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

})();
