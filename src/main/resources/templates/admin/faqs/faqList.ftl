<#import "/adminLayout.ftl" as layout>
<@layout.page>
<h1>FAQs: ${entityType} / ${entityId}</h1>

<p>
  <a class="btn btn-secondary btn-sm" href="javascript:history.back()">Back</a>
</p>

<hr class="my-3">

<h3>Add new</h3>
<form action="/admin/faqs/add" method="post" class="form-horizontal">
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
  <input type="hidden" name="entityType" value="${entityType}"/>
  <input type="hidden" name="entityId" value="${entityId}"/>
  <div class="form-group row">
    <label class="col-sm-2 col-form-label">Sort</label>
    <div class="col-sm-2"><input class="form-control" type="number" name="sortOrder" value="0"/></div>
  </div>
  <div class="form-group row">
    <label class="col-sm-2 col-form-label">Question</label>
    <div class="col-sm-8"><input class="form-control" type="text" name="question" value="" maxlength="500" required/></div>
  </div>
  <div class="form-group row">
    <label class="col-sm-2 col-form-label">Answer</label>
    <div class="col-sm-8"><textarea class="form-control" name="answer" rows="4" required></textarea></div>
  </div>
  <div class="form-group row">
    <div class="col-sm-2 col-form-label"></div>
    <div class="col-sm-8"><button class="btn btn-primary" type="submit">Add</button></div>
  </div>
</form>

<hr class="my-4">

<h3>Existing</h3>
<#if entries?has_content>
  <#list entries as e>
    <div class="card mb-3">
      <div class="card-body">
        <form action="/admin/faqs/${e.id}/update" method="post">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
          <input type="hidden" name="entityType" value="${entityType}"/>
          <input type="hidden" name="entityId" value="${entityId}"/>
          <div class="form-group row">
            <label class="col-sm-2 col-form-label">Sort</label>
            <div class="col-sm-2"><input class="form-control" type="number" name="sortOrder" value="${e.sortOrder}"/></div>
          </div>
          <div class="form-group row">
            <label class="col-sm-2 col-form-label">Question</label>
            <div class="col-sm-8"><input class="form-control" type="text" name="question" value="${e.question?html}" maxlength="500" required/></div>
          </div>
          <div class="form-group row">
            <label class="col-sm-2 col-form-label">Answer</label>
            <div class="col-sm-8"><textarea class="form-control" name="answer" rows="4" required>${e.answer?html}</textarea></div>
          </div>
          <div class="form-group row">
            <div class="col-sm-2 col-form-label"></div>
            <div class="col-sm-8">
              <button class="btn btn-primary btn-sm" type="submit">Save</button>
            </div>
          </div>
        </form>

        <form action="/admin/faqs/${e.id}/delete" method="post" onsubmit="return confirm('Delete this FAQ entry?');">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
          <input type="hidden" name="entityType" value="${entityType}"/>
          <input type="hidden" name="entityId" value="${entityId}"/>
          <button class="btn btn-danger btn-sm" type="submit">Delete</button>
        </form>
      </div>
    </div>
  </#list>
<#else>
  <p class="text-muted">No entries yet.</p>
</#if>
</@layout.page>

