# Práctica 4: usando Term-level queries

En esta práctica, vamos a probar a crear term-level queries o filtros para manejar documentos en ElasticSearch. 

## Ejercicio 1. Añadiendo el proyecto

En este apartado vamos a añadir el proyecto a IntelliJ para que sea mucho más fácil trabajar con el código Java.

1. Lo primero que vamos a hacer es chequear que el entorno funciona. Para ello ejecutamos `mvn clean install` desde la carpeta de la práctica. Deberán fallar los tests.
2. Este comando se descargará las dependencias y compilara el proyecto, el resultado debería ser correcto.
3. Ahora para limpiar la carpeta ejecutamos `mvn clean`.
4. Ahora abrimos IntelliJ,
5. Pulsamos en `Import project`. 
6. Seleccionamos la carpeta `Practica3`.
7. Ahora seleccionamos la opción `Import project from external model`.
8. Seleccionamos `maven` y pulsamos `next`.
9. Marcamos la casilla `Import Maven projects automatically`.
10. Pulsamos `next`.
11. Pulsamos `next`.
12. Pulsamos `next`.
13. Pulsamos `finish`.
14. Ya esta ahora ya tenemos nuestro proyecto importado en IntelliJ. 

## Ejercicio 2. Repaso del código

En este apartado vamos a repasar la dos clases que están incluidas en el repositorio: `Practica4Controller` y `EventController`.

2. La clase `EventController` es una clase Abstracta que contiene las llamadas de ElasticSearch.
2. La clase `Event` es un clase que representa un evento en el sistema.
3. La clase `Practica4Controller` es la clase que vamos a implementar.
4. Esta clase tiene una instancia del cliente de alto nivel.

El objetivo de este proyecto es crear una clase que nos ayude a navegar entre los eventos de un timeline, agrupando esos evento en base a unos tags.

## Ejercicio 3. Creando la función Last()

La función `last(String id, List<String> tags, int limit, Instant before)` devuelve los últimos n elementos de un timeline concreto que contengan algunos de los tags asignado y que estén antes de una fecha dada. 

Para poder resolver este método vamos a utilizar una función boolean query. La boolean query contiene tres tipos de busqueda:

+ **Must queries.** Son filtros que el documento **DEBE** cumplir
+ **Should queries.** Son filtros que el documento **DEBERIA** cumplir. Para hacer que al menos una de consulta sea obligatoria tendremos que modificar el parámetro `minimumShouldMatch`.
+ **Must not queries.** Son filtros que el documento **NO DEBE** cumplir, los resultado que pasen este filtro serán excluidos de los resultados. 

1. Lo primero que vamos a hacer es crearlo el objeto resulta que vamos a devolver en caso de que todo vaya bien.

```java
List<Event> result = new LinkedList<>();
```

2. Una vez hecho esto nos crearemos una instancia del Bool Query Builder y añadiremos el primer filtro **must**.

```java
BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
	.must(QueryBuilders.termQuery(EventController.ID_FIELD, id));
```

3. Por cada uno de los tags añadiremos un filtro **should**.

```java
for (String t : tags) {
	boolQueryBuilder = boolQueryBuilder.should(
  QueryBuilders.termQuery(EventController.TAG_FIELD, t));
}

```

4. En caso de que el parámetro Before sea distinto de null, debemos otro filtro **must**.

```java
if (before != null){
	boolQueryBuilder = boolQueryBuilder.must(
	QueryBuilders.rangeQuery(EventController.T2_FIELD).to(before));
}
```

5. Modificamos el parámetro `minimumShouldMatch` para que el evento al menos contenga un tag.

```java
boolQueryBuilder=boolQueryBuilder.minimumShouldMatch(1);
```

6. Creamos la llamada al API.

```java
SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
sourceBuilder.query(boolQueryBuilder)
             .sort(EventController.T2_FIELD, SortOrder.DESC).size(limit);
SearchRequest searchRequest = new SearchRequest(EventController.EVENT_INDEX)
                .source(sourceBuilder);
```

7. Fíjate que hemos ordenado la consulta en orden descendente, sin embargo queremos que el orden sea siempre ascendente ¿por qué hemos hecho esto?
8. Por último, recorremos el ResultSet y creamos la lista de eventos.

```java
try {
  SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
  response.getHits().iterator().forEachRemaining(it -> {
    Map<String, Object> source = it.getSourceAsMap();
    result.add(
      new Event(
        source.get(EventController.ID_FIELD).toString(),
        source.get(EventController.TAG_FIELD).toString(),
        Instant.parse(source.get(EventController.T1_FIELD).toString()),
        Instant.parse(source.get(EventController.T2_FIELD).toString())
      )
    );
  });
} catch (IOException e) {
	return Optional.empty();
}
Collections.reverse(result);
return Optional.of(result);
```

9. Arrancamos ElasticSearch utilizando el comando `docker-compose up`
10. Probamos de nuevo los test, veremos cómo algunos de ellos ya pasan.
11. Tarea: ¿porque hacemos un reverse de la lista?

## Ejercicio 4. Pasando los tests

Muy bien ya hemos hecho un ejemplo, ahora el resto de la practica es rellenar el resto de métodos, y conseguir que los tests pasen. Recordad los tipos diferentes de filtros y cómo debemos ordenar los resultados para filtrar.



 

