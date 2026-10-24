/** Admin UI behaviour: active navigation link, back-to-top button, confirmation of destructive forms. */
(function () {
  'use strict';

  function hrefPath(link) {
    return link.getAttribute('href').split('?')[0];
  }

  // Marks the link with the longest matching path (secondary links exist twice: inline and in the "More" menu)
  function markActiveNavLink() {
    var path = window.location.pathname;
    var links = Array.prototype.slice.call(document.querySelectorAll('.navbar-nav a[href^="/admin"]'));
    var bestPath = '';
    links.forEach(function (link) {
      var href = hrefPath(link);
      if ((path === href || path.indexOf(href + '/') === 0) && href.length > bestPath.length) {
        bestPath = href;
      }
    });
    links.filter(function (link) { return bestPath && hrefPath(link) === bestPath; }).forEach(function (link) {
      link.classList.add('active');
      link.setAttribute('aria-current', 'page');
      var more = link.closest('.nav-more');
      if (more) {
        more.querySelector('.nav-link').classList.add('active');
      }
    });
  }

  function initBackToTop() {
    var button = document.getElementById('admin-to-top');
    if (!button) {
      return;
    }
    function update() {
      button.classList.toggle('visible', window.scrollY > 300);
    }
    window.addEventListener('scroll', update, {passive: true});
    button.addEventListener('click', function () {
      window.scrollTo({top: 0, behavior: 'smooth'});
    });
    update();
  }

  // Forms with data-confirm ask for confirmation before submit (inline handlers are blocked by CSP)
  function initConfirmForms() {
    document.addEventListener('submit', function (ev) {
      var message = ev.target.getAttribute && ev.target.getAttribute('data-confirm');
      if (message && !window.confirm(message)) {
        ev.preventDefault();
      }
    });
  }

  // Selects with the class admin-state-select submit their form on change
  function initAutoSubmitSelects() {
    document.addEventListener('change', function (ev) {
      if (ev.target.classList && ev.target.classList.contains('admin-state-select') && ev.target.form) {
        ev.target.form.submit();
      }
    });
  }

  document.addEventListener('DOMContentLoaded', function () {
    initAutoSubmitSelects();
    markActiveNavLink();
    initBackToTop();
    initConfirmForms();
  });
})();
