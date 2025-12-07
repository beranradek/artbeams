<#import "/loginLikePageLayout.ftl" as layout>
<#import "/forms.ftl" as forms>
<@layout.page>
<#assign fields = passwordSetupForm.fields>
<div class="centered-box password-setup-form">
    <form action="/password-setup" method="post" id="passwordSetupForm">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

      <h2 class="logo">${xlat['member.passwordSetup.header']}</h2>

      <p>${xlat['user']}: ${fields.login.value!}</p>

      <@forms.inputHidden field=fields.login />
      <@forms.inputHidden field=fields.token />
      <@forms.globalMessages messages=passwordSetupForm.validationResult.globalMessages />

      <div class="form-group">
        <@forms.inputText type="password" field=fields.password label="${xlat['password']}" size=60 vertical=true />
        <div id="password-strength-indicator" class="password-strength-indicator" style="display: none; margin-top: 8px;">
          <div class="strength-bar-container">
            <div id="strength-bar" class="strength-bar"></div>
          </div>
          <div id="strength-text" class="strength-text"></div>
          <div id="strength-feedback" class="strength-feedback"></div>
        </div>
      </div>

      <div class="form-group">
        <@forms.inputText type="password" field=fields.password2 label="${xlat['password.again-for-control']}" size=60 vertical=true />
        <div id="password-match-indicator" class="password-match-indicator" style="display: none; margin-top: 8px;"></div>
      </div>

      <@forms.buttonSubmit text="${xlat['passwordSetup.setPassword']}" class="btn btn-primary" id="submitBtn" />
    </form>
</div>

<script src="https://cdn.jsdelivr.net/npm/zxcvbn@4.4.2/dist/zxcvbn.js"></script>
<script>
(function() {
  const passwordInput = document.getElementById('passwordSetup-password');
  const password2Input = document.getElementById('passwordSetup-password2');
  const strengthIndicator = document.getElementById('password-strength-indicator');
  const strengthBar = document.getElementById('strength-bar');
  const strengthText = document.getElementById('strength-text');
  const strengthFeedback = document.getElementById('strength-feedback');
  const matchIndicator = document.getElementById('password-match-indicator');
  const submitBtn = document.getElementById('submitBtn');

  const strengthLabels = ['Velmi slabé', 'Slabé', 'Přijatelné', 'Silné', 'Velmi silné'];
  const strengthColors = ['#dc3545', '#fd7e14', '#ffc107', '#28a745', '#20c997'];

  function updatePasswordStrength() {
    const password = passwordInput.value;

    if (password.length === 0) {
      strengthIndicator.style.display = 'none';
      return;
    }

    strengthIndicator.style.display = 'block';
    const result = zxcvbn(password);
    const score = result.score;

    // Update strength bar
    const strengthPercent = ((score + 1) / 5) * 100;
    strengthBar.style.width = strengthPercent + '%';
    strengthBar.style.backgroundColor = strengthColors[score];

    // Update strength text
    strengthText.textContent = 'Síla hesla: ' + strengthLabels[score];
    strengthText.style.color = strengthColors[score];

    // Update feedback
    let feedback = '';
    if (result.feedback.warning) {
      feedback += '<div class="text-warning"><i class="fas fa-exclamation-triangle"></i> ' + result.feedback.warning + '</div>';
    }
    if (result.feedback.suggestions && result.feedback.suggestions.length > 0) {
      feedback += '<div class="text-info mt-1"><i class="fas fa-info-circle"></i> ' + result.feedback.suggestions.join(' ') + '</div>';
    }
    if (password.length < 8) {
      feedback += '<div class="text-danger mt-1"><i class="fas fa-times-circle"></i> Heslo musí mít alespoň 8 znaků.</div>';
    }
    if (score < 2) {
      feedback += '<div class="text-danger mt-1"><i class="fas fa-times-circle"></i> Heslo je příliš slabé. Použijte kombinaci velkých i malých písmen, číslic a speciálních znaků.</div>';
    }

    strengthFeedback.innerHTML = feedback;

    updateSubmitButton();
  }

  function updatePasswordMatch() {
    const password = passwordInput.value;
    const password2 = password2Input.value;

    if (password2.length === 0) {
      matchIndicator.style.display = 'none';
      return;
    }

    matchIndicator.style.display = 'block';

    if (password === password2) {
      matchIndicator.innerHTML = '<div class="text-success"><i class="fas fa-check-circle"></i> Hesla se shodují</div>';
    } else {
      matchIndicator.innerHTML = '<div class="text-danger"><i class="fas fa-times-circle"></i> Hesla se neshodují</div>';
    }

    updateSubmitButton();
  }

  function updateSubmitButton() {
    const password = passwordInput.value;
    const password2 = password2Input.value;
    const result = password.length > 0 ? zxcvbn(password) : null;

    // Disable submit if password is too weak or passwords don't match
    if (password.length > 0 && (password.length < 8 || (result && result.score < 2) || password !== password2)) {
      submitBtn.disabled = true;
      submitBtn.classList.add('btn-secondary');
      submitBtn.classList.remove('btn-primary');
    } else {
      submitBtn.disabled = false;
      submitBtn.classList.remove('btn-secondary');
      submitBtn.classList.add('btn-primary');
    }
  }

  if (passwordInput) {
    passwordInput.addEventListener('input', function() {
      updatePasswordStrength();
      updatePasswordMatch();
    });
  }

  if (password2Input) {
    password2Input.addEventListener('input', updatePasswordMatch);
  }

  // Initial check
  updatePasswordStrength();
  updatePasswordMatch();
})();
</script>

<style>
.password-strength-indicator {
  margin-top: 8px;
}

.strength-bar-container {
  width: 100%;
  height: 8px;
  background-color: #e9ecef;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 8px;
}

.strength-bar {
  height: 100%;
  width: 0;
  transition: width 0.3s ease, background-color 0.3s ease;
  border-radius: 4px;
}

.strength-text {
  font-weight: 600;
  font-size: 14px;
  margin-bottom: 4px;
}

.strength-feedback {
  font-size: 13px;
  line-height: 1.5;
}

.strength-feedback i {
  margin-right: 4px;
}

.password-match-indicator {
  font-size: 14px;
  font-weight: 500;
}

.password-match-indicator i {
  margin-right: 4px;
}
</style>
</@layout.page>
