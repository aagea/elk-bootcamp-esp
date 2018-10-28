# Práctica 2: operaciones básicas de ElasticSearch

El objetivo de esta práctica es aprender a utilizar los comandos básicos de ElasticSearch. Indexar, borrar, editar y buscar documentos almacenados en este motor de búsqueda.

## Ejercicio1. Monitorizando el estado de tu cluster.

1. Lo primero que vamos es arrancar ejecutar el comando `docker compose up` en la carpeta del ejercicio.
2. El comando más básico para saber cual es el estado del cluste es el comando status.

```bash
$ curl -X GET "localhost:9200/_cat/health?v"
```

3. Este comando devuelve la información general de como se encuentra el cluster.



