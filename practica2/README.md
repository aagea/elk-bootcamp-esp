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







​    





