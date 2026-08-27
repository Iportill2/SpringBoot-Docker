(function () {
  var DARK_CLASS = "dark-mode";
  var KEY = "theme";

  function isDark() {
    return document.documentElement.classList.contains(DARK_CLASS);
  }

  function persist() {
    try {
      localStorage.setItem(KEY, isDark() ? "dark" : "light");
    } catch (e) {}
  }

  function updateButtons() {
    var dark = isDark();
    document.querySelectorAll(".theme-toggle").forEach(function (btn) {
      var on = btn.querySelector(".theme-toggle-icon");
      if (on) {
        on.textContent = dark ? "\u2600\uFE0F" : "\uD83C\uDF19";
      }
      btn.setAttribute("aria-pressed", dark ? "true" : "false");
      btn.setAttribute("title", dark ? "Cambiar a tema claro" : "Cambiar a tema oscuro");
    });
  }

  function apply() {
    updateButtons();
  }

  function toggle() {
    document.documentElement.classList.toggle(DARK_CLASS);
    persist();
    updateButtons();
  }

  function savedTheme() {
    try {
      return localStorage.getItem(KEY);
    } catch (e) {
      return null;
    }
  }

  window.addEventListener("DOMContentLoaded", function () {
    var saved = savedTheme();
    if (saved === "dark" || (saved === null && window.matchMedia && window.matchMedia("(prefers-color-scheme: dark)").matches)) {
      document.documentElement.classList.add(DARK_CLASS);
    }
    document.querySelectorAll(".theme-toggle").forEach(function (btn) {
      btn.addEventListener("click", toggle);
    });
    updateButtons();
  });

  window.Theme = { apply: apply, toggle: toggle };
})();
