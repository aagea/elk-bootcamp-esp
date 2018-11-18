# Practica 9: probando Logstash

Esta práctica se compone de dos partes, un repaso de metrics, donde vamos a instalar Filebeats, Metricbeats, Hearbeats, ElasticSearch y Kibana. Ademas lanzaremos un servicio NGINX que servirá contenido estático y nos servirá para capturar sus logs. La segunda parte del ejercicio consistirá en usar Kibana para crear un dashboard que nos muestre el estado de nuestra máquina.

## Ejercicio 1. Lanzando el compose.

Lo primero que vamos a hacer es lanzar el compose para ello primero vamos a analizar como está implementado.

1. Abrimos el fichero `docker-compose.yml` con el comando `vim docker-compose.yml`.

```yaml
<< DOCKER_COMPOSE >>
```

2. Como podemos ver arrancamos cinco servicios: 
   - **ElasticSearch,** en el vamos a almacenar todas las métricas de los servicios.
   - **Kibana,** lo utilizamos para visualizar y explorar la información almacenada en ElasticSearch.
   - **Filebeats,** extrae todos los logs generados por los contenedores y por el NGINX.
3. ElasticSearch se levanta de la forma habitual.
4. Kibana se asocia al ElasticSearch ya levantado.
5. Filebeats tiene dos puntos de montaje: para leer los logs de los contenedores y para leer los accesos a NGINX.
6. Vamos a abrir el fichero `filebeat.yml`.

```yaml
filebeat.modules:
- module: nginx
  access:
    var.paths: ["/var/log/nginx/access.log*"]
  error:
    var.paths: ["/var/log/nginx/error.log*"]
filebeat.inputs:
- type: docker
  enabled: true
  containers:
    path: "/var/lib/docker/containers"
    ids:
    - '*'
output.elasticsearch:
  hosts: ["es-pract9:9200"]
```

7. Este fichero contiene la configuración de filebeat.
8. Hemos lanzado un modulo NGINX que captura los logs y los transforma para que los podamos buscar en ElasticSearch.
9. Modificamos los permisos del fichero `filebeat.yml` 

```bash
sudo chown root filebeat.yml
```

10. Lanzamos el compose usando el comando `docker-compose up`.





