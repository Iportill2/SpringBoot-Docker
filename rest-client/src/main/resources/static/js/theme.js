(function () {
  var KEY = "theme";
  var DARK = "dark";
  var LIGHT = "light";

  function current() {
    return document.documentElement.getAttribute("data-theme") === DARK
      ? DARK
      : LIGHT;
  }

  function apply(theme) {
    document.documentElement.setAttribute("data-theme", theme);
    try {
      localStorage.setItem(KEY, theme);
    } catch (e) {}
    updateButtons(theme);
  }

  function toggle() {
    apply(current() === DARK ? LIGHT : DARK);
  }

  function updateButtons(theme) {
    var dark = theme === DARK;
    document.querySelectorAll(".theme-toggle").forEach(function (btn) {
      var on = btn.querySelector(".theme-toggle-icon");
      if (on) {
        on.textContent = dark ? "\u2600\uFE0F" : "\uD83C\uDF19";
      }
      btn.setAttribute("aria-pressed", dark ? "true" : "false");
      btn.setAttribute("title", dark ? "Cambiar a tema claro" : "Cambiar a tema oscuro");
    });
  }

  window.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".theme-toggle").forEach(function (btn) {
      btn.addEventListener("click", toggle);
    });
    updateButtons(current());
  });

  window.Theme = { apply: apply, toggle: toggle };
})();
