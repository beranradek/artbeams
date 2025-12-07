/**
 * Admin Keyboard Shortcuts
 * Provides keyboard shortcuts for power users in the admin interface
 */
(function() {
  'use strict';

  // Keyboard shortcut configuration
  const shortcuts = {
    '?': { action: 'showHelp', description: 'Show keyboard shortcuts help', category: 'General' },
    '/': { action: 'focusSearch', description: 'Focus search box', category: 'Navigation' },
    'Escape': { action: 'closeModal', description: 'Close modal or cancel operation', category: 'General' },
    'Ctrl+N': { action: 'createNew', description: 'Create new item (article, user, etc.)', category: 'Actions', macKey: 'Meta+N' },
    'Ctrl+S': { action: 'saveForm', description: 'Save current form', category: 'Actions', macKey: 'Meta+S' },
    'Ctrl+E': { action: 'toggleEdit', description: 'Toggle edit mode', category: 'Actions', macKey: 'Meta+E' },
    'g+a': { action: 'goToArticles', description: 'Go to Articles', category: 'Navigation' },
    'g+u': { action: 'goToUsers', description: 'Go to Users', category: 'Navigation' },
    'g+o': { action: 'goToOrders', description: 'Go to Orders', category: 'Navigation' },
    'g+p': { action: 'goToProducts', description: 'Go to Products', category: 'Navigation' },
    'g+c': { action: 'goToComments', description: 'Go to Comments', category: 'Navigation' },
    'g+m': { action: 'goToMedia', description: 'Go to Media', category: 'Navigation' },
  };

  // Track sequence for combo keys (like 'g+a')
  let keySequence = '';
  let sequenceTimer = null;

  // Check if user is on Mac
  const isMac = navigator.platform.toUpperCase().indexOf('MAC') >= 0;

  /**
   * Initialize keyboard shortcuts
   */
  function init() {
    document.addEventListener('keydown', handleKeyDown);
    createHelpModal();
  }

  /**
   * Handle keydown events
   */
  function handleKeyDown(e) {
    // Don't trigger shortcuts when typing in input fields, textareas, or contenteditable elements
    const activeElement = document.activeElement;
    const isInputField = activeElement && (
      activeElement.tagName === 'INPUT' ||
      activeElement.tagName === 'TEXTAREA' ||
      activeElement.tagName === 'SELECT' ||
      activeElement.isContentEditable
    );

    // Allow '/' and 'Escape' even in input fields
    if (isInputField && e.key !== '/' && e.key !== 'Escape') {
      return;
    }

    // Build key combination string
    let keyCombo = '';

    if (e.ctrlKey || e.metaKey) {
      keyCombo += (isMac && e.metaKey) ? 'Meta+' : 'Ctrl+';
    }
    if (e.altKey) keyCombo += 'Alt+';
    if (e.shiftKey && e.key.length > 1) keyCombo += 'Shift+'; // Only for special keys

    keyCombo += e.key;

    // Handle sequence shortcuts (like 'g+a')
    if (!e.ctrlKey && !e.metaKey && !e.altKey) {
      if (keySequence) {
        keyCombo = keySequence + '+' + e.key;
        clearTimeout(sequenceTimer);
        keySequence = '';
      } else if (e.key === 'g') {
        keySequence = 'g';
        sequenceTimer = setTimeout(() => { keySequence = ''; }, 1000);
        e.preventDefault();
        return;
      }
    }

    // Find matching shortcut
    let shortcut = shortcuts[keyCombo];

    // Check for Mac-specific key variant
    if (!shortcut && isMac && keyCombo.startsWith('Meta+')) {
      const ctrlVariant = keyCombo.replace('Meta+', 'Ctrl+');
      shortcut = shortcuts[ctrlVariant];
    }

    if (shortcut) {
      e.preventDefault();
      executeAction(shortcut.action);
    }
  }

  /**
   * Execute keyboard shortcut action
   */
  function executeAction(action) {
    switch (action) {
      case 'showHelp':
        showHelpModal();
        break;

      case 'focusSearch':
        focusSearchBox();
        break;

      case 'closeModal':
        closeModals();
        break;

      case 'createNew':
        createNew();
        break;

      case 'saveForm':
        saveForm();
        break;

      case 'toggleEdit':
        toggleEdit();
        break;

      case 'goToArticles':
        window.location.href = '/admin/articles';
        break;

      case 'goToUsers':
        window.location.href = '/admin/users';
        break;

      case 'goToOrders':
        window.location.href = '/admin/orders';
        break;

      case 'goToProducts':
        window.location.href = '/admin/products';
        break;

      case 'goToComments':
        window.location.href = '/admin/comments';
        break;

      case 'goToMedia':
        window.location.href = '/admin/media';
        break;
    }
  }

  /**
   * Focus the search box on current page
   */
  function focusSearchBox() {
    const searchInput = document.querySelector('input[type="search"], input[name="search"], input[placeholder*="Search"], input[placeholder*="Hledat"]');
    if (searchInput) {
      searchInput.focus();
      searchInput.select();
    }
  }

  /**
   * Close any open modals
   */
  function closeModals() {
    const helpModal = document.getElementById('keyboardShortcutsModal');
    if (helpModal && helpModal.style.display === 'flex') {
      helpModal.style.display = 'none';
      return;
    }

    // Close Bootstrap modals
    const openModals = document.querySelectorAll('.modal.show');
    openModals.forEach(modal => {
      const bsModal = bootstrap.Modal.getInstance(modal);
      if (bsModal) {
        bsModal.hide();
      }
    });
  }

  /**
   * Create new item (detect current page context)
   */
  function createNew() {
    const path = window.location.pathname;
    if (path.includes('/articles')) {
      window.location.href = '/admin/articles/new';
    } else if (path.includes('/users')) {
      window.location.href = '/admin/users/new';
    } else if (path.includes('/orders')) {
      window.location.href = '/admin/orders/new';
    } else if (path.includes('/products')) {
      window.location.href = '/admin/products/new';
    } else if (path.includes('/categories')) {
      window.location.href = '/admin/categories/new';
    } else if (path.includes('/comments')) {
      window.location.href = '/admin/comments/new';
    }
  }

  /**
   * Save current form
   */
  function saveForm() {
    const saveButton = document.querySelector('button[type="submit"]:not([name="delete"]), input[type="submit"]');
    if (saveButton) {
      saveButton.click();
    }
  }

  /**
   * Toggle edit mode (context-dependent)
   */
  function toggleEdit() {
    const editLink = document.querySelector('a[href*="/edit"]');
    if (editLink) {
      editLink.click();
    }
  }

  /**
   * Create the help modal
   */
  function createHelpModal() {
    const modal = document.createElement('div');
    modal.id = 'keyboardShortcutsModal';
    modal.style.cssText = `
      display: none;
      position: fixed;
      z-index: 9999;
      left: 0;
      top: 0;
      width: 100%;
      height: 100%;
      overflow: auto;
      background-color: rgba(0,0,0,0.6);
      align-items: center;
      justify-content: center;
    `;

    // Group shortcuts by category
    const categorized = {};
    Object.entries(shortcuts).forEach(([key, shortcut]) => {
      if (!categorized[shortcut.category]) {
        categorized[shortcut.category] = [];
      }
      const displayKey = (isMac && shortcut.macKey) ? shortcut.macKey.replace('Meta', 'Cmd') : key.replace('Meta', 'Cmd');
      categorized[shortcut.category].push({ key: displayKey, description: shortcut.description });
    });

    let shortcutsHTML = '';
    Object.entries(categorized).forEach(([category, shortcuts]) => {
      shortcutsHTML += `
        <div class="mb-4">
          <h5 class="text-primary mb-3">${category}</h5>
          <div class="row">
            ${shortcuts.map(s => `
              <div class="col-md-6 mb-2">
                <div class="d-flex justify-content-between align-items-center">
                  <span class="text-muted">${s.description}</span>
                  <kbd class="bg-dark text-white px-2 py-1 rounded" style="font-size: 0.9em;">${s.key}</kbd>
                </div>
              </div>
            `).join('')}
          </div>
        </div>
      `;
    });

    modal.innerHTML = `
      <div class="bg-white rounded shadow-lg" style="max-width: 800px; width: 90%; max-height: 90vh; overflow-y: auto; padding: 2rem;">
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h3 class="mb-0">
            <i class="fas fa-keyboard me-2"></i>
            Keyboard Shortcuts
          </h3>
          <button class="btn btn-sm btn-outline-secondary" onclick="document.getElementById('keyboardShortcutsModal').style.display='none'">
            <i class="fas fa-times"></i> Close
          </button>
        </div>
        ${shortcutsHTML}
        <div class="text-muted text-center mt-4 pt-3 border-top">
          <small>Press <kbd>?</kbd> anytime to show this help</small>
        </div>
      </div>
    `;

    document.body.appendChild(modal);

    // Close on outside click
    modal.addEventListener('click', function(e) {
      if (e.target === modal) {
        modal.style.display = 'none';
      }
    });
  }

  /**
   * Show the help modal
   */
  function showHelpModal() {
    const modal = document.getElementById('keyboardShortcutsModal');
    if (modal) {
      modal.style.display = 'flex';
    }
  }

  // Initialize when DOM is ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }

})();
