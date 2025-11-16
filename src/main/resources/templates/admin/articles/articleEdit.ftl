<#import "/adminLayout.ftl" as layout>
<#import "/forms.ftl" as forms>
<@layout.page>
<#if errorMessage??>
  <div class="alert alert-danger" role="alert">${errorMessage}</div>
</#if>

<#assign fields = editForm.fields>

<form action="/admin/articles/save" method="POST" enctype="multipart/form-data" class="form-article-edit">
  <div>
    <h1>${fields.title.value!}</h1>
  </div>
  <input type="hidden" name="${fields.id.name}" value="${fields.id.value!}"/>
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
  <div class="form-group article-basic-attributes">
    <label>Slug
      <input type="text" name="${fields.slug.name}" value="${fields.slug.value!}" size="30"/>
    </label>&nbsp;
    <label>Title
      <input type="text" name="${fields.title.name}" value="${fields.title.value!}" size="30"/>
    </label>&nbsp;
    <label>Image
      <input type="text" name="${fields.image.name}" value="${fields.image.value!}" size="30"/>
      <input type="file" name="${fields.file.name}"/>
    </label>
  </div>

  <div class="form-group">
    <label for="${fields.perex.elementId}">Perex&nbsp;</label>
    <textarea name="${fields.perex.name}" id="${fields.perex.elementId}" rows="2" cols="160" class="max-width-100-percent">${fields.perex.value!}</textarea>
  </div>

  <div class="container">
  <div class="row">
    <div class="col-sm-6">
        <button type="submit" name="save" accesskey="s" class="btn btn-secondary btn-sm align-right">Save</button>
        <div class="align-left"><label for="markdown-content">Article body</label></div>
        <textarea name="${fields.bodyEdited.name}" id="markdown-content" rows="32">${fields.bodyEdited.value!}</textarea>
        <button type="submit" name="save" accesskey="s" class="btn btn-secondary btn-sm align-right">Save</button>
    </div>
    <div class="col-sm-6">
        <#-- Preview of HTML rendered from markdown -->
        <div>
            <a href="https://commonmark.org/help/">CommonMark markdown syntax</a>
            <#if xlat['markdown.examples.url']??>
            | <a href="${xlat['markdown.examples.url']}">${xlat['markdown.examples.title']}</a>
            </#if>
        </div>
        <div id="markdown-output" class="blog-main"></div>
    </div>
  </div>
  </div>

  <div class="form-group row">
    <label for="${fields.editor.elementId}" class="col-form-label col-sm-2 label-fix">Editor</label>
    <div class="col-sm-3">
      <select name="${fields.editor.name}" id="${fields.editor.elementId}" class="form-control">
        <option value="markdown" <#if fields.editor.value == "markdown">selected</#if>>Markdown</option>
        <option value="html" <#if fields.editor.value == "html">selected</#if>>HTML</option>
      </select>
    </div>
  </div>
  <div class="form-group row">
    <label for="${fields.externalId.elementId}" class="col-form-label col-sm-2 label-fix">External ID</label>
    <div class="col-sm-3">
        <input type="text" name="${fields.externalId.name}" value="${fields.externalId.value!}" id="${fields.externalId.elementId}" class="form-control"/>
    </div>
  </div>
  <@forms.inputDateTime field=fields.validFrom label="Valid from" required=true inputDivClass="col-sm-3" />
  <@forms.inputDateTime field=fields.validTo label="Valid to" required=false inputDivClass="col-sm-3" />
  <div class="form-group row">
    <label for="${fields.keywords.elementId}" class="col-form-label col-sm-2 label-fix">Keywords</label>
    <div class="col-sm-3">
        <input type="text" name="${fields.keywords.name}" value="${fields.keywords.value!}" id="${fields.keywords.elementId}" class="form-control"/>
    </div>
  </div>
  <@forms.inputCheckbox field=fields.showOnBlog label="Show on blog" inputDivClass="col-sm-10" />
  <div class="form-group row">
    <label for="${fields.categories.elementId}" class="col-form-label col-sm-2 label-fix">Categories</label>
    <div class="col-sm-2">
        <select name="${fields.categories.name}" multiple size="5" id="${fields.categories.elementId}" class="form-control">
          <#list categories as category>
            <option value="${category.id}"<#if fields.categories.filledObjects?seq_contains(category.id)> selected</#if>>${category.title}</option>
          </#list>
        </select>
    </div>
  </div>
  <div class="form-group row">
    <div class="col-sm-2 col-form-label"></div>
    <div class="col-sm-10">
        <button type="submit" class="btn btn-primary">Save and leave</button>
        <button type="submit" name="save" accesskey="s" class="btn btn-secondary">Save</button>
    </div>
  </div>
