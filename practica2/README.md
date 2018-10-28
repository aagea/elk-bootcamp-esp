# Práctica 2: operaciones básicas de ElasticSearch

El objetivo de esta práctica es aprender a utilizar los comandos básicos de ElasticSearch. Indexar, borrar, editar y buscar documentos almacenados en este motor de búsqueda.

## Ejercicio1. Monitorizando el estado de tu cluster.

La idea de este ejercicio es que conozcamos cómo extraer los principales parámetros de un cluster, para conocer su estado.

1. Lo primero que vamos es arrancar ejecutar el comando `docker compose up` en la carpeta del ejercicio.
2. El comando más básico para saber cual es el estado del cluste es el comando status.

```bash
curl -X GET "localhost:9200/_cluster/health?pretty"
```

3. Este comando devuelve la información general de como se encuentra el cluster.
4. Para conseguir toda la información relacionada con el cluster, podemos ejecutar la siguiente instrucción.

```bash
curl -X GET "localhost:9200/_cluster/state?pretty"
```

5. Esto devuelve una  gran cantidad de información podemos filtrarla indicando las métricas que queremos captura.

```bash
curl -X GET "localhost:9200/_cluster/state/metadata,routing_table/?pretty"
```

5. Para recuperar estadísticas del uso del cluster podemos ejecutar la siguiente petición.

```bash
curl -X GET "localhost:9200/_cluster/stats?human&pretty"
```

6. Para recuperar la configuración del cluster podemos ejecutar este comando.

```bash
curl -X GET "localhost:9200/_cluster/settings"
```

7. Listando los indices.

```bash
curl -X GET "localhost:9200/_cat/indices?v"
```

Podemos saber más información de los nodos, del estado de los share y demás  partes del cluster, para ello podemos ir a la API de ElasticSearch.

## Ejercicio2. Operaciones CRUD.

En este ejercicio vamos a repasar las operaciones de creación, lectura, actualización y borrado que podemos hacer con el motor de ElasticSearch.

### Creando y borrando un indice

Lo primero vamos a ver cómo podemos crear un indice en ElasticSearch.

1. Lo primero que vamos es chequear si la composición sigue levantada con el comando `docker-compose ps` . Si el servicio esta parado volvemos a hacer `docker-compose up`.

2. Vamos a crear un indice con el comando básico.

```bash
curl -X PUT "localhost:9200/twitter"
```

3. Este comando utiliza el numero de Shards y réplicas que asigna ElasticSearch por defecto. Si queremos crear un indicio con otro número de Shards o réplicas debemos especificarlo.

```bash
curl -X PUT "localhost:9200/twitter-2" -H 'Content-Type: application/json' -d'
{
    "settings" : {
        "number_of_shards" : 3,
        "number_of_replicas" : 2
    }
}'
```

4. Y si queremos especificar un Mapping Type especifico.

```bash
curl -X PUT "localhost:9200/test" -H 'Content-Type: application/json' -d'
{
    "settings" : {
        "number_of_shards" : 1
    },
    "mappings" : {
        "_doc" : {
            "properties" : {
                "field1" : { "type" : "text" }
            }
        }
    }
}'
```

5. Este comando deberemos meterlo en nuestro script de arranque, pero necesitaré un método para comprobar si  un indice existe o no.

```bash
curl -X HEAD "localhost:9200/twitter"
```

6. Si listamos los indices podemos ver todos los que ahora mismos existen en el sistema.

```bash
curl -X GET "localhost:9200/_cat/indices?v"
```

7. Para borrar un indice podemos ejecutar el siguiente comando.

```bash
curl -X DELETE "localhost:9200/twitter"
```

8. **Tarea:** Borra todo los indices que hemos creado en este apartado.

### Indexando con  documentos

En este apartado vamos a indexar algunos momento y probaremos como podemos  crear nuestro documentos.

1. Vamos a crear un documento.

```bash
curl -X PUT "localhost:9200/twitter/_doc/1" -H 'Content-Type: application/json' -d'
{
    "user" : "kimchy",
    "post_date" : "2009-11-15T14:12:12",
    "message" : "trying out Elasticsearch"
}'
```

2. Pero espera nos habíamos cargado el indice. ¿qué ha pasado?
3. Vale pero hemos creado un documento donde hemos puesto el ID de forma explicita, ahora vamos a probar esto.

```bash
curl -X POST "localhost:9200/twitter/_doc/" -H 'Content-Type: application/json' -d'
{
    "user" : "kimchy",
    "post_date" : "2009-11-15T14:12:12",
    "message" : "trying out Elasticsearch"
}'
```

