# Practica 8: probando Kibana

Esta práctica se compone de dos partes, un repaso de metrics, donde vamos a instalar Filebeats, Metricbeats, Hearbeats, ElasticSearch y Kibana. Ademas lanzaremos un servicio NGINX que servirá contenido estático y nos servirá para capturar sus logs. La segunda parte del ejercicio consistirá en usar Kibana para crear un dashboard que nos muestre el estado de nuestra máquina.

## Ejercicio 1. Lanzando el compose.

Lo primero que vamos a hacer es lanzar el compose para ello primero vamos a analizar como está implementado.

1. Abrimos el fichero `docker-compose.yml` con el comando `vim docker-compose.yml`.

```yaml
<< DOCKER-COMPOSE >>
```

2. Como podemos ver arrancamos tres servicios: ElasticSearch, Metricbeat y Kibana.
3. ElasticSearch se levanta de la forma habitual.
4. Kibana se asocia al ElasticSearch ya levantado.
5. Filebeat tiene un punto de montaje `metricbeat.yml`.
6. Vamos a abrir el fichero `metricbeat.yml`.

```yaml
metricbeat.modules:
- module: docker
  metricsets:
    - "container"
    - "cpu"
    - "diskio"
    - "healthcheck"
    - "info"
    - "image"
    - "memory"
    - "network"
  hosts: ["unix:///var/run/docker.sock"]
  period: 10s
  enabled: true
output.elasticsearch:
  hosts: ["es-pract7:9200"]
```

7. Este fichero contiene la configuración de metricbeat
8. Modificamos los permisos del fichero `metricbeat.yml` 

```bash
sudo chown root metricbeat.yml
```

7. Vamos a arrancar la composición con el comando `docker-compose up`.

## Ejercicio 2. Explorando datos con Kibana.

Vamos a ver cómo podemos explorar los datos capturado por ElasticSearch a través de Kibana.

1. En un navegador abrimos la URL http://localhost:5601
2. En el navegador aparecerá el dashboard de Kibana, hacemos click en discovery.
3. Nos aparecerá el menú de crear un índex pattern.
4. Ponemos el texto metricbeat` y damos a `Next step`.
5. En el menú seleccionamos `@timestamp`como unidad de tiempo y pulsamos `Create index pattern`.
6. Volvemos a pulsa en el menú discovery.
7. Ahora podemos explorar los datos generados por metricbeat.
8. **Tarea:** Prueba las diferentes visualizaciones de Kibana.