FROM iker-nginx:latest

COPY nginx.conf.template /etc/nginx/templates/nginx.conf.template

COPY certs/openssl.cnf.template /etc/nginx/certs/openssl.cnf.template

COPY entrypoint.sh /docker-entrypoint.d/40-generate-certs.sh

RUN chmod +x /docker-entrypoint.d/40-generate-certs.sh
