# Practica 9: probando Logstash

Aunque LogStash al principio era una parte muy importante del stack ELK, poco a poco ha ido perdiendo relevancia en favor de los Beats. Actualmente LogStash se utilizada para gestionar transformaciones un poco más complejas, filtrados y publicación de información a multiples almacenamientos.

En esta practica vamos a entender como se compone un pipeline que extraiga información de las trazas de log.

## Ejercicio 1. Lanzando el compose.

Lo primero que vamos a hacer es lanzar el compose para ello primero vamos a analizar como está implementado.

1. Abrimos el fichero `docker-compose.yml` con el comando `vim docker-compose.yml`.

```yaml
version: '3'
services:
  es-pract9:
    image: docker.elastic.co/elasticsearch/elasticsearch-oss:7.2.0
    container_name: es-pract9
    environment:
      - discovery.type=single-node
    ulimits:
      memlock:
        soft: -1
        hard: -1
    volumes:
      - es-data9:/usr/share/elasticsearch/data
    ports:
      - 9200:9200
  logstash-pract9:
    user: root
    image: docker.elastic.co/logstash/logstash-oss:7.2.0
    container_name: logstash-pract9
    volumes:
      - ./pipeline:/usr/share/logstash/pipeline/
  filebeat-pract9:
    user: root
    image: docker.elastic.co/beats/filebeat-oss:7.2.0
    container_name: filebeat-pract9
    volumes:
      - ./filebeat.yml:/usr/share/filebeat/filebeat.yml
      - ./data/:/var/log/pratc9/
  kibana-pract9:
    image: docker.elastic.co/kibana/kibana-oss:7.2.0
    environment:
     ELASTICSEARCH_URL: http://es-pract9:9200
    ports:
      - 5601:5601
volumes:
  es-data9:
    driver: local
```

2. Como podemos ver arrancamos cinco servicios: 
   - **ElasticSearch,** en el vamos a almacenar todas las métricas de los servicios.
   - **Kibana,** lo utilizamos para visualizar y explorar la información almacenada en ElasticSearch.
   - **Filebeats,** extrae todos los logs generados por los contenedores y por el NGINX.
   - **LogStash,** va a recibir todos los mensajes que se manden desde Filebeat.
3. ElasticSearch se levanta de la forma habitual.
4. Kibana se asocia al ElasticSearch ya levantado.
5. Filebeats tiene un punto de montaje para leer un fichero precargado con trazas de logs.
6. Vamos a abrir el fichero `filebeat.yml`.

```yaml
filebeat.inputs:
- type: log
  paths:
    - /var/log/pratc9/*.log 
output.logstash:
  hosts: ["logstash-pract9:5044"]
```

7. Este fichero contiene la configuración de filebeat.
8. Lo que hacemos es leer todos los ficheros de logs que se contengan en esta carpeta y los mandamos a LogStash.
9. Modificamos los permisos del fichero `filebeat.yml` 

```bash
sudo chown root filebeat.yml
```

10. Si nos fijamos en e LogStash tenemos una punto de montaje que configura la carpeta donde vamos a guardar los pipelines.
11. Un pipeline es una configuración de entrada y salida de LogStash. 
12. A continuation ponemos un esqueleto de lo que es un pipeline.

```
# En esta seccion describimos las entradas del pipeline.
input {
}
# El filtrado es algo opcional, es donde se tranforman los mensajes.
# filter {
#
# }
# Aqui se indican donde vamos a escribir los resultados.
output {
}
```

13. Vamos a abrir el fichero `pipeline.conf` con el comando `vim pipeline.conf`.

```
input {
	beats {
        port => "5044"
        host => "0.0.0.0"
    }
}
# El filtrado es algo opcional, es donde se tranforman los mensajes.
# filter {
#
# }

output {
    stdout { codec => rubydebug }
}
```

14. La entrada ponemos el plugin de beats.
15. La salida es la salida estándar por lo que podemos ver en pantalla todos los mensajes de logs.
16. Ejecutamos primero logstash `docker-compose up logstash-pract9`
17. Cuando logstash este arrancado, debería salir por pantalla algo así.

