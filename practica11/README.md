# Practica 8: probando Kibana

Esta práctica se compone de dos partes, un repaso de metrics, donde vamos a instalar Filebeats, Metricbeats, Hearbeats, ElasticSearch y Kibana. Ademas lanzaremos un servicio NGINX que servirá contenido estático y nos servirá para capturar sus logs. La segunda parte del ejercicio consistirá en usar Kibana para crear un dashboard que nos muestre el estado de nuestra máquina.

## Ejercicio 1. Lanzando el compose.

Lo primero que vamos a hacer es lanzar el compose para ello primero vamos a analizar como está implementado.

1. Abrimos el fichero `docker-compose.yml` con el comando `vim docker-compose.yml`.

```yaml
version: '3'
services:
  es-pract8:
    image: docker.elastic.co/elasticsearch/elasticsearch-oss:7.2.0
    container_name: elasticsearch
    environment:
      - discovery.type=single-node
    ulimits:
      memlock:
        soft: -1
        hard: -1
    volumes:
      - es-data8:/usr/share/elasticsearch/data
    ports:
      - 9200:9200
    #Healthcheck to confirm availability of ES. Other containers wait on this.
    healthcheck:
      test: ["CMD", "curl","-s" ,"-f", "http://localhost:9200/_cat/health"]
  filebeat-pract8:
    user: root
    image: docker.elastic.co/beats/filebeat-oss:7.2.0
    container_name: filebeat-pract5
    volumes:
      - ./filebeat.yml:/usr/share/filebeat/filebeat.yml
      - /var/snap/docker/common/var-lib-docker/containers:/var/lib/docker/containers
      - ./logs/nginx/:/var/log/nginx/
  metricbeat-pract8:
    user: root
    image: docker.elastic.co/beats/metricbeat-oss:7.2.0
    container_name: metricbeat-pract8
    volumes:
      - ./metricbeat.yml:/usr/share/metricbeat/metricbeat.yml
      - /var/run/docker.sock:/var/run/docker.sock
  heartbeat-pract8:
    user: root
    image: docker.elastic.co/beats/heartbeat-oss:7.2.0
    container_name: heartbeat-pract8
    volumes:
      - ./heartbeat.yml:/usr/share/heartbeat/heartbeat.yml
  kibana-pract8:
    image: docker.elastic.co/kibana/kibana-oss:7.2.0
    environment:
      ELASTICSEARCH_URL: http://es-pract8:9200
    ports:
      - 5601:5601
  nginx-pract8:
    container_name: nginx-pract8
    hostname: nginx
    build: ./nginx
    #Expose port 80 to allow users to hit content and generate data for filebeat and packetbeat
    ports: ['8080:80']
    command: nginx -g 'daemon off;'
    volumes:
      #Logs are mounted to a relative path. These are also accessed by Filebeat and consumed by the Nginx module
      - ./logs/nginx/:/var/log/nginx/
volumes:
  es-data8:
    driver: local
```

2. Como podemos ver arrancamos cinco servicios: 
   - **ElasticSearch,** en el vamos a almacenar todas las métricas de los servicios.
   - **Kibana,** lo utilizamos para visualizar y explorar la información almacenada en ElasticSearch.
   - **Filebeats,** extrae todos los logs generados por los contenedores y por el NGINX.
   - **Metricbeats,** recoge la información de uso de CPU y de IOPS de la máquina host.
   - **Heartbeats,** comprueba el status de ElasticSearch y del NGINX. 
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
  hosts: ["es-pract8:9200"]
```

7. Este fichero contiene la configuración de filebeat.
8. Hemos lanzado un modulo NGINX que captura los logs y los transforma para que los podamos buscar en ElasticSearch.
9. Modificamos los permisos del fichero `filebeat.yml` 

```bash
sudo chown root filebeat.yml
```

10. Metricbeats tiene un punto de montaje a `/var/run/docker.sock` de donde extraerá las métricas de los servicios de docker.
11. Vamos a abrir el fichero `metricbeats.yml`.

```yaml
metricbeat.modules:
- module: docker
  metricsets:
    - "cpu"
    - "diskio"
  hosts: ["unix:///var/run/docker.sock"]
  period: 10s
  enabled: true
output.elasticsearch:
  hosts: ["es-pract8:9200"]
```

12. Metrics solo capturará las métricas relacionadas con la CPU y la operaciones de entrada y salida.
13. Modificamos los permisos del fichero `metricsbeat.yml` 

```bash
sudo chown root metricsbeat.yml
```

14. Heartbeats no tiene ningún punto de montaje asignado.
15. Vamos a abrir el fichero `heartbeat.yml`.

```yaml
heartbeat.monitors:
- type: http
  schedule: '@every 5s'
  urls: ["http://es-pract8:9200"]
  check.request:
    method: "GET"
  check.response:
    status: 200
- type: http
  enabled: true
  schedule: '@every 5s'
  urls: ["http://nginx-pract8/server-status"]
  ipv4: true
  ipv6: true
  mode: any
  timeout: 5s
  check.request:
    method: "GET"
  check.response:
    status: 200
output.elasticsearch:
  hosts: ["es-pract8:9200"]
