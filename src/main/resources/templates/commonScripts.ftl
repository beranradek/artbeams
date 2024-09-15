<script nonce="${_cspNonce}">
    /* Function for running handler on click event on given element. */
    function registerOnClickHandler(elementId, handler) {
        var element = document.getElementById(elementId);
        if (element) {
            if (element.addEventListener) {
                element.addEventListener('click', handler, false);
            } else if (acceptCookie.attachEvent) {
                // this is for IE, because it doesn't support addEventListener
                // this strange part for making the keyword 'this' indicate the clicked anchor:
                element.attachEvent('onclick', function() { return handler.apply(element, [window.event]) });
            }
        }
    }

    /* Document on ready implementation */
    function ready(callback) {
        // in case the document is already rendered
        if (document.readyState != 'loading') callback();
        // modern browsers
        else if (document.addEventListener) document.addEventListener('DOMContentLoaded', callback);
        // IE <= 8
        else document.attachEvent('onreadystatechange', function() {
            if (document.readyState=='complete') callback();
        });
    }

    function handleRecaptchaFormWithClass(formClass) {
        document.querySelector('.' + formClass).addEventListener('submit', function (event) {
          event.preventDefault();
          var formElement = this;
          grecaptcha.ready(function() {
              grecaptcha.execute('${xlat['recaptcha.siteKey']}', {action: 'submit'}).then(function(token) {
                  // Add the reCaptcha token to the form data and submit
                  document.getElementById('g-recaptcha-response').value = token;
                  formElement.submit();
              });
          });
        });
    }

    /*
     * AJAX handling of form submission and updating form content with AJAX response.
     * Form is identified by given CSS class name
     * and must contain sub-element with class name formClass + '-ajax-content' to replace
     * its inner HTML content with htmlContent of AJAX response body.
     */
    function ajaxHandleFormWithClass(formClass, useRecaptcha) {
        document.querySelector('.' + formClass).addEventListener('submit', function (event) {
          event.preventDefault();
          var formElement = this;
          const formData = new FormData(formElement);
          if (useRecaptcha) {
            grecaptcha.ready(function() {
                grecaptcha.execute('${xlat['recaptcha.siteKey']}', {action: 'submit'}).then(function(token) {
                    // Add the reCaptcha token to the form data
                    formData.append('g-recaptcha-response', token);
                    ajaxHandleForm(formElement, formClass, formData);
                });
            });
          } else {
              ajaxHandleForm(formElement, formClass, formData);
          }
        });
    }

    function ajaxHandleForm(formElement, formClass, formData) {
      const searchParams = new URLSearchParams(formData);
      // Submission of form data:
      fetch(formElement.getAttribute('action'), {
          method: formElement.getAttribute('method'),
          body: searchParams
      })
      .then(res => {
        // Parse response from server as JSON
        var responseJson = res.json();
        return responseJson;
      })
      .then(data => {
          if (data.redirectUri) {
            window.location.href = data.redirectUri; // Redirect to the URI if provided
          } else if (data.htmlContent) {
              const ajaxContentElement = document.querySelector('.' + formClass + '-ajax-content');
              if (ajaxContentElement) {
                  ajaxContentElement.innerHTML = data.htmlContent;
              } else {
                console.error('Element to replace with AJAX data was not found.');
              }
          }
      })
      .catch(error => console.error('Error:', error));
    }
</script>