4. En este caso el id es auto-generado por lo que no nos tenemos que preocupar de su generación.
5. Por último vamos a borrar el indice twitter.

```bash
curl -X DELETE "localhost:9200/twitter"
```

4. **Pregunta:** ¿Cómo se almacenan los documentos?

### Recuperando documentos

Vamos a recuperar los documentos a través de su ID por lo que no haremos consultas complejas, pero entenderemos como se almacenan nuestros datos en ElasticSearch.

1. Vamos a crear un documento.

```bash
curl -X PUT "localhost:9200/twitter/_doc/0" -H 'Content-Type: application/json' -d'
{
    "user" : "kimchy",
    "post_date" : "2009-11-15T14:12:12",
    "message" : "trying out Elasticsearch"
}'
```

2. Primero vamos a chequear que nuestro documento exista.

```bash
curl -X HEAD "localhost:9200/twitter/_doc/0"
```

3. Para recuperarlo por su ID vamos a utilizar el siguiente comando.

```bash
curl -X GET "localhost:9200/twitter/_doc/0?pretty"
```

4. Espera aquí hay más cosas de las que hemos añadido. ¿Para qué sirven todos esto datos?
5. Esto es simple pero la recuperación de información se complica cuándo lanzamos consultas directas a los indices.

### Borrando un documento

Borrar documentos es sencillo en ElasticSearch y no es necesario tener que borrar siempre el indice.

1. Esto es muy sencillo solo tenemos que lanzar este comando y borramos el documento seleccionado.

```bash
curl -X DELETE "localhost:9200/twitter/_doc/0"
```

2. Pero sí queremos borrar varios documento y pero no queremos borrar el indice, debemos utilizar el borrado por query.

```bash
curl -X POST "localhost:9200/twitter/_delete_by_query" -H 'Content-Type: application/json' -d'
{
  "query": { 
    "match_all": {}
  }
}'

```

##Ejercicio 3. Probando el lenguaje de consultas.

En este ejercicio vamos a aprender cómo ejecutar diferentes tipos de consulta utilizando el Query DSL.

1. Vamos a cargar un juego de datos en ElasticSearch para que podamos jugar con los datos del sistema.

```bash
$ curl -H "Content-Type: application/json" -XPOST "localhost:9200/bank/_doc/_bulk?pretty&refresh" --data-binary "@accounts.json"
```

2. Si hacemos `head accounts.json` podemos ver que formato tiene estos datos.

```json
{"index":{"_id":"1"}}
{"account_number":1,"balance":39225,"firstname":"Amber","lastname":"Duke","age":32,"gender":"M","address":"880 Holmes Lane","employer":"Pyrami","email":"amberduke@pyrami.com","city":"Brogan","state":"IL"}
{"index":{"_id":"6"}}
{"account_number":6,"balance":5686,"firstname":"Hattie","lastname":"Bond","age":36,"gender":"M","address":"671 Bristol Street","employer":"Netagy","email":"hattiebond@netagy.com","city":"Dante","state":"TN"}
{"index":{"_id":"13"}}
{"account_number":13,"balance":32838,"firstname":"Nanette","lastname":"Bates","age":28,"gender":"F","address":"789 Madison Street","employer":"Quility","email":"nanettebates@quility.com","city":"Nogal","state":"VA"}
{"index":{"_id":"18"}}
{"account_number":18,"balance":4180,"firstname":"Dale","lastname":"Adams","age":33,"gender":"M","address":"467 Hutchinson Court","employer":"Boink","email":"daleadams@boink.com","city":"Orick","state":"MD"}
{"index":{"_id":"20"}}
{"account_number":20,"balance":16418,"firstname":"Elinor","lastname":"Ratliff","age":36,"gender":"M","address":"282 Kings Place","employer":"Scentric","email":"elinorratliff@scentric.com","city":"Ribera","state":"WA"}
```

3. Otra forma de saber como son los datos que tenemos en recuperando el Mapping Type.

```bash
curl -X GET "localhost:9200/bank/_mapping/_doc"
```

4. Ahora vamos a hacer la query sencilla vamos a contar cuantos registros hay.

```bash
curl -X GET "localhost:9200/bank/_doc/_count" -H 'Content-Type: application/json' -d'
{
    "query": {
        "match_all": {}
    }
}'
```

5. Pregunta: ¿Cuántas mujeres hay en la empresa?
6. Pregunta: ¿Cuántas mujeres viven en MA or WA?
7. Pregunta: ¿Cuántos hombre tiene un saldo mayor  que 30000$?
8. Pregunta: ¿Podemos borrar sólo los hombres por debajo de los 5000$?
9. Pregunta: ¿Cuántas mujeres tienes más de 30 años?









​    