```

16. Cómo podemos ver Heartbeats hace dos comprobaciones cada 5 segundos una para chequear ElasticSearch y otra para chequear NGINX.
17. Modificamos los permisos del fichero `heartbeat.yml` 

```bash
sudo chown root heartbeat.yml
```

18. Lanzamos el compose usando el comando `docker-compose up`.

## Ejercicio 2. Primeros pasos con Kibana.

Vamos a ver cómo podemos explorar los datos capturado por ElasticSearch a través de Kibana.

1. En un navegador abrimos la URL http://localhost:5601
2. En esta pagina aparece la aplicación de Kibana que contiene un sidebar con ocho pestañas:
   - **Discover.** Dónde podemos explorar los datos almacenados en ElasticSearch.
   - **Visualize.** Para poder visualizar los datos debemos definir las gráficas que vamos a embeber en los dashboards.
   - **Dashboard.** Aquí definiremos los dashboards que crearemos utilizando la visualizaciones que hayamos diseñado.
   - **Management.** En esta pestaña controlaremos la configuración de Kibana.
3. Lo primero que hacemos es hacer click en la pestaña `management`.
4. La primera sección que aparece se refiere a ElasticSearch. En ella podemos comprobar el estado de los indices, su tamaño y el numero de shards.
5. También podemos gestionar los mapping types.
6. En la sección de Kibana podemos ver lo siguiente:
   - **Index Patterns.** Esta sección es donde vamos a configurar los indices que agregará Kibana. Para ello utilizaremos un nombre, con comodines para poder agrupar varios indices a la vez.
   - **Saved objects.** Contiene un listado con todos los objetos que están almacenados en Kibana permite tanto importarlos como exportarlos.
   -  **Reporting.** Los reports generados por el sistema.
   - **Advanced settings.** En esta página podemos controlar todas las propiedades de la plataforma. Desde formatos de fecha, hasta personalización de la interfaz.
7. Ahora vamos a crear un index pattern para cada uno de los beats que hemos configurado.
8. Como campo de tiempo debemos utilizar el campo `@timestamp`
9. Ahora pulsamos en la pestaña discover, en ella podemos visualizar la información que esta almacenada en ElasticSearch.
11. **Tarea:** explora los datos y encuentra el campo que indica el porcentaje de uso total de CPU.
12. Ahora vamos a la sección de `Visualize`.
13. Pulsamos el botón `+`.
14. Seleccionamos la gráfica `Vertical Bar`.
15. Seleccionamos el pattern que captura los datos de heartbeat.
16. Primero vamos a configura el eje x.
17. Para ello vamos a pulsar al botón `X-Axis`.
18. Seleccionamos la agregación `Date Histogram`
19. Ahora añadimos un nuevo criterio de agrupación, pulsando `Add sub-buckets` y `Split charts`.
20. Seleccionamos que queremos separarlos por columnas.
21. Seleccionamos la agregación `Terms`.
22. Y el field `monitor-id`.
23. Ahora añadimos un nuevo criterio de agrupación, pulsando `Add sub-buckets` y `Split series`.
24. Seleccionamos la agregación `Terms`.
25. Y el field `monitor-status`.
26. El eje-y no hace falta que lo toquemos.
27. Ahora tenemos un gráfico que nos muestra un el estado de los dos componentes.
28. Buscamos en la barra superior el botón `Save` y guardamos la visualización con el nombre `Health Check`.
29. Hacemos click en la pestaña `Dashboard`.
30. Pulsamos en el botón `Create new dashboard`. 
31. Pulsamos en el botón `Add` y seleccionamos la visualización `Health Check`.
32. Pulsamos en el botón `Save` y ya tenemos nuestro primer dashboard.
33. **Tarea:** Creemos un grafico usando los datos de CPU, y pon esta visualización en el mismo dashboard.

## Ejercicio 3. Creando un gráfico usando Timelion.

Para trabaja con series temporales, Kibana recomienda utilizar este tipo de gráficas con Timelion. Timelion define un nuevo lenguaje que nos permite ser más descriptivos a la hora de diseñar la visualización.

1. Lo primero que tenemos que hacer es pulsar en la pestaña de visualizaciones e incluir este tipo de visualizacion  `Timelion`.
2. En esta pestaña nos encontramos dos paneles: el superior que indica la query que se va a ejecutar y el inferioridades con los resultados de la query.
3. El comando `.es` hace referencia a ElasticSearch, por tanto aquí vamos a describir de donde cogemos la información de nuestra series temporales.
4. La propia caja de texto tiene un sistema de auto-completado que te describe cada uno de los campos, vamos a introducir el siguiente texto.

```timelion
.es(index=metric*,metric=avg:docker.cpu.total.pct,timefield=@timestamp)
```

5. Esto añadirá una serie temporal a la gráfica.
6. Si queremos añadir otra serie temporal a la misma gráfica sólo hay que incluir otra descripción separada por una coma.
7. **Tarea:** Añadir en la misma gráfica el valor mínimo de la cpu total.
8. Tambien se puede modificar el estilo de las gráficas.

```timelion
.es(index=metric*,metric=avg:docker.cpu.total.pct,timefield=@timestamp).color(red)
```

9. Aunque la verdadera potencia se obtiene cuando se quiere comparar dos series temporales a la vez.

```
.es(index=metric*,metric=avg:docker.cpu.total.pct,timefield=@timestamp).color(red), .es(index=metric*,metric=avg:docker.cpu.total.pct,timefield=@timestamp).color(red).mvavg(window=5m)
```

10. La gráfica se puede salvar para ser exportada o para ser usada en el dashboard de Kibana.
11. **Tarea:** Incluye esta gráfica en el dashboard de Kibana que hemos creado en el ejercicio anterior.

## Ejercicio 4. Montando un dashboard complejo.

En este ejercicio te vamos a proponer un reto que será montar un dashboard con varios datos que están siendo almacenado en ElasticSearch.

![Dashboard](https://i.imgur.com/0uI7l1cl.png)





