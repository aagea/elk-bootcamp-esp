# Practica 3: usando ElasticSearch con Java

En esta práctica vamos a probar cómo se usa la librería de ElasticSearch con Java, para ello vamos a usar un código de ejemplo. Nuestro trabajo va a se completar el código.

## Añadiendo el proyecto

En este apartado vamos a añadir el proyecto a IntelliJ para que sea mucho más fácil trabajar con el código Java.

1. Lo primero que vamos a hacer es chequear que el entorno funciona. Para ello ejecutamos `mvn clean install` desde la carpeta de la práctica.
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

## Revisando las dependencias

Lo primero que vamos a hacer es comprobar las dependencias que tenemos instaladas.

1. Para ello vamos a abrir el fichero `pom.xml`. Allí podemos comprobar que nuestro código esta usando la librería `lasticsearch-rest-high-level-client` la librería `transport ` estará deprecada a partir de la versión 7.0.0.

```xml
<dependencies>
    <dependency>
        <groupId>org.elasticsearch.client</groupId>
        <artifactId>elasticsearch-rest-high-level-client</artifactId>
        <version>6.4.2</version>
    </dependency>
</dependencies>
```

2. Ahora vamos a al fichero `com.alvaroagea.elk.practica3.Controller` éste contiene todos los métodos que gestionan el indice.

## Repaso del código

En este apartado vamos a repasar la dos clases que están incluidas en el repositorio: `App` y `Controller`.

1. La clase `App` contiene la lógica de cómo se ejecutan los comandos desde consola.
2. La clase `controller` contiene los métodos que tienen que acceder a ElasticSearch.
3. Esta clase tiene una instancia del cliente de alto nivel.
4. **Tarea:** revisa los métodos con los que cuenta el cliente rest en esta [URL](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.4/java-rest-high-getting-started.html)

## Indexando los elementos

Después de haber revisado los diferentes documentos, vamos a indexar un documento. Para ello vamos a modificar el método índex que se encuentra en la clase `controller`.

1. Lo primero vamos a crear un objeto `IndexRequest`, que contenga los campo requeridos.

```java
IndexRequest indexRequest = new IndexRequest("message")
                .source("author", author,
                        "date", date,
                        "message", message);
```

2. Con este método hemos creado el cuerpo de la query que queremos mandar al servidor.
3. Ahora debemos ejecutar la query. Para ello llamamos al `client` al método `index`.

```java
IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
```

4. Por último, imprimimos por pantalla el resultado de la query.

```java
System.out.println(indexResponse.toString());
```

5. Recordar añadir todos los import necesarios, para ello debéis poner el cursor encima de la clase y pulsar `alt+enter`.

```java
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
```

5. Para chequear que todo esta bien ejecutar el comando `mvn clean install`.

## Buscando dentro de los mensajes

El siguiente paso va a ser implementar el método que nos permite buscar dentro de los mensajes de texto. Para ello vamos a hacer un Match Query. Vamos a implementar el método `searchMessage`.

1. Lo primero que debemos hacer es que crear un objeto `SearchSourceBuilder`.

```java
SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
```

2. Ahora con `QueryBuilders` creamos la consulta que queremos ejecutar.

```java
sourceBuilder.query(QueryBuilders.matchQuery("message", message));
```

3. Creamos el `SearchRequest` y la ejecutamos.

```java
 SearchRequest searchRequest = new SearchRequest("message")
                .source(sourceBuilder);
  SearchResponse response = client.search(searchRequest,RequestOptions.DEFAULT);
```

4. Por último iteramos por todos los elementos y los imprimimos por pantalla.

```java
 response.getHits().iterator().forEachRemaining((doc) -> System.out.println(doc.getSourceAsString()));
```

5. Recordar añadir todos los import necesarios, para ello debéis poner el cursor encima de la clase y pulsar `alt+enter`.

```java
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
```

6. **Tarea:** Implementemos el método searchAuthor, en este caso la búsqueda será usando una wildcard.

## Ejecutando la aplicación

Una vez terminado el ejercicio vamos a ver cómo funciona para ello tenemos que seguir los siguientes pasos.

1. Arrancar el `docker-compose` con el comando `docker-compose up -d`
2. Ejecutar el comando `mvn clean install`.
3. Ejecutar el comando `mvn exec:java -Dexec.mainClass="com.alvaroagea.elk.practica3.App"`



 

