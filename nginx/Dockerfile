FROM nginx

RUN apt-get update && \
    apt-get install -y openssl gettext-base && \
    rm -rf /var/lib/apt/lists/*

RUN rm -f /etc/nginx/conf.d/default.conf

COPY nginx.conf.template /etc/nginx/templates/nginx.conf.template

COPY certs/openssl.cnf.template /etc/nginx/certs/openssl.cnf.template

COPY entrypoint.sh /docker-entrypoint.d/40-generate-certs.sh

RUN chmod +x /docker-entrypoint.d/40-generate-certs.sh