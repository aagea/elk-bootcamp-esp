# Practica 5: indexando y agregando

En esta práctica vamos a probar cómo se usa la librería de ElasticSearch con Java, para ello vamos a usar un código de ejemplo. Nuestro trabajo va a se completar el código.

## Ejercicio 1. Añadiendo el proyecto

En este apartado vamos a añadir el proyecto a IntelliJ para que sea mucho más fácil trabajar con el código Java.

1. Lo primero que vamos a hacer es chequear que el entorno funciona. Para ello ejecutamos `mvn clean install` desde la carpeta de la práctica. Deberán fallar los tests.
2. Este comando se descargará las dependencias y compilara el proyecto, el resultado debería ser correcto.
3. Ahora para limpiar la carpeta ejecutamos `mvn clean`.
4. Ahora abrimos IntelliJ,
5. Pulsamos en `Import project`. 
6. Seleccionamos la carpeta `Practica5`.
7. Ahora seleccionamos la opción `Import project from external model`.
8. Seleccionamos `maven` y pulsamos `next`.
9. Marcamos la casilla `Import Maven projects automatically`.
10. Pulsamos `next`.
11. Pulsamos `next`.
12. Pulsamos `next`.
13. Pulsamos `finish`.
14. Ya esta ahora ya tenemos nuestro proyecto importado en IntelliJ. 

## Ejercicio 2. Indexando el CSV

El primer ejercicio que vamos a hacer es rellenar el método de indexación, este método es necesario para indexar todos los registros del CSV. Una vez que el método este completo, lanzaremos la aplicación y ejecutaremos el modo `i`. En caso de que algo salga mal siempre podemos respetar el indice utilizando la aplicación el modo `r`.

## Ejercicio 3. getOlympicWinnerByYear

Este método debe decirlo que país obtuvo más medalla desde 1950. Para ello vamos a tener que hacer una consulta de agregación.

1. Primero creamos la base de la consulta.

```java
SearchRequest searchRequest = new SearchRequest();
SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
```

2. Creamos la query que filtrará todos los resultado menores de 1950.

```java
searchSourceBuilder.query(QueryBuilders.rangeQuery(RecordController.YEAR_FIELD)
	.from("1950"))
  .sort(RecordController.YEAR_FIELD, SortOrder.DESC);
```

3. Creamos la consulta de agregación. Esta consulta agrega primero por año y después por país, ordenando estos últimos por el número de veces que aparece en el indice.

```java
searchSourceBuilder.aggregation(AggregationBuilders.terms("year")
	.field(RecordController.YEAR_FIELD)
  .subAggregation(
    AggregationBuilders
    .terms("country")
    .field(COUNTRY_FIELD)
    .order(BucketOrder.aggregation("_count", false))
	)
);
```

4. Ya esta todo listo para que podamos ejecutar la consulta.

```java
searchRequest.source(searchSourceBuilder);
final SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
```

5. Para recuperar los resultados, primero extremos la agregación `year`, después recorremos los buckets y recuperamos las claves y los valores. Para recuperar el campo `country` tenemos que introducirnos en la segunda agregación y extraer la clave del campo.

```java
Terms years = searchResponse.getAggregations().get("year");
List<OlympicWinner> olympicWinners = new LinkedList<>();
for (Terms.Bucket x : years.getBuckets()) {
  String year = x.getKeyAsString();
  Terms countries = x.getAggregations().get("country");
  String country = countries.getBuckets().get(0).getKeyAsString();
  long medals = countries.getBuckets().get(0).getDocCount();

  olympicWinners.add(new OlympicWinner(year, country, (int) medals));
}
return olympicWinners;
```



## Ejercicio 4. Completando el resto de los métodos

El resto del ejercicio consistirá en completar el resto de los métodos de la clase, para ello deberás seguir el mismo esquema que en los ejercicios anteriores.

 