</form>

<#-- AI Agent for article editing -->
<#include "/articles/agent/chat.ftl">
<div class="article-agent-icon" onclick="window.ArticleAgent.openChat()" title="AI Asistent pro editaci článků">
    <i class="fas fa-robot"></i>
</div>

<script nonce="${_cspNonce}" src="/static/js/markdown-it.js?v190420"></script>
<#--
markdown-it-attrs is extension/plugin of markdown-it that allows custom styles using attributes such as {.text-box}
Extracted from NPM package markdown-it-attrs-3.0.3.tgz:
-->
<script nonce="${_cspNonce}" src="/static/js/markdown-it-attrs.browser-3.0.3.js?v190420"></script>
<script nonce="${_cspNonce}">
<!-- Document on ready implementation -->
function ready(callback) {
    // in case the document is already rendered
    if (document.readyState!='loading') callback();
    // modern browsers
    else if (document.addEventListener) document.addEventListener('DOMContentLoaded', callback);
    // IE <= 8
    else document.attachEvent('onreadystatechange', function(){
      if (document.readyState=='complete') callback();
    });
}

var md = null;
var editedContentElement = document.getElementById('markdown-content');
function updateResult(md) {
    if (md) {
        if (editedContentElement) {
            var editorSelect = document.getElementById("${fields.editor.elementId}");
            var html = "";
            if (editorSelect && editorSelect.value === "html") {
                html = editedContentElement.value;
            } else {
                html = md.render(editedContentElement.value);
            }
            var markdownOutputElement = document.getElementById('markdown-output');
            if (markdownOutputElement) {
                markdownOutputElement.innerHTML = html;
            }
        }
    }
}

ready(function() {
    markdownItAttrs = window.markdownItAttrs;
    md = window.markdownit().use(markdownItAttrs);
    updateResult(md);
});

var oldVal = "";
function onMarkdownChange() {
    var currentVal = editedContentElement.value;
    if (currentVal == oldVal) {
      return; // check to prevent multiple simultaneous triggers
    }

    oldVal = currentVal;
    // action to be performed on textarea changed
    updateResult(md);
}

if (editedContentElement) {
    editedContentElement.addEventListener('change', onMarkdownChange, false);
    editedContentElement.addEventListener('keyup', onMarkdownChange, false);
    editedContentElement.addEventListener('paste', onMarkdownChange, false);
}

// DateTime format conversion between formio (d.M.yyyy HH:mm) and HTML5 (yyyy-MM-ddTHH:mm)
function convertFormioToDatetimeLocal(formioDate) {
    if (!formioDate) return '';
    // Parse "d.M.yyyy HH:mm" format (e.g., "1.1.2024 14:30")
    var parts = formioDate.match(/(\d+)\.(\d+)\.(\d+)\s+(\d+):(\d+)/);
    if (!parts) return '';

    var day = parts[1].padStart(2, '0');
    var month = parts[2].padStart(2, '0');
    var year = parts[3];
    var hour = parts[4].padStart(2, '0');
    var minute = parts[5].padStart(2, '0');

    return year + '-' + month + '-' + day + 'T' + hour + ':' + minute;
}

function convertDatetimeLocalToFormio(datetimeLocal) {
    if (!datetimeLocal) return '';
    // Parse "yyyy-MM-ddTHH:mm" format
    var parts = datetimeLocal.match(/(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2})/);
    if (!parts) return '';

    var year = parts[1];
    var month = parseInt(parts[2], 10); // Remove leading zero
    var day = parseInt(parts[3], 10);   // Remove leading zero
    var hour = parts[4];
    var minute = parts[5];

    return day + '.' + month + '.' + year + ' ' + hour + ':' + minute;
}

