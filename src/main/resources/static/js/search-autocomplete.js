/**
 * Search autocomplete functionality with debouncing.
 * Provides instant suggestions as users type in the search box.
 * @author Radek Beran
 */
(function() {
    'use strict';

    class SearchAutocomplete {
        constructor(inputElement, resultsContainer) {
            this.input = inputElement;
            this.results = resultsContainer;
            this.debounceTimer = null;
            this.currentQuery = '';
            this.selectedIndex = -1;
            this.suggestions = [];

            this.init();
        }

        init() {
            // Handle input events
            this.input.addEventListener('input', (e) => {
                this.handleInput(e.target.value);
            });

            // Handle keyboard navigation
            this.input.addEventListener('keydown', (e) => {
                this.handleKeyboard(e);
            });

            // Hide results when clicking outside
            document.addEventListener('click', (e) => {
                if (!this.input.contains(e.target) && !this.results.contains(e.target)) {
                    this.hideResults();
                }
            });

            // Prevent form submission on Enter if autocomplete is showing
            this.input.form.addEventListener('submit', (e) => {
                if (this.isResultsVisible() && this.selectedIndex >= 0) {
                    e.preventDefault();
                    this.selectSuggestion(this.selectedIndex);
                }
            });
        }

        handleInput(query) {
            clearTimeout(this.debounceTimer);

            if (query.length < 3) {
                this.hideResults();
                return;
            }

            // Debounce: wait 400ms after user stops typing
            this.debounceTimer = setTimeout(() => {
                this.fetchSuggestions(query);
            }, 400);
        }

        handleKeyboard(e) {
            if (!this.isResultsVisible()) {
                return;
            }

            switch(e.key) {
                case 'ArrowDown':
                    e.preventDefault();
                    this.selectNext();
                    break;
                case 'ArrowUp':
                    e.preventDefault();
                    this.selectPrevious();
                    break;
                case 'Escape':
                    e.preventDefault();
                    this.hideResults();
                    break;
                case 'Enter':
                    if (this.selectedIndex >= 0) {
                        e.preventDefault();
                        this.selectSuggestion(this.selectedIndex);
                    }
                    break;
            }
        }

        async fetchSuggestions(query) {
            this.currentQuery = query;

            try {
                const response = await fetch(`/api/search/suggest?query=${encodeURIComponent(query)}`);
                if (!response.ok) {
                    throw new Error('Search request failed');
                }

                const suggestions = await response.json();
                this.suggestions = suggestions;
                this.displaySuggestions(suggestions, query);
            } catch (error) {
                console.error('Search suggestions error:', error);
                this.hideResults();
            }
        }

        displaySuggestions(suggestions, query) {
            if (!suggestions || suggestions.length === 0) {
                this.hideResults();
                return;
            }

            // Group by entity type
            const grouped = {
                ARTICLE: suggestions.filter(s => s.entityType === 'ARTICLE'),
                CATEGORY: suggestions.filter(s => s.entityType === 'CATEGORY'),
                PRODUCT: suggestions.filter(s => s.entityType === 'PRODUCT')
            };

            let html = '<div class="search-suggestions">';

            // Articles
            if (grouped.ARTICLE.length > 0) {
                html += this.renderGroup('Články', grouped.ARTICLE, query);
            }

            // Categories
            if (grouped.CATEGORY.length > 0) {
                html += this.renderGroup('Rubriky', grouped.CATEGORY, query);
            }

            // Products
            if (grouped.PRODUCT.length > 0) {
                html += this.renderGroup('Produkty', grouped.PRODUCT, query);
            }

            // "See all results" link
            html += `<div class="suggestion-footer">
                <a href="/search?query=${encodeURIComponent(query)}" class="btn btn-sm btn-primary">
                    Zobrazit všechny výsledky
                </a>
            </div>`;
            html += '</div>';

            this.results.innerHTML = html;
            this.selectedIndex = -1;
            this.showResults();
        }

        renderGroup(title, items, query) {
            let html = '<div class="suggestion-group">';
            html += `<h6>${title}</h6>`;

            items.forEach((item, index) => {
                html += `<a href="${this.escapeHtml(item.url)}" class="suggestion-item" data-index="${index}">
                    <strong>${this.highlight(this.escapeHtml(item.title), query)}</strong>`;

                if (item.description) {
                    html += `<div class="text-muted small">${this.truncate(this.escapeHtml(item.description), 80)}</div>`;
                }

                html += '</a>';
            });

            html += '</div>';
            return html;
        }

        highlight(text, query) {
            const regex = new RegExp(`(${this.escapeRegex(query)})`, 'gi');
            return text.replace(regex, '<mark>$1</mark>');
        }

        truncate(text, maxLength) {
            return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
        }

        escapeHtml(text) {
            const div = document.createElement('div');
            div.textContent = text;
            return div.innerHTML;
        }

        escapeRegex(text) {
            return text.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
        }

        selectNext() {
            const items = this.results.querySelectorAll('.suggestion-item');
            if (items.length === 0) return;

            this.selectedIndex = Math.min(this.selectedIndex + 1, items.length - 1);
            this.updateSelection(items);
        }

        selectPrevious() {
            const items = this.results.querySelectorAll('.suggestion-item');
            if (items.length === 0) return;

            this.selectedIndex = Math.max(this.selectedIndex - 1, 0);
            this.updateSelection(items);
        }

        updateSelection(items) {
            items.forEach((item, index) => {
                if (index === this.selectedIndex) {
                    item.classList.add('selected');
                    item.scrollIntoView({ block: 'nearest' });
                } else {
                    item.classList.remove('selected');
                }
            });
        }

        selectSuggestion(index) {
            const items = this.results.querySelectorAll('.suggestion-item');
            if (items[index]) {
                window.location.href = items[index].href;
            }
        }

        showResults() {
            this.results.style.display = 'block';
        }

        hideResults() {
            this.results.style.display = 'none';
            this.selectedIndex = -1;
        }

        isResultsVisible() {
            return this.results.style.display === 'block';
        }
    }

    // Initialize autocomplete when DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initializeAutocomplete);
    } else {
        initializeAutocomplete();
    }

    function initializeAutocomplete() {
        const searchInput = document.querySelector('.search-box');
        if (!searchInput) {
            return; // No search box on this page
        }

        // Create results container
        const resultsContainer = document.createElement('div');
        resultsContainer.className = 'search-autocomplete-results';

        // Append to the wrapper div (parent of search input)
        const wrapper = searchInput.closest('.search-input-wrapper');
        if (wrapper) {
            wrapper.appendChild(resultsContainer);
        } else {
            // Fallback to old behavior if wrapper not found
            searchInput.parentElement.appendChild(resultsContainer);
        }

        // Initialize autocomplete
        new SearchAutocomplete(searchInput, resultsContainer);
    }
})();
