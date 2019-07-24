# Practica 5: probando Filebeat

En esta práctica vamos a probar el servicio Filebeat, para ello vamos a probar a capturar todo los logs que genera docker.

## Ejercicio 1. Lanzando el compose.

En este ejercicio vamos a explorar el compose y lo vamos a ejecutar para entender que estamos haciendo en el ejercicio.

1. Abrimos el fichero `docker-compose.yml` con el comando vim `docker-compose.yml`.

```yaml
version: '3'
services:
  es-pract5:
    image: docker.elastic.co/elasticsearch/elasticsearch-oss:7.2.0
    container_name: es-pract5
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
  filebeat-pract5:
    user: root
    image: docker.elastic.co/beats/filebeat-oss:7.2.0
    container_name: filebeat-pract5
    volumes:
      - ./filebeat.yml:/usr/share/filebeat/filebeat.yml
      - /var/snap/docker/common/var-lib-docker/containers:/var/lib/docker/containers
  kibana-pract5:
    image: docker.elastic.co/kibana/kibana-oss:7.2.0
    environment:
      ELASTICSEARCH_URL: http://es-pract5:9200
    ports:
      - 5601:5601
volumes:
  es-data5:
    driver: local

```

2. Como podemos ver arrancamos tres servicios: ElasticSearch, Filebeat y Kibana.
3. ElasticSearch se levanta de la forma habitual.
4. Kibana se asocia al ElasticSearch ya levantado.
5. Filebeat tiene dos puntos de montaje `filebeat.yml` y el directorio donde se almacenan los logs de docker.
6. Vamos a abrir el fichero `filebeat.yml`.

```yaml
filebeat.inputs:
- type: docker
	containers:
		path: "/var/lib/docker/containers"
  	stream: "stdout"
  	ids:
  		- '*'
output.elasticsearch:
  hosts: ["es-pract5:9200"]
```

7. Este fichero contiene la configuración de filenbeat.
8. Esta configurado para monitorizar el directorio /var/lib/docker/containers`.
9. Este es el directorio que hemos montado en el `docker-compose.yml`
10. Modificamos los permisos del fichero `filebeat.yml` 

```bash
sudo chown root filebeat.yml
```

7. Vamos a arrancar la composición con el comando `docker-compose up`.

## Ejercicio 2. Explorando datos con Kibana.

Vamos a ver cómo podemos explorar los datos capturado por ElasticSearch a través de Kibana.

1. En un navegador abrimos la URL http://localhost:5601
2. En el navegador aparecerá el dashboard de Kibana, hacemos click en discovery.
3. Nos aparecerá el menú de crear un índex pattern.
4. Ponemos el texto `filebeat` y damos a `Next step`.
5. En el menú seleccionamos `@timestamp`como unidad de tiempo y pulsamos `Create index pattern`.
6. Volvemos a pulsa en el menú discovery.
7. Ahora podemos explorar los datos generados por Filebeat.
8. **Tarea:** Prueba las diferentes visualizaciones de Kibana.