```verilog
logstash-pract9    | Sending Logstash logs to /usr/share/logstash/logs which is now configured via log4j2.properties
logstash-pract9    | [2018-11-18T22:51:51,166][INFO ][logstash.setting.writabledirectory] Creating directory {:setting=>"path.queue", :path=>"/usr/share/logstash/data/queue"}
logstash-pract9    | [2018-11-18T22:51:51,185][INFO ][logstash.setting.writabledirectory] Creating directory {:setting=>"path.dead_letter_queue", :path=>"/usr/share/logstash/data/dead_letter_queue"}
logstash-pract9    | [2018-11-18T22:51:52,299][INFO ][logstash.agent           ] No persistent UUID file found. Generating new UUID {:uuid=>"011df0db-a8c5-4612-b072-b6bb11bc2526", :path=>"/usr/share/logstash/data/uuid"}
logstash-pract9    | [2018-11-18T22:51:53,689][INFO ][logstash.runner          ] Starting Logstash {"logstash.version"=>"6.4.3"}
logstash-pract9    | [2018-11-18T22:51:58,072][INFO ][logstash.pipeline        ] Starting pipeline {:pipeline_id=>"main", "pipeline.workers"=>2, "pipeline.batch.size"=>125, "pipeline.batch.delay"=>50}
logstash-pract9    | [2018-11-18T22:51:59,140][INFO ][logstash.inputs.beats    ] Beats inputs: Starting input listener {:address=>"0.0.0.0:5044"}
logstash-pract9    | [2018-11-18T22:51:59,186][INFO ][logstash.pipeline        ] Pipeline started successfully {:pipeline_id=>"main", :thread=>"#<Thread:0x7faa3982 run>"}
logstash-pract9    | [2018-11-18T22:51:59,449][INFO ][logstash.agent           ] Pipelines running {:count=>1, :running_pipelines=>[:main], :non_running_pipelines=>[]}
logstash-pract9    | [2018-11-18T22:51:59,674][INFO ][org.logstash.beats.Server] Starting server on port: 5044
logstash-pract9    | [2018-11-18T22:52:00,295][INFO ][logstash.agent           ] Successfully started Logstash API endpoint {:port=>9600}
```

18. **Desde otra consola.** Ejecutamos el siguiente comando en la misma carpeta de la práctica.

```bash
docker-compose up filebeat-pract9
```

19. Los logs del fichero se mostrarán por pantalla.

## Ejercicio2. Analizando los mensajes de Log.

Podemos comprobar que file beat nos da muchos metadatos de la traza de log, sin embargo no descompone el mensaje de log.

```
{
    "@timestamp" => 2017-11-09T01:44:20.071Z,
        "offset" => 325,
      "@version" => "1",
          "beat" => {
            "name" => "My-MacBook-Pro.local",
        "hostname" => "My-MacBook-Pro.local",
         "version" => "6.0.0"
    },
          "host" => "My-MacBook-Pro.local",
    "prospector" => {
        "type" => "log"
    },
        "source" => "/path/to/file/logstash-tutorial.log",
       "message" => "83.149.9.216 - - [04/Jan/2015:05:13:42 +0000] \"GET /presentations/logstash-monitorama-2013/images/kibana-search.png HTTP/1.1\" 200 203023 \"http://semicomplete.com/presentations/logstash-monitorama-2013/\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.77 Safari/537.36\"",
          "tags" => [
        [0] "beats_input_codec_plain_applied"
    ]
}
...
```

Para descomponer el mensaje vamos a utilizar uno de los plugins más útiles de LogStash, el Grok filter.

1. Primero paramos todos los docker con el comando `docker-compose down`
2. Si analizamos el mensaje de una de las trazas, veremos que es muy fácil de reconocer muchos campos.

```verilog
83.149.9.216 - - [04/Jan/2015:05:13:42 +0000] "GET /presentations/logstash-monitorama-2013/images/kibana-search.png HTTP/1.1" 200 203023 "http://semicomplete.com/presentations/logstash-monitorama-2013/" "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.77 Safari/537.36"
```

3. El Grok filter justamente se encarga de extraer esa información, para ello sólo tenemos que modificar el pipeline e introducir este texto en la sección de filter.

```
filter {
    grok {
        match => { "message" => "%{COMBINEDAPACHELOG}"}
    }
}
```

4. Ejecutamos primero logstash `docker-compose up logstash-pract9`
5. Esperamos a que el logstash este arrancado.
6. **Desde otra consola.** Ejecutamos el siguiente comando en la misma carpeta de la práctica.

```bash
docker-compose up filebeat-pract9
```

