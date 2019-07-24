# Practica 6: probando Heartbeat

En esta práctica vamos a probar el servicio Heart, para ello vamos a monitorizar el estado de ElasticSearch.

## Ejercicio 1. Lanzando el compose.

En este ejercicio vamos a explorar el compose y lo vamos a ejecutar para entender que estamos haciendo en el ejercicio.

1. Abrimos el fichero `docker-compose.yml` con el comando vim `docker-compose.yml`.

```yaml
vversion: '3'
services:
  es-pract6:
    image: docker.elastic.co/elasticsearch/elasticsearch-oss:7.2.0
    container_name: es-pract6
    environment:
      - discovery.type=single-node
    ulimits:
      memlock:
        soft: -1
        hard: -1
    volumes:
      - es-data5:/usr/share/elasticsearch/data
    ports:
      - 9200:9200
  heartbeat-pract6:
    user: root
    image: docker.elastic.co/beats/heartbeat-oss:7.2.0
    container_name: heartbeat-pract6
    volumes:
      - ./heartbeat.yml:/usr/share/heartbeat/heartbeat.yml
  kibana-pract6:
    image: docker.elastic.co/kibana/kibana-oss:7.2.0
    environment:
      ELASTICSEARCH_URL: http://es-pract6:9200
    ports:
      - 5601:5601
volumes:
  es-data5:
    driver: local
```

2. Como podemos ver arrancamos tres servicios: ElasticSearch, Heartbeat y Kibana.
3. ElasticSearch se levanta de la forma habitual.
4. Kibana se asocia al ElasticSearch ya levantado.
5. Filebeat tiene un punto de montaje `heartbeat.yml`.
6. Vamos a abrir el fichero `heartbeat.yml`.

```yaml
heartbeat.monitors:
- type: http
  schedule: '@every 5s'
  urls: ["http://es-pract6:9200/service/status"]
  check.request:
    method: "GET"
  check.response:
    status: 200
output.elasticsearch:
  hosts: ["es-pract6:9200"]
```

7. Este fichero contiene la configuración de heartbeat.
8. Modificamos los permisos del fichero `hearbeat.yml` 

```bash
sudo chown root hearbeat.yml
```

7. Vamos a arrancar la composición con el comando `docker-compose up`.

## Ejercicio 2. Explorando datos con Kibana.

Vamos a ver cómo podemos explorar los datos capturado por ElasticSearch a través de Kibana.

1. En un navegador abrimos la URL http://localhost:5601
2. En el navegador aparecerá el dashboard de Kibana, hacemos click en discovery.
3. Nos aparecerá el menú de crear un índex pattern.
4. Ponemos el texto `heartbeat` y damos a `Next step`.
5. En el menú seleccionamos `@timestamp`como unidad de tiempo y pulsamos `Create index pattern`.
6. Volvemos a pulsa en el menú discovery.
7. Ahora podemos explorar los datos generados por Heartbeat.
8. **Tarea:** Prueba las diferentes visualizaciones de Kibana.