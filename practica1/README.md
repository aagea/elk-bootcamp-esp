# Practica 1: el entorno de trabajo

La idea de esta práctica es familiarizarse con la herramientas que vamos a usar en el curso.

## Ejercicio 1. Docker compose.

En este ejercicio vamos a arrancar una composición que permite ver diferentes característica de la tecnología docker compose y que nos refresque algunos conceptos de docker.

### Creando el server

1. Lo primero que vamos a hacer es crear un directorio para el docker compose.

```bash
$ mkdir ejercicio1
$ cd ejercicio1
```

2. Vamos a crear un fichero python que ejecuta un servidor, recordar esta va a ser la única vez que utilicemos python. Pero la verdad es que para esto es más fácil utilizar python. Crear el fichero `app.py`.

```python
import time
 
import redis
from flask import Flask
 
 
app = Flask(__name__)
cache = redis.Redis(host='redis', port=6379)
 
 
def get_hit_count():
    retries = 5
    while True:
        try:
            return cache.incr('hits')
        except redis.exceptions.ConnectionError as exc:
            if retries == 0:
                raise exc
            retries -= 1
            time.sleep(0.5)
 
 
@app.route('/')
def hello():
    count = get_hit_count()
    return 'Hello World! I have been seen {} times.\n'.format(count)
 
if __name__ == "__main__":
    app.run(host="0.0.0.0", debug=True)
```

3. Vale ahora creamos el fichero de requisitos el `requirements.txt`

```
flask
Redis
```

### Creando la imagen de docker

Vale ya hemos creado un servidor que se conecta a Redis cada vez que llamamos a su página e incrementa un contador ahora vamos a dockerizarlo.

1. Vamos a crear un fichero que se llama `Dockerfile`, y vamos a pegar este código.

```dockerfile
FROM python:3.4-alpine
ADD . /code
WORKDIR /code
RUN pip install -r requirements.txt
CMD ["python", "app.py"]
```

2. ¿Qué hace esto?

* Coge como base una imagen con python 3.4.
* Añade el contenido de esta carpeta en la carpeta code.
* Marca como directorio de trabajo el directorio code.
* Instala las dependencias.
* Lanza nuestra aplicación.

3. Pregunta: ¿necesitamos python3 instalado en nuestra máquina para que funcione?

### Creando una composición

Bueno ahora vamos a ver como lanzamos esto con docker-compose.

1. Lo primero que tenemos que hacer es crear un fichero llamado `docker-compose.yml` (Que original). El fichero debe contener lo siguiente.

```yaml
version: '3'
services:
  web:
    build: .
    ports:
     - "5000:5000"
  redis:
    image: "redis:alpine"
```

2. ¿Qué hace esto?

* Crea dos servicios: web y redis.
* El servicio web utiliza el `dockerfile` que está en la carpeta
* Redis carga una imagen desde el repositorio.

3. Pregunta: ¿por qué en el puerto está dos veces repetido el 5000?

### Lanzando la composición

Hemos creado la composición, los servicios pero dónde veo la web.

1. Ejecuta el siguiente comando `docker-compose up` .recuerda que debes estar en la misma carpeta donde están los ficheros. 

```bash
$ sudo rm /var/snap/docker/321/run/docker.pid
$ sudo snap stop docker
$ sudo snap start docker
$ sudo docker-compose up
```

2. Debería pasar algo así más o menos.

```
Creating network "ejercicio1_default" with the default driver
Creating ejercicio1_web_1 ...
Creating ejercicio1_redis_1 ...
Creating ejercicio1_web_1
Creating ejercicio1_redis_1 ... done
Attaching to ejercicio1_web_1, ejercicio1_redis_1
web_1    |  * Running on http://0.0.0.0:5000/ (Press CTRL+C to quit)
redis_1  | 1:C 17 Aug 22:11:10.480 # oO0OoO0OoO0Oo Redis is starting oO0OoO0OoO0Oo
redis_1  | 1:C 17 Aug 22:11:10.480 # Redis version=4.0.1, bits=64, commit=00000000, modified=0, pid=1, just started
redis_1  | 1:C 17 Aug 22:11:10.480 # Warning: no config file specified, using the default config. In order to specify a config file use redis-server /path/to/redis.conf
web_1    |  * Restarting with stat
redis_1  | 1:M 17 Aug 22:11:10.483 * Running mode=standalone, port=6379.
redis_1  | 1:M 17 Aug 22:11:10.483 # WARNING: The TCP backlog setting of 511 cannot be enforced because /proc/sys/net/core/somaxconn is set to the lower value of 128.
web_1    |  * Debugger is active!
redis_1  | 1:M 17 Aug 22:11:10.483 # Server initialized
redis_1  | 1:M 17 Aug 22:11:10.483 # WARNING you have Transparent Huge Pages (THP) support enabled in your kernel. This will create latency and memory usage issues with Redis. To fix this issue run the command 'echo never > /sys/kernel/mm/transparent_hugepage/enabled' as 
```

3. Pregunta: ¿qué ha pasado?
4. Abrimos un navegador y entramos en la página <http://localhost:5000>. Desde la máquina virtual, windows no existe para nosotros.
5. Debería mostrar un mensaje así.

```
Hello World! I have been seen 1 times.
```

6. Refrescar la página.

```
Hello World! I have been seen 2 times.
```

6. Ejecuta desde otro terminal el siguiente comando docker image ls
7. Pregunta: ¿Que muestra ese comando?
8. Pregunta: ¿Que debemos hacer si cambiamos el código?

### Haciendo un directorio de binding

