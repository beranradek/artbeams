<#import "/layouts/adminLayout.ftl" as layout>
<@layout.page>
<#if errorMessage??>
  <div class="alert alert-danger" role="alert">${errorMessage}</div>
</#if>

<#assign fields = editForm.fields>

<form action="/admin/articles/save" method="post" class="form-horizontal">
  <div>
    <#-- <button type="submit" name="save" accesskey="s" class="btn btn-sm" style="float:right">Save</button> -->
    <h1>${fields.title.value!}</h1>
  </div>
  <input type="hidden" name="${fields.id.name}" value="${fields.id.value!}"/>
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
  <div class="form-group row">
    <label>Slug
      <input type="text" name="${fields.slug.name}" value="${fields.slug.value!}" size="30"/>
    </label>
    <label>Title
      <input type="text" name="${fields.title.name}" value="${fields.title.value!}" size="30"/>
    </label>
    <label>Card image
      <input type="text" name="${fields.image.name}" value="${fields.image.value!}" size="30"/>
    </label>
    <label>Detailed image
      <input type="text" name="${fields.imageDetail.name}" value="${fields.imageDetail.value!}" size="30"/>
    </label>
  </div>

  <div class="form-group row">
    <label for="${fields.perex.elementId}">Perex</label>
    <textarea name="${fields.perex.name}" id="${fields.perex.elementId}" rows="2" cols="160">${fields.perex.value!}</textarea>
  </div>

  <div class="container">
  <div class="row">
    <div class="col-sm-6">
        <button type="submit" name="save" accesskey="s" class="btn btn-sm align-right">Save</button>
        <div class="align-left"><label for="markdown-content">Article body</label></div>
        <textarea name="${fields.bodyMarkdown.name}" id="markdown-content" rows="32">${fields.bodyMarkdown.value!}</textarea>
        <button type="submit" name="save" accesskey="s" class="btn btn-sm align-right">Save</button>
    </div>
    <div class="col-sm-6">
        <#-- Preview of HTML rendered from markdown -->
        <div><a href="https://commonmark.org/help/">CommonMark markdown syntax</a></div>
        <div id="markdown-output" class="blog-main"></div>
    </div>
  </div>
  </div>

  <div class="form-group row">
    <label for="${fields.externalId.elementId}" class="col-form-label col-sm-2 label-fix">External ID</label>
    <div class="col-sm-10">
        <input type="text" name="${fields.externalId.name}" value="${fields.externalId.value}" id="${fields.externalId.elementId}"/>
    </div>
  </div>
  <div class="form-group row">
    <label for="${fields.validFrom.elementId}" class="col-form-label col-sm-2 label-fix">Valid from</label>
    <div class="col-sm-10">
        <input type="text" name="${fields.validFrom.name}" value="${fields.validFrom.value}" id="${fields.validFrom.elementId}"/>
    </div>
  </div>
  <div class="form-group row">
    <label for="${fields.validTo.elementId}" class="col-form-label col-sm-2 label-fix">Valid to</label>
    <div class="col-sm-10">
        <input type="text" name="${fields.validTo.name}" value="${fields.validTo.value}" id="${fields.validTo.elementId}"/>
    </div>
  </div>
  <div class="form-group row">
    <label for="${fields.keywords.elementId}" class="col-form-label col-sm-2 label-fix">Keywords</label>
    <div class="col-sm-10">
        <input type="text" name="${fields.keywords.name}" value="${fields.keywords.value!}" id="${fields.keywords.elementId}"/>
    </div>
  </div>
  <div class="form-group row">
    <label for="${fields.showOnBlog.elementId}" class="col-form-label col-sm-2 label-fix">Show on blog</label>
    <div class="col-sm-10">
        <input type="checkbox" name="${fields.showOnBlog.name}"<#if fields.showOnBlog.filledObject> checked</#if> id="${fields.showOnBlog.elementId}" />
    </div>
  </div>
  <div class="form-group row">
    <label for="${fields.categories.elementId}" class="col-form-label col-sm-2 label-fix">Categories</label>
    <div class="col-sm-10">
        <select name="${fields.categories.name}" multiple size="5" id="${fields.categories.elementId}">
          <#list categories as category>
            <option value="${category.id}"<#if fields.categories.filledObjects?seq_contains(category.id)> selected</#if>>${category.title}</option>
          </#list>
        </select>
    </div>
  </div>
  <div class="form-group">
    <button type="submit" class="btn btn-primary">Save and leave</button>
    <button type="submit" name="save" accesskey="s" class="btn btn-secondary">Save</button>
  </div>
</form>

<script src="/static/js/markdown-it.js?v190420"></script>
<#--
markdown-it-attrs is extension/plugin of markdown-it that allows custom styles using attributes such as {.text-box}
Extracted from NPM package markdown-it-attrs-3.0.3.tgz:
-->
<script src="/static/js/markdown-it-attrs.browser-3.0.3.js?v190420"></script>
<script>
// On page load handler:
var md = null;
document.addEventListener('DOMContentLoaded', function() {
    markdownItAttrs = window.markdownItAttrs;
    md = window.markdownit().use(markdownItAttrs);
    updateResult(md);
}, false);

var oldVal = "";
$('#markdown-content').on("change keyup paste", function() {
    var currentVal = $(this).val();
    if(currentVal == oldVal) {
        return; //check to prevent multiple simultaneous triggers
    }

    oldVal = currentVal;
    // action to be performed on textarea changed
    updateResult(md);
});

function updateResult(md) {
    if (md) {
        var markdown = $('#markdown-content').val();
        var html = md.render(markdown);
        $('#markdown-output').html(html);
    }
}
</script>
</@layout.page>
