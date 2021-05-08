<#macro commentAdd>
<#assign fields = commentForm.fields>
<div class="comment-add" id="comment-add">
    <h3 class="comment-add-title">Napsat komentář</h3>
    <form action="/comments" method="post">
      <input type="hidden" name="${fields.id.name}" value="${fields.id.value!}"/>
      <input type="hidden" name="${fields.entityId.name}" value="${fields.entityId.value!}"/>
      <input type="hidden" name="${fields.antispamQuestion.name}" value="${fields.antispamQuestion.value!}"/>
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
      <div class="form-group">
        <label for="${fields.comment.elementId}">Komentář *</label>
        <textarea class="form-control" name="${fields.comment.name}" id="${fields.comment.elementId}" rows="8" cols="45" maxlength="20000" required="required">${fields.comment.value!}</textarea>
      </div>
      <div class="form-group row">
        <label for="${fields.userName.elementId}" class="col-form-label col-sm-2 label-fix">Jméno *</label>
        <div class="col-sm-10">
            <input type="text" class="form-control" id="${fields.userName.elementId}" name="${fields.userName.name}" value="${fields.userName.value!}" size="64" required="required"/>
        </div>
      </div>
      <div class="form-group row">
        <label for="${fields.email.elementId}" class="col-form-label col-sm-2 label-fix">Email *</label>
        <div class="col-sm-10">
            <input type="email" class="form-control" id="${fields.email.elementId}" name="${fields.email.name}" value="${fields.email.value!}" size="64" required="required"/>
        </div>
      </div>
      <div class="form-group row">
        <label style="cursor:help" title="Kontrolní otázka - ochrana proti robotům" for="${fields.antispamAnswer.elementId}" class="col-form-label col-sm-6 label-fix">${fields.antispamQuestion.value!} *</label>
        <div class="col-sm-6">
            <input type="text" class="form-control" id="${fields.antispamAnswer.elementId}" name="${fields.antispamAnswer.name}" value="${fields.antispamAnswer.value!}" size="64" required="required"/>
        </div>
      </div>
      <p class="comment-info">Povinné údaje jsou označeny *. Emailová adresa nebude zveřejněna. Vaše osobní údaje budou použity pouze pro účely zpracování tohoto komentáře. <a href="/ochrana-osobnich-udaju">Zásady zpracování osobních údajů</a>.</p>
      <div class="form-group">
         <button name="submit" type="submit" id="submit" class="btn">Odeslat komentář</button>
      </div>
    </form>
</div>
</#macro>
