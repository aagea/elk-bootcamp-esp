FROM nginx:1.15.6-alpine

COPY dockerenv/nginx.conf /etc/nginx/conf.d/

RUN mkdir -p /var/www/port8080
COPY dockerenv/index8080.html /var/www/port8080/index.html

CMD ["nginx", "-g", "daemon off;"]
