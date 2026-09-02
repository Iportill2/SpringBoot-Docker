#!/bin/sh
set -eu

CERTS_DIR=/etc/nginx/certs
OPENSSL_TEMPLATE="$CERTS_DIR/openssl.cnf.template"
OPENSSL_CONF="$CERTS_DIR/openssl.cnf"
NGINX_TEMPLATE=/etc/nginx/templates/nginx.conf.template

if [ -z "${SERVER_IP:-}" ]; then
    echo "ERROR: variable SERVER_IP no definida. Revisa el .env" >&2
    exit 1
fi

echo "Generando openssl.cnf con SERVER_IP=$SERVER_IP"
envsubst '${SERVER_IP}' < "$OPENSSL_TEMPLATE" > "$OPENSSL_CONF"

echo "Generando certificado SSL auto-firmado para $SERVER_IP"
openssl req -x509 -nodes -batch \
    -config "$OPENSSL_CONF" \
    -keyout "$CERTS_DIR/server.key" \
    -out "$CERTS_DIR/server.crt" \
    -days 365

echo "Certificado generado correctamente"