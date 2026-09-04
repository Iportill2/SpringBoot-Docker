#!/bin/sh
set -e

if [ ! -d /app ]; then
  mkdir -p /app
fi

# Crear usuario no privilegiado si no existe.
if ! getent passwd appuser >/dev/null 2>&1; then
  groupadd -r appgroup 2>/dev/null || true
  useradd -r -g appgroup -d /app appuser 2>/dev/null || true
fi

# El volumen de backups debe ser escribible por el usuario no root.
chown -R appuser:appgroup /backup 2>/dev/null || true
chown appuser:appgroup /tmp/app.jar 2>/dev/null || true

if command -v runuser >/dev/null 2>&1; then
  exec runuser -u appuser -- java -jar /tmp/app.jar
elif command -v su >/dev/null 2>&1; then
  exec su -s /bin/sh appuser -c "exec java -jar /tmp/app.jar"
elif command -v setpriv >/dev/null 2>&1; then
  UID_APP=$(id -u appuser)
  GID_APP=$(id -g appuser)
  exec setpriv --reuid "$UID_APP" --regid "$GID_APP" --init-groups java -jar /tmp/app.jar
else
  echo "No privilege-drop tool available; running as root" >&2
  exec java -jar /tmp/app.jar
fi
