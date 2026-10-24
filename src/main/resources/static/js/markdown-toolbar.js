/**
 * Markdown formatting toolbar for a textarea (CommonMark + markdown-it-attrs syntax).
 * Usage: MarkdownToolbar.init(textareaElement, toolbarContainerElement, onChangeCallback)
 */
(function () {
    'use strict';

    var DEFAULT_IMAGE_URL = '/media/image.jpg';

    // Replaces range [start, end) of textarea by text; uses execCommand to keep native undo (Ctrl+Z) working.
    function replaceRange(ta, start, end, text, selStart, selEnd) {
        ta.focus();
        ta.setSelectionRange(start, end);
        var done = false;
        try {
            done = document.execCommand('insertText', false, text);
        } catch (e) {
            done = false;
        }
        if (!done || ta.value.substr(start, text.length) !== text) {
            ta.value = ta.value.substring(0, start) + text + ta.value.substring(end);
            ta.dispatchEvent(new Event('input', {bubbles: true}));
        }
        ta.setSelectionRange(selStart, selEnd);
    }

    function lineStart(value, pos) {
        return value.lastIndexOf('\n', pos - 1) + 1;
    }

    function lineEnd(value, pos) {
        var i = value.indexOf('\n', pos);
        return i === -1 ? value.length : i;
    }

    // Wraps selection with prefix/suffix; unwraps if already wrapped. Without selection inserts placeholder.
    function wrapInline(ta, prefix, suffix, placeholder) {
        var v = ta.value, s = ta.selectionStart, e = ta.selectionEnd;
        if (s >= prefix.length && v.substr(s - prefix.length, prefix.length) === prefix && v.substr(e, suffix.length) === suffix) {
            replaceRange(ta, s - prefix.length, e + suffix.length, v.substring(s, e), s - prefix.length, e - prefix.length);
            return;
        }
        var sel = v.substring(s, e) || placeholder;
        replaceRange(ta, s, e, prefix + sel + suffix, s + prefix.length, s + prefix.length + sel.length);
    }

    // Applies prefix function to every line touched by selection (toggles when all lines already have the prefix).
    function prefixLines(ta, prefixFn, detectRegex) {
        var v = ta.value;
        var start = lineStart(v, ta.selectionStart);
        var end = lineEnd(v, ta.selectionEnd);
        var lines = v.substring(start, end).split('\n');
        var allPrefixed = lines.every(function (l) { return detectRegex.test(l); });
        var out = lines.map(function (l, i) {
            return allPrefixed ? l.replace(detectRegex, '') : prefixFn(l.replace(detectRegex, ''), i);
        }).join('\n');
        replaceRange(ta, start, end, out, start, start + out.length);
    }

    // Inserts a standalone block separated from surrounding text by blank lines.
    function insertBlock(ta, block, selOffsetStart, selOffsetEnd) {
        var v = ta.value, s = ta.selectionStart, e = ta.selectionEnd;
        var before = v.substring(0, s), after = v.substring(e);
        var lead = before.length === 0 || /\n\n$/.test(before) ? '' : (/\n$/.test(before) ? '\n' : '\n\n');
        var trail = after.length === 0 || /^\n\n/.test(after) ? '' : (/^\n/.test(after) ? '\n' : '\n\n');
        var base = s + lead.length;
        replaceRange(ta, s, e, lead + block + trail, base + selOffsetStart, base + selOffsetEnd);
    }

    function selectedOr(ta, placeholder) {
        var sel = ta.value.substring(ta.selectionStart, ta.selectionEnd);
        return sel || placeholder;
    }

    // Wraps the selected paragraph(s) as one paragraph with trailing attribute line, e.g. "{.text-box}".
    function textBox(ta, cssClass) {
        var text = selectedOr(ta, 'Text of the box').replace(/\n+/g, ' ').trim();
        var block = text + '\n{.' + cssClass + '}';
        insertBlock(ta, block, 0, text.length);
    }

    function heading(ta, level) {
        var hashes = new Array(level + 1).join('#') + ' ';
        var v = ta.value;
        var start = lineStart(v, ta.selectionStart);
        var end = lineEnd(v, ta.selectionStart);
        var line = v.substring(start, end).replace(/^#{1,6}\s+/, '');
        var current = v.substring(start, end).match(/^#{1,6}\s+/);
        var replacement = current && current[0] === hashes ? line : hashes + (line || 'Heading ' + level);
        replaceRange(ta, start, end, replacement, start + (replacement.length - line.length), start + replacement.length);
    }

    function link(ta) {
        var s = ta.selectionStart, e = ta.selectionEnd;
        var text = ta.value.substring(s, e);
        if (/^https?:\/\//.test(text)) {
            replaceRange(ta, s, e, '[link text](' + text + ')', s + 1, s + 10);
        } else {
            var label = text || 'link text';
            replaceRange(ta, s, e, '[' + label + '](https://)', s + label.length + 3, s + label.length + 11);
        }
    }

    function codeBlock(ta) {
        var text = selectedOr(ta, 'code');
        var block = '```\n' + text + '\n```';
        insertBlock(ta, block, 4, 4 + text.length);
    }

    function orderedList(ta) {
        prefixLines(ta, function (l, i) { return (i + 1) + '. ' + l; }, /^\d+\.\s/);
    }

    function table(ta) {
        var block = '| Header 1 | Header 2 | Header 3 |\n| --- | --- | --- |\n| Cell 1 | Cell 2 | Cell 3 |\n| Cell 4 | Cell 5 | Cell 6 |';
        insertBlock(ta, block, 2, 10);
    }

    function image(ta, variant) {
        var alt = selectedOr(ta, 'Image description');
        var block;
        if (variant === 'center') {
            block = '![' + alt + '](' + DEFAULT_IMAGE_URL + '#center "' + alt + '")\n' + alt + '\n{.centered}';
        } else if (variant === 'plain') {
            block = '![' + alt + '](' + DEFAULT_IMAGE_URL + ')';
        } else {
            block = '![' + alt + '](' + DEFAULT_IMAGE_URL + '#' + variant + ' "' + alt + '")';
        }
        // Select the URL so it can be typed over immediately
        insertBlock(ta, block, alt.length + 4, alt.length + 4 + DEFAULT_IMAGE_URL.length);
    }

    var ACTIONS = {
        h1: function (ta) { heading(ta, 1); },
        h2: function (ta) { heading(ta, 2); },
        h3: function (ta) { heading(ta, 3); },
        h4: function (ta) { heading(ta, 4); },
        h5: function (ta) { heading(ta, 5); },
        bold: function (ta) { wrapInline(ta, '**', '**', 'bold text'); },
        italic: function (ta) { wrapInline(ta, '*', '*', 'italic text'); },
        quote: function (ta) { prefixLines(ta, function (l) { return '> ' + l; }, /^>\s?/); },
        code: function (ta) { wrapInline(ta, '`', '`', 'code'); },
        codeblock: codeBlock,
        link: link,
        ul: function (ta) { prefixLines(ta, function (l) { return '* ' + l; }, /^[*-]\s/); },
        ol: orderedList,
        hr: function (ta) { insertBlock(ta, '---', 3, 3); },
        table: table,
        'box-text-box': function (ta) { textBox(ta, 'text-box'); },
        'box-text-box-bordered': function (ta) { textBox(ta, 'text-box-bordered'); },
        'box-text-box-bordered-only': function (ta) { textBox(ta, 'text-box-bordered-only'); },
        'img-center': function (ta) { image(ta, 'center'); },
        'img-plain': function (ta) { image(ta, 'plain'); },
        'img-left': function (ta) { image(ta, 'left'); },
        'img-right': function (ta) { image(ta, 'right'); }
    };

    function btn(action, icon, title, label) {
        return '<button type="button" class="md-tb-btn" data-md-action="' + action + '" title="' + title + '" aria-label="' + title + '">' +
            (icon ? '<i class="' + icon + '"></i>' : '') + (label ? '<span>' + label + '</span>' : '') + '</button>';
    }

    function menuItem(action, label) {
        return '<button type="button" class="md-tb-item" data-md-action="' + action + '">' + label + '</button>';
    }

    function dropdown(icon, title, items) {
        return '<div class="md-tb-dropdown">' +
            '<button type="button" class="md-tb-btn md-tb-toggle" title="' + title + '" aria-label="' + title + '" aria-haspopup="true" aria-expanded="false">' +
            '<i class="' + icon + '"></i><i class="fas fa-caret-down md-tb-caret"></i></button>' +
            '<div class="md-tb-menu" role="menu">' + items.join('') + '</div></div>';
    }

    function sep() { return '<span class="md-tb-sep"></span>'; }

    function buildHtml() {
        return [
            dropdown('fas fa-heading', 'Heading', [
                menuItem('h1', '<b>Heading 1</b>'), menuItem('h2', '<b>Heading 2</b>'), menuItem('h3', '<b>Heading 3</b>'),
                menuItem('h4', '<b>Heading 4</b>'), menuItem('h5', '<b>Heading 5</b>')]),
            btn('bold', 'fas fa-bold', 'Bold (Ctrl+B)'),
            btn('italic', 'fas fa-italic', 'Italic (Ctrl+I)'),
            btn('quote', 'fas fa-quote-right', 'Quote'),
            btn('code', 'fas fa-code', 'Inline code'),
            btn('codeblock', 'fas fa-file-code', 'Code block'),
            btn('link', 'fas fa-link', 'Link (Ctrl+K)'),
            sep(),
            btn('ul', 'fas fa-list-ul', 'Bulleted list'),
            btn('ol', 'fas fa-list-ol', 'Numbered list'),
            btn('hr', 'fas fa-minus', 'Horizontal rule'),
            btn('table', 'fas fa-table', 'Table'),
            sep(),
            dropdown('far fa-square', 'Text box', [
                menuItem('box-text-box', 'Text box'), menuItem('box-text-box-bordered', 'Text box bordered'),
                menuItem('box-text-box-bordered-only', 'Text box bordered only')]),
            dropdown('far fa-image', 'Image', [
                menuItem('img-center', 'Centered image with caption'), menuItem('img-plain', 'Image without caption'),
                menuItem('img-left', 'Image aligned left'), menuItem('img-right', 'Image aligned right')])
        ].join('');
    }

    function closeMenus(container) {
        var open = container.querySelectorAll('.md-tb-dropdown.open');
        for (var i = 0; i < open.length; i++) {
            open[i].classList.remove('open');
            open[i].querySelector('.md-tb-toggle').setAttribute('aria-expanded', 'false');
        }
    }

    function init(textarea, container, onChange) {
        if (!textarea || !container) {
            return;
        }
        container.innerHTML = buildHtml();

        function run(action) {
            var fn = ACTIONS[action];
            if (fn) {
                fn(textarea);
                if (onChange) {
                    onChange();
                }
            }
        }

        // mousedown prevented to keep focus and selection in textarea while clicking toolbar
        container.addEventListener('mousedown', function (ev) {
            if (ev.target.closest('button')) {
                ev.preventDefault();
            }
        });
        container.addEventListener('click', function (ev) {
            var button = ev.target.closest('button');
            if (!button) {
                return;
            }
            if (button.classList.contains('md-tb-toggle')) {
                var dd = button.parentNode;
                var wasOpen = dd.classList.contains('open');
                closeMenus(container);
                if (!wasOpen) {
                    dd.classList.add('open');
                    button.setAttribute('aria-expanded', 'true');
                }
                return;
            }
            closeMenus(container);
            run(button.getAttribute('data-md-action'));
        });
        document.addEventListener('click', function (ev) {
            if (!container.contains(ev.target)) {
                closeMenus(container);
            }
        });
        textarea.addEventListener('keydown', function (ev) {
            if (!(ev.ctrlKey || ev.metaKey) || ev.altKey || ev.shiftKey) {
                return;
            }
            var key = ev.key.toLowerCase();
            var action = key === 'b' ? 'bold' : key === 'i' ? 'italic' : key === 'k' ? 'link' : null;
            if (action) {
                ev.preventDefault();
                run(action);
            }
        });
    }

    window.MarkdownToolbar = {init: init};
})();