Vamos a asociar un directorio de nuestra máquina host a nuestra imagen docker. De esta forma podemos hacer visible nuestro contenido a los proceso ejecutado en el contenedor.

1. Paramos el proceso que se estaba ejecutando. Tan fácil como pulsar `ctrl+c`.
2. Modificamos nuestro fichero `docker-compose.yml` con el siguiente código.

```yaml
version: '3'
services:
  web:
    build: .
    ports:
     - "5000:5000"
    volumes:
     - .:/code
  redis:
    image: "redis:alpine"
```

3. Volvemos a lanzar la composición con el comando `docker-compose up`.
4. Prueba la aplicación.
5. Pregunta: ¿Qué hemos hecho diferente?

### Otros comandos

Ahora vamos a jugar con otros comandos de docker compose.

1. Ejecuta `docker-compose up -d` para lanzar la composición en background.
2. Con el comando `docker-compose ps` podemos ver las imágenes que se están ejecutando.
3. Si ejecutamos `docker-compose run web env` lanzamos el comando env dentro del contenedor.
4. Con el comando `docker-compose stop` paramos los servicios lanzados
5. Con el comando `docker-compose down —volumes` borramos todos los datos incluso los volúmenes que hayan montado las imagenes.

## Ejercicio 2. Lanzando ElasticSearch.

Ya hemos lanzado nuestra primera composición, ahora vamos a hacer algo más interesante vamos a lanzar nuestro primer cluster de dos máquinas de elasticsearch.

### Lanzando el servicio

1. Lo primero volvemos a nuestro carpeta de trabajo.
2. Después nos creamos una carpeta.

```bash
$ mkdir ejercicio2
$ cd ejercicio2
```

3. Creamos el fichero `docker-compose.yml`.

```yaml
version: '3'
services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch-oss:7.9.3
    container_name: elasticsearch
    environment:
      - discovery.type=single-node
    ulimits:
      memlock:
        soft: -1
        hard: -1
    volumes:
      - esdata1:/usr/share/elasticsearch/data
    ports:
      - 9200:9200
volumes:
  esdata1:
    driver: local
```

4. Lanzamos la composición con el comando `docker-compose up`.
5. Para comprobar que funciona correctamente debemos ejecutar el siguiente comando.

```bash
$ http "http://127.0.0.1:9200/_cat/health"
```

### Jugando con un dataset de ejemplo

1. Primero nos descargamos el dataset de ejemplo.

```bash
$ wget "https://raw.githubusercontent.com/elastic/elasticsearch/master/docs/src/test/resources/accounts.json"
```

2. Después ejecuta el siguiente comando.

```bash
$ curl -H "Content-Type: application/json" -XPOST "localhost:9200/bank/_doc/_bulk?pretty&refresh" --data-binary "@accounts.json"
```

2. Para comprobar que todos los datos se han cargado bien ejecuta el siguiente comando.

```bash
$ curl "localhost:9200/_cat/indices?v"
```

3. Para hacer una busqueda simple puedes ejecutar el siguiente comando.

```bash
$ curl -X GET "localhost:9200/bank/_search?q=*&sort=account_number:asc&pretty"

```

4. Este método es una forma alternativa de hacer la misma query

```bash
$ curl -X GET "localhost:9200/bank/_search?pretty" -H 'Content-Type: application/json' -d'
{
  "query": { "match_all": {} },
  "sort": [
	{ "account_number": "asc" }
  ]
}'
```

## Ejercicio 3. Ejecutando un stack de ELK.

Una vez que me hemos arrancado ElasticSearch y hemos comprobado que funciona, vamos a arrancar el stack completo. Para ello vamos a arranca ElasticSearch y Kibana, y como fuente de datos utilizaremos el beat más sencillo, HeartBeat, que monitorizara el estado de un servicio NGINX.

### Lanzando los servicios

1. Lo primero que hacemos es entrar en la carpeta, que ya esta creada, llamada `ejercicio3`

```bash
$ cd ejercicio3
```

2. Abrimos el fichero `docker-compose.yml` con el comando vim `docker-compose.yml`.

```yaml
version: '3'
services:
  web:
    build: .
    ports:
     - "8080:8080"
  heartbeat-pract1:
    user: root
    image: docker.elastic.co/beats/heartbeat-oss:7.9.3
    container_name: heartbeat-pract1
    volumes:
      - ./heartbeat.yml:/usr/share/heartbeat/heartbeat.yml
  es-pract1:
    image: docker.elastic.co/elasticsearch/elasticsearch-oss:7.9.3
    container_name: elasticsearch
    environment:
      - discovery.type=single-node
    ulimits:
      memlock:
        soft: -1
        hard: -1
    volumes:
      - esdata1:/usr/share/elasticsearch/data
    ports:
      - 9200:9200
  kibana-pract1:
    image: docker.elastic.co/kibana/kibana-oss:7.9.3
    environment:
      ELASTICSEARCH_URL: http://es-pract1:9200
    ports:
      - 5601:5601
volumes:
  esdata1:
    driver: local
```

3. Cómo podemos ver se arrancan cuatro servicios: un nginx con contenido estático, ElasticSearch, Kibana y Heartbeats.

4. lasticSearch se levanta de la forma habitual.

5. Kibana se asocia al ElasticSearch ya levantado.

6. Heartbeat tiene un punto de montaje `heartbeat.yml`.

7. Este fichero contiene la configuración de heartbeat.

8. Modificamos los permisos del fichero `hearbeat.yml`.

9. ```bash
   $ sudo chown root hearbeat.yml
   ```

9. Vamos a arrancar la composición con el comando `docker-compose up`.

## 

