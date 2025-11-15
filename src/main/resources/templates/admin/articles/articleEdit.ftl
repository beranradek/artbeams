<#import "/adminLayout.ftl" as layout>
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
  <div class="form-group row">
    <label for="${fields.validFrom.elementId}" class="col-form-label col-sm-2 label-fix">Valid from</label>
    <div class="col-sm-3">
        <input type="text" name="${fields.validFrom.name}" value="${fields.validFrom.value}" id="${fields.validFrom.elementId}" class="form-control"/>
    </div>
  </div>
  <div class="form-group row">
    <label for="${fields.validTo.elementId}" class="col-form-label col-sm-2 label-fix">Valid to</label>
    <div class="col-sm-3">
        <input type="text" name="${fields.validTo.name}" value="${fields.validTo.value!}" id="${fields.validTo.elementId}" class="form-control"/>
    </div>
  </div>
  <div class="form-group row">
    <label for="${fields.keywords.elementId}" class="col-form-label col-sm-2 label-fix">Keywords</label>
    <div class="col-sm-3">
        <input type="text" name="${fields.keywords.name}" value="${fields.keywords.value!}" id="${fields.keywords.elementId}" class="form-control"/>
    </div>
  </div>
  <div class="form-group row">
    <label for="${fields.showOnBlog.elementId}" class="col-form-label col-sm-2 label-fix">Show on blog</label>
    <div class="col-sm-2">
        <input type="text" name="${fields.showOnBlog.name}" value="${fields.showOnBlog.value!}" id="${fields.showOnBlog.elementId}" class="form-control"/>
    </div>
  </div>
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
</script>
</@layout.page>
