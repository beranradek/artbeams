<#macro commentList title>
<#if comments?size gt 0>
  <div class="comments">
    <h2><span data-i18n-key="comments.heading">Komentáře</span> (${comments?size})</h2>
    <#list comments as comment>
      <div class="comment">
        <div class="comment-meta">${comment.created?string["d.M.yyyy, HH:mm"]}</div>
        <div class="comment-author"><cite>${comment.userName}</cite> <span data-i18n-key="comments.author.wrote">napsal(a):</span></div>
        <div class="comment-body">${comment.comment}</div>
      </div>
    </#list>
  </div>
</#if>
</#macro>
