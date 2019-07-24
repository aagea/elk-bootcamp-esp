# Practica 7: probando Metricbeat

En esta práctica vamos a probar el servicio Metricbeat, para ello vamos a monitorizar los procesos de Docker.

## Ejercicio 1. Lanzando el compose.

En este ejercicio vamos a explorar el compose y lo vamos a ejecutar para entender que estamos haciendo en el ejercicio.

1. Abrimos el fichero `docker-compose.yml` con el comando vim `docker-compose.yml`.

```yaml
version: '3'
services:
  es-pract7:
    image: docker.elastic.co/elasticsearch/elasticsearch-oss:7.2.0
    container_name: elasticsearch
    environment:
      - discovery.type=single-node
    ulimits:
      memlock:
        soft: -1
        hard: -1
    volumes:
      - es-data7:/usr/share/elasticsearch/data
    ports:
      - 9200:9200
  metricbeat-pract7:
    user: root
    image: docker.elastic.co/beats/metricbeat-oss:7.2.0
    container_name: metricbeat-pract7
    volumes:
      - ./metricbeat.yml:/usr/share/metricbeat/metricbeat.yml
      - /var/run/docker.sock:/var/run/docker.sock
  kibana-pract7:
    image: docker.elastic.co/kibana/kibana-oss:7.2.0
    environment:
      ELASTICSEARCH_URL: http://es-pract7:9200
    ports:
      - 5601:5601
volumes:
  es-data7:
    driver: local
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