7. Ahora del mensaje se han extraído más campos que nos van a ser muy fáciles de analizar.

```
{
        "request" => "/presentations/logstash-monitorama-2013/images/kibana-search.png",
          "agent" => "\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.77 Safari/537.36\"",
         "offset" => 325,
           "auth" => "-",
          "ident" => "-",
           "verb" => "GET",
     "prospector" => {
        "type" => "log"
    },
         "source" => "/path/to/file/logstash-tutorial.log",
        "message" => "83.149.9.216 - - [04/Jan/2015:05:13:42 +0000] \"GET /presentations/logstash-monitorama-2013/images/kibana-search.png HTTP/1.1\" 200 203023 \"http://semicomplete.com/presentations/logstash-monitorama-2013/\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.77 Safari/537.36\"",
           "tags" => [
        [0] "beats_input_codec_plain_applied"
    ],
       "referrer" => "\"http://semicomplete.com/presentations/logstash-monitorama-2013/\"",
     "@timestamp" => 2017-11-09T02:51:12.416Z,
       "response" => "200",
          "bytes" => "203023",
       "clientip" => "83.149.9.216",
       "@version" => "1",
           "beat" => {
            "name" => "My-MacBook-Pro.local",
        "hostname" => "My-MacBook-Pro.local",
         "version" => "6.0.0"
    },
           "host" => "My-MacBook-Pro.local",
    "httpversion" => "1.1",
      "timestamp" => "04/Jan/2015:05:13:42 +0000"
}
```

## Ejercicio 3. Añadiendo la Geo localización.

Ahora que hemos extraído la ip podemos utilizar el sistema Geoip para extraer la geo localización de las trazas, y poder conocer de dónde viene el trafico que estamos capturando.

1. Primero paramos todos los docker con el comando `docker-compose down`
2. Vamos a añadir un nuevo filter en el pipeline.

```
    geoip {
        source => "clientip"
    }
```

3. Este filter extrae del campo clientip, que es donde Grok pone la IP del cliente de la request, la posición.
4. El fichero final debería tener este aspecto.

```
input {
    beats {
        port => "5044"
    }
}
 filter {
    grok {
        match => { "message" => "%{COMBINEDAPACHELOG}"}
    }
    geoip {
        source => "clientip"
    }
}
output {
    stdout { codec => rubydebug }
}
```

5. Ejecutamos primero logstash `docker-compose up logstash-pract9`
6. Esperamos a que el logstash este arrancado.
7. **Desde otra consola.** Ejecutamos el siguiente comando en la misma carpeta de la práctica.

```bash
docker-compose up filebeat-pract9
```

8. Ahora los mensajes debería incluir detalles de la localización del cliente.

```
{
        "request" => "/presentations/logstash-monitorama-2013/images/kibana-search.png",
          "agent" => "\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.77 Safari/537.36\"",
          "geoip" => {
              "timezone" => "Europe/Moscow",
                    "ip" => "83.149.9.216",
              "latitude" => 55.7485,
        "continent_code" => "EU",
             "city_name" => "Moscow",
          "country_name" => "Russia",
         "country_code2" => "RU",
         "country_code3" => "RU",
           "region_name" => "Moscow",
              "location" => {
            "lon" => 37.6184,
            "lat" => 55.7485
        },
           "postal_code" => "101194",
           "region_code" => "MOW",
             "longitude" => 37.6184
    },
    ...
```

## Ejercicio 4. Indexando en ElasticSearch y visualizando en Kibana.

El principal objetivo de LogStash es indexar la información en ElasticSearch para ser visualizada en Kibana. Por lo tanto, eso es lo que vamos a hacer y no podría ser más sencillo.

1. Primero paramos todos los docker con el comando `docker-compose down`
2. Ahora modificamos el campo output del fichero pipelines.

```
output {
    elasticsearch {
        hosts => [ "es-pract9:9200" ]
    }
}
```

3. El fichero debería tener más o menos esta pinta.

```
input {
    beats {
        port => "5044"
    }
}
 filter {
    grok {
        match => { "message" => "%{COMBINEDAPACHELOG}"}
    }
    geoip {
        source => "clientip"
    }
}
output {
    elasticsearch {
        hosts => [ "es-pract9:9200" ]
    }
}
```

4. Ahora ejecutamos el comando `docker-compose up`
5. **Tarea:** crear una visualización que aproveche los datos de geolocalizaciones.





