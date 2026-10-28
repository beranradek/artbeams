<#import "/adminLayout.ftl" as layout><#import "/adminComponents.ftl" as c>
<#macro stepRow step>
  <div class="row g-2 align-items-end mb-2" data-workflow-step>
    <div class="col-md-7">
      <label class="form-label">E-mail template</label>
      <select class="form-select" name="templateId" required>
        <option value="">Choose a template</option>
        <#list templates as template>
          <option value="${template.id?html}" <#if step?is_hash && step.emailTemplateId == template.id>selected</#if>>${template.templateKey?html} — ${template.subject?html}</option>
        </#list>
      </select>
    </div>
    <div class="col-md-3">
      <label class="form-label">Delay (minutes)</label>
      <input class="form-control" type="number" name="delayMinutes" min="0" max="525600" required value="<#if step?is_hash>${step.delayMinutes}</#if>"/>
    </div>
    <div class="col-md-2">
      <button class="btn btn-outline-danger w-100" type="button" data-remove-workflow-step>Remove</button>
    </div>
  </div>
</#macro>
<#macro workflowForm workflow action submitLabel>
  <form action="${action}" method="post" class="card mb-4" data-workflow-form>
    <div class="card-body">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
      <div class="row g-2">
        <div class="col-md-5"><label class="form-label">Name</label><input class="form-control" name="name" maxlength="160" required value="<#if workflow?is_hash>${workflow.name?html}</#if>"/></div>
        <div class="col-md-5"><label class="form-label">Product</label><select class="form-select" name="productId" required><option value="">Choose a product</option><#list products as product><option value="${product.id?html}" <#if workflow?is_hash && workflow.productId == product.id>selected</#if>>${product.title?html}</option></#list></select></div>
        <div class="col-md-2 d-flex align-items-end"><div class="form-check mb-2"><input class="form-check-input" type="checkbox" name="active" value="true" <#if !workflow?is_hash || workflow.active>checked</#if>/><label class="form-check-label">Active</label></div></div>
      </div>
      <h3 class="h5 mt-3">Steps</h3>
      <div data-workflow-step-list>
        <#if workflow?is_hash><#list workflow.steps as step><@stepRow step=step/></#list><#else><@stepRow step=""/></#if>
      </div>
      <button class="btn btn-outline-secondary btn-sm" type="button" data-add-workflow-step>Add step</button>
      <button class="btn btn-primary" type="submit">${submitLabel}</button>
    </div>
  </form>
</#macro>
<@layout.page>
  <@c.pageHeader title="E-mail workflows"/>
  <p class="text-muted">A workflow is started once after a product is acquired. Each step is sent after its delay from the acquisition time. Available variables: <code>{{firstName}}</code>, <code>{{lastName}}</code>, <code>{{fullName}}</code>, <code>{{email}}</code>, <code>{{productTitle}}</code>.</p>
  <#if templates?size == 0><div class="alert alert-warning">Create a non-system e-mail template before creating a workflow.</div></#if>
  <#list workflows as workflow>
    <h2 class="h4">${workflow.name?html}</h2>
    <@workflowForm workflow=workflow action="/admin/email-workflows/${workflow.id}/update" submitLabel="Save workflow"/>
    <form class="mb-4" action="/admin/email-workflows/${workflow.id}/delete" method="post" data-confirm="Delete this workflow? Workflows that have already started can only be deactivated."><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/><button class="btn btn-outline-danger btn-sm">Delete workflow</button></form>
  <#else><p>No e-mail workflows yet.</p></#list>
  <h2 class="h4 mt-4">Add workflow</h2>
  <@workflowForm workflow="" action="/admin/email-workflows/add" submitLabel="Add workflow"/>
  <template id="workflow-step-template"><@stepRow step=""/></template>
  <script src="/static/js/email-workflows.js" defer></script>
</@layout.page>
