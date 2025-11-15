<#-- Article Editing AI Agent Chat Interface -->
<div class="modal fade" id="articleAgentModal" tabindex="-1" role="dialog" aria-labelledby="articleAgentModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="articleAgentModalLabel">
          <i class="fas fa-robot"></i> AI Asistent pro editaci článků
        </h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Zavřít"></button>
      </div>
      <div class="modal-body">
        <#-- Chat messages container -->
        <div id="agent-chat-messages" class="agent-chat-messages">
          <div class="agent-welcome-message">
            <i class="fas fa-info-circle"></i> Vítejte! Jsem AI asistent pro editaci článků.
            Mohu vám pomoci s tvorbou, úpravou nebo vylepšením vašeho článku.
            Jak vám mohu pomoci?
          </div>
        </div>

        <#-- Error display -->
        <div id="agent-error-message" class="agent-error-message" style="display: none;">
          <i class="fas fa-exclamation-triangle"></i>
          <span id="agent-error-text"></span>
        </div>

        <#-- Loading indicator -->
        <div id="agent-loading" class="agent-loading" style="display: none;">
          <i class="fas fa-circle-notch fa-spin"></i> AI asistent přemýšlí...
        </div>

        <#-- Input area -->
        <div class="agent-input-area">
          <textarea
            id="agent-message-input"
            class="form-control"
            rows="3"
            placeholder="Napište svou zprávu..."
            aria-label="Zpráva pro AI asistenta"></textarea>
          <div class="agent-input-actions">
            <button type="button" id="agent-send-btn" class="btn btn-primary">
              <i class="fas fa-paper-plane"></i> Odeslat
            </button>
            <button type="button" id="agent-clear-btn" class="btn btn-secondary">
              <i class="fas fa-broom"></i> Nová konverzace
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<#-- Diff Viewer Modal -->
<div class="modal fade" id="articleDiffModal" tabindex="-1" role="dialog" aria-labelledby="articleDiffModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-xl" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="articleDiffModalLabel">
          <i class="fas fa-code-compare"></i> Porovnání verzí článku
        </h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Zavřít"></button>
      </div>
      <div class="modal-body">
        <div class="row">
          <div class="col-md-6">
            <h6>Rozdíly (+ přidáno, - odebráno)</h6>
            <pre id="diff-viewer-left" class="diff-viewer" readonly></pre>
          </div>
          <div class="col-md-6">
            <h6>Nová verze článku</h6>
            <textarea id="diff-viewer-right" class="form-control diff-editor" rows="20"></textarea>
            <div class="diff-actions mt-2">
              <button type="button" id="diff-copy-clipboard-btn" class="btn btn-sm btn-secondary">
                <i class="fas fa-clipboard"></i> Zkopírovat do schránky
              </button>
              <button type="button" id="diff-apply-btn" class="btn btn-sm btn-primary">
                <i class="fas fa-check"></i> Použít v editoru
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<#-- Load CSS and JavaScript -->
<link rel="stylesheet" href="/static/css/article-agent.css?v=${.now?long}">
<script nonce="${_cspNonce}" src="/static/js/article-agent.js?v=${.now?long}"></script>
