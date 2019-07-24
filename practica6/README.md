# Practica 4: probando Auditbeat

En esta práctica vamos a probar el servicio Auditbeat, en ese caso vamos a probar únicamente el servicio que chequea la integridad de los directorios.

## Ejercicio 1. Lanzando el compose.

En este ejercicio vamos a explorar el compose y lo vamos a ejecutar para entender que estamos haciendo en el ejercicio.

1. Abrimos el fichero `docker-compose.yml` con el comando vim `docker-compose.yml`.

```yaml
version: '3'
services:
  es-pract4:
    image: docker.elastic.co/elasticsearch/elasticsearch-oss:7.2.0
    container_name: elasticsearch
    environment:
      - discovery.type=single-node
    ulimits:
      memlock:
        soft: -1
        hard: -1
    volumes:
      - es-data4:/usr/share/elasticsearch/data
    ports:
      - 9200:9200
  auditbeat-pract4:
    user: root
    image: docker.elastic.co/beats/auditbeat-oss:7.2.0
    container_name: auditbeat-pract4
    volumes:
      - ./auditbeat.yml:/usr/share/auditbeat/auditbeat.yml
      - ./test:/var/test
  kibana-pract4:
    image: docker.elastic.co/kibana/kibana-oss:7.2.0
    environment:
      ELASTICSEARCH_URL: http://es-pract4:9200
    ports:
      - 5601:5601
volumes:
  es-data4:
    driver: local
```

2. Como podemos ver arrancamos tres servicios: ElasticSearch, Auditbeat y Kibana.
3. ElasticSearch se levanta de la forma habitual.
4. Kibana se asocia al ElasticSearch ya levantado.
5. Auditbeat tiene dos puntos de montaje `auditbeat.yml` y el directorio `test`.
6. Vamos a abrir el fichero `auditbeat.yml`.

```yaml
auditbeat.modules:
- module: file_integrity
  paths:
  - /var/test
  exclude_files:
  - '(?i)\.sw[nop]$'
  - '~$'
  - '/\.git($|/)'
  scan_at_start: true
  scan_rate_per_sec: 50 MiB
  max_file_size: 100 MiB
  hash_types: [sha1]
  recursive: false
output.elasticsearch:
  hosts: ["es-pract4:9200"]
```

7. Este fichero contiene la configuración de auditbeat.
8. Esta configurado para monitorizar el directorio `/var/test`.
9. Este es el directorio que hemos montado en el `docker-compose.yml`
10. Modificamos los permisos del fichero `auditbeat.yml`

```bash
sudo chown root auditbeat.yml
```

11. Vamos a arrancar la composición con el comando `docker-compose up`.

## Ejercicio 2. Explorando datos con Kibana.

Vamos a ver cómo podemos explorar los datos capturado por ElasticSearch a través de Kibana.

1. En un navegador abrimos la URL http://localhost:5601
2. En el navegador aparecerá el dashboard de Kibana, hacemos click en discovery.
3. Nos aparecerá el menú de crear un índex pattern.
4. Ponemos el texto `audit*` y damos a `Next step`.
5. En el menú seleccionamos `@timestamp`como unidad de tiempo y pulsamos `Create index pattern`.
6. Volvemos a pulsa en el menú discovery.
7. Ahora podemos explorar los datos generados por Auditbeat.
8. **Tarea:** Modifica el fichero, crea, nuevos y veras como se generán nuevos eventos.
