(function () {
  "use strict";

  var STORAGE_KEY = "sidebarCollapsed";
  var DESKTOP_MEDIA = "(min-width: 900px)";

  var body = document.body;
  var btn = document.getElementById("sidebarToggle");
  var overlay = document.getElementById("sidebarOverlay");

  if (!btn) {
    return;
  }

  var media = window.matchMedia(DESKTOP_MEDIA);

  function isDesktop() {
    return media.matches;
  }

  function isCollapsed() {
    return body.classList.contains("sidebar-collapsed");
  }

  function saveCollapsed(collapsed) {
    try {
      localStorage.setItem(STORAGE_KEY, collapsed ? "1" : "0");
    } catch (e) {}
  }

  function readSaved() {
    try {
      return localStorage.getItem(STORAGE_KEY) === "1";
    } catch (e) {
      return false;
    }
  }

  function openDrawer() {
    body.classList.add("sidebar-open");
    if (overlay) {
      overlay.hidden = false;
    }
    btn.setAttribute("aria-expanded", "true");
  }

  function closeDrawer() {
    body.classList.remove("sidebar-open");
    if (overlay) {
      overlay.hidden = true;
    }
    btn.setAttribute("aria-expanded", "false");
  }

  function setDesktopCollapsed(collapsed) {
    closeDrawer();
    body.classList.remove("sidebar-open");
    body.classList.toggle("sidebar-collapsed", collapsed);
    saveCollapsed(collapsed);
    btn.setAttribute("aria-expanded", String(!collapsed));
  }

  function applyForMode() {
    if (isDesktop()) {
      setDesktopCollapsed(readSaved());
    } else {
      // En móvil siempre empieza cerrado (drawer).
      body.classList.remove("sidebar-collapsed");
      closeDrawer();
    }
  }

  function onToggle() {
    if (isDesktop()) {
      setDesktopCollapsed(!isCollapsed());
    } else {
      if (body.classList.contains("sidebar-open")) {
        closeDrawer();
      } else {
        openDrawer();
      }
    }
  }

  function onImage(image) {
    if (image.matches) {
      body.classList.remove("sidebar-open");
      if (overlay) {
        overlay.hidden = true;
      }
      setDesktopCollapsed(readSaved());
    } else {
      // Al pasar a móvil, forzar estado cerrado e ignorar lo guardado de desktop.
      body.classList.remove("sidebar-collapsed");
      closeDrawer();
    }
  }

  btn.addEventListener("click", onToggle);

  if (overlay) {
    overlay.addEventListener("click", closeDrawer);
  }

  document.querySelectorAll("#appSidebar nav a").forEach(function (link) {
    link.addEventListener("click", function () {
      if (!isDesktop()) {
        closeDrawer();
      }
    });
  });

  if (media.addEventListener) {
    media.addEventListener("change", onImage);
  } else if (media.addListener) {
    media.addListener(onImage);
  }

  applyForMode();
})();
