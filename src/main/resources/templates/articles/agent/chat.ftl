<#-- Article Editing AI Agent Chat Interface -->
<div class="modal fade" id="articleAgentModal" tabindex="-1" role="dialog" aria-labelledby="articleAgentModalLabel">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="articleAgentModalLabel">
          <i class="fas fa-robot"></i> AI assistant for article editing
        </h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <#-- Chat messages container -->
        <div id="agent-chat-messages" class="agent-chat-messages">
          <div class="agent-welcome-message">
            <i class="fas fa-info-circle"></i> Welcome! I am an AI assistant for article editing.
            I can help you create, edit or improve your article.
            How can I help you?
          </div>
        </div>

        <#-- Error display -->
        <div id="agent-error-message" class="agent-error-message" style="display: none;">
          <i class="fas fa-exclamation-triangle"></i>
          <span id="agent-error-text"></span>
        </div>

        <#-- Loading indicator -->
        <div id="agent-loading" class="agent-loading" style="display: none;">
          <i class="fas fa-circle-notch fa-spin"></i> The AI assistant is thinking...
        </div>

        <#-- Input area -->
        <div class="agent-input-area">
          <textarea
            id="agent-message-input"
            class="form-control"
            rows="3"
            placeholder="Write your message..."
            aria-label="Message for the AI assistant"></textarea>

          <#-- File upload area -->
          <div class="agent-file-upload-area">
            <input
              type="file"
              id="agent-file-input"
              multiple
              accept="image/*"
              style="display: none;"
              aria-label="Upload files">
            <div id="agent-file-list" class="agent-file-list" style="display: none;"></div>
          </div>

          <div class="agent-input-actions">
            <button type="button" id="agent-attach-btn" class="btn btn-outline-secondary" title="Attach images">
              <i class="fas fa-paperclip"></i> Attach images
            </button>
            <button type="button" id="agent-generate-image-btn" class="btn btn-info" title="Generate an image using AI">
              <i class="fas fa-palette"></i> Generate image
            </button>
            <button type="button" id="agent-send-btn" class="btn btn-primary">
              <i class="fas fa-paper-plane"></i> Odeslat
            </button>
            <button type="button" id="agent-clear-btn" class="btn btn-secondary">
              <i class="fas fa-broom"></i> New conversation
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<#-- Diff Viewer Modal -->
<div class="modal fade" id="articleDiffModal" tabindex="-1" role="dialog" aria-labelledby="articleDiffModalLabel">
  <div class="modal-dialog modal-xl" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="articleDiffModalLabel">
          <i class="fas fa-code-compare"></i> Article version comparison
        </h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <div class="row">
          <div class="col-md-6">
            <h6>Differences (+ added, - removed)</h6>
            <pre id="diff-viewer-left" class="diff-viewer" readonly></pre>
          </div>
          <div class="col-md-6">
            <h6>New article version</h6>
            <textarea id="diff-viewer-right" class="form-control diff-editor" rows="20"></textarea>
            <div class="diff-actions mt-2">
              <button type="button" id="diff-copy-clipboard-btn" class="btn btn-sm btn-secondary">
                <i class="fas fa-clipboard"></i> Copy to clipboard
              </button>
              <button type="button" id="diff-apply-btn" class="btn btn-sm btn-primary">
                <i class="fas fa-check"></i> Use in editor
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<#-- Load CSS and JavaScript -->
<link rel="stylesheet" href="/static/css/article-agent.css?v=${.now?long?c}">
<link rel="stylesheet" href="/static/css/image-agent.css?v=${.now?long?c}">
<script nonce="${_cspNonce}" src="/static/js/article-agent.js?v=${.now?long?c}"></script>
<script nonce="${_cspNonce}" src="/static/js/image-agent.js?v=${.now?long?c}"></script>