// Function to generate URL-friendly slug from text
function generateSlug(text) {
    if (!text) return '';

    // Mapping of Czech/Slovak characters to ASCII equivalents
    var charMap = {
        'á': 'a', 'Á': 'A', 'č': 'c', 'Č': 'C', 'ď': 'd', 'Ď': 'D',
        'é': 'e', 'É': 'E', 'ě': 'e', 'Ě': 'E', 'í': 'i', 'Í': 'I',
        'ň': 'n', 'Ň': 'N', 'ó': 'o', 'Ó': 'O', 'ř': 'r', 'Ř': 'R',
        'š': 's', 'Š': 'S', 'ť': 't', 'Ť': 'T', 'ú': 'u', 'Ú': 'U',
        'ů': 'u', 'Ů': 'U', 'ý': 'y', 'Ý': 'Y', 'ž': 'z', 'Ž': 'Z',
        'ä': 'a', 'Ä': 'A', 'ľ': 'l', 'Ľ': 'L', 'ĺ': 'l', 'Ĺ': 'L',
        'ô': 'o', 'Ô': 'O', 'ŕ': 'r', 'Ŕ': 'R'
    };

    var slug = text;

    // Replace Czech/Slovak characters with ASCII equivalents
    for (var char in charMap) {
        slug = slug.split(char).join(charMap[char]);
    }

    // Convert to lowercase
    slug = slug.toLowerCase();

    // Replace spaces and underscores with hyphens
    slug = slug.replace(/[\s_]+/g, '-');

    // Remove all non-alphanumeric characters except hyphens
    slug = slug.replace(/[^a-z0-9-]/g, '');

    // Replace multiple consecutive hyphens with single hyphen
    slug = slug.replace(/-+/g, '-');

    // Remove hyphens from start and end
    slug = slug.replace(/^-+|-+$/g, '');

    return slug;
}

// Initialize datetime inputs and checkboxes on page load
document.addEventListener('DOMContentLoaded', function() {
    // Auto-generate slug from title
    var titleInput = document.querySelector('input[name="${fields.title.name}"]');
    var slugInput = document.querySelector('input[name="${fields.slug.name}"]');

    if (titleInput && slugInput) {
        var isSlugManuallyEdited = false;

        // Track if user manually edited the slug
        slugInput.addEventListener('input', function() {
            if (this.value.trim() !== '') {
                isSlugManuallyEdited = true;
            } else {
                isSlugManuallyEdited = false;
            }
        });

        // Auto-generate slug when title changes (only if slug is empty or not manually edited)
        titleInput.addEventListener('input', function() {
            if (!isSlugManuallyEdited && slugInput.value.trim() === '') {
                slugInput.value = generateSlug(this.value);
            }
        });

        titleInput.addEventListener('blur', function() {
            if (!isSlugManuallyEdited && slugInput.value.trim() === '') {
                slugInput.value = generateSlug(this.value);
            }
        });
    }

    // Initialize datetime inputs
    var datetimeInputs = document.querySelectorAll('input[type="datetime-local"]');

    datetimeInputs.forEach(function(input) {
        var originalValue = input.getAttribute('data-original-value');
        if (originalValue) {
            input.value = convertFormioToDatetimeLocal(originalValue);
        }

        // Update hidden field when datetime changes
        input.addEventListener('change', function() {
            var hiddenField = document.getElementById(input.id + '_hidden');
            if (hiddenField) {
                hiddenField.value = convertDatetimeLocalToFormio(input.value);
            }
        });
    });

    // Before form submit, ensure all datetime values are converted
    var form = document.querySelector('.form-article-edit');
    if (form) {
        form.addEventListener('submit', function(e) {
            datetimeInputs.forEach(function(input) {
                var hiddenField = document.getElementById(input.id + '_hidden');
                if (hiddenField && input.value) {
                    hiddenField.value = convertDatetimeLocalToFormio(input.value);
                }
            });
        });
    }

    // Initialize checkbox switches with badge updates
    var checkboxSwitches = document.querySelectorAll('.form-check-input[type="checkbox"]');
    checkboxSwitches.forEach(function(checkbox) {
        checkbox.addEventListener('change', function() {
            var label = this.nextElementSibling;
            if (label && label.classList.contains('form-check-label')) {
                var badge = label.querySelector('.badge');
                if (badge) {
                    if (this.checked) {
                        badge.className = 'badge bg-success';
                        badge.textContent = 'Enabled';
                    } else {
                        badge.className = 'badge bg-secondary';
                        badge.textContent = 'Disabled';
                    }
                }
            }
        });
    });
});
</script>
</@layout.page>
