# Práctica 4a: usando Match queries

En esta practica vamos a probar a utilizar la match queries. La primera parte de la practica consistirá en preparar el entorno. Después crearemos un MVP, que de la minima funcionalidad que se solicita. Por último, que haremos sera complicar un poco más las consultas para comprobar cuales son los resultados.

## Ejercicio 1. Añadiendo el proyecto

En este apartado vamos a añadir el proyecto a IntelliJ para que sea mucho más fácil trabajar con el código Java.

1. Lo primero que vamos a hacer es chequear que el entorno funciona. Para ello ejecutamos `mvn clean install` desde la carpeta de la práctica. 
2. Este comando se descargará las dependencias y compilara el proyecto, el resultado debería ser correcto.
3. Ahora para limpiar la carpeta ejecutamos `mvn clean`.
4. Ahora abrimos IntelliJ,
5. Pulsamos en `Import project`. 
6. Seleccionamos la carpeta `Practica4a`.
7. Ahora seleccionamos la opción `Import project from external model`.
8. Seleccionamos `maven` y pulsamos `next`.
9. Marcamos la casilla `Import Maven projects automatically`.
10. Pulsamos `next`.
11. Pulsamos `next`.
12. Pulsamos `next`.
13. Pulsamos `finish`.
14. Ya esta ahora ya tenemos nuestro proyecto importado en IntelliJ. 

## Ejercicio 2. Introducir el dataset

En la carpeta de la practica hay un dataset con las obras de William Shakespeare, el esquema de este dataset tiene el siguiente formato.

```json
{
    "line_id": "int",
    "play_name": "String",
    "speech_number": "int",
    "line_number": "String",
    "speaker": "String",
    "text_entry": "String",
}
```

1. Lo primero que vamos a hacer el levantar ElasticSearch, para ello ejecutaremos el comando `docker-compose up`.
2. Después de que elastic search haya arrancado, crearemos el indice donde vamos a guardar los datos con el siguiente comando.

```bash
curl -X PUT "localhost:9200/shakespeare" -H 'Content-Type: application/json' -d'
{
  "mappings": {
    "properties": {
    "line_id": {"type": "integer"},
    "speech_number": {"type": "integer"}
    }
  }
}
'
```

3. Cargaremos los datos en Elastic Search.

```bash
curl -H 'Content-Type: application/x-ndjson' -XPOST 'localhost:9200/shakespeare/_bulk?pretty' --data-binary @shakespeare.json
```

4. Este proceso se tiene que repetir cada vez que ejecutemos el comando `docker-compose down`.

## Ejercicio 3. Creando el MVP.

Una vez que el entorno esta prepara nuestro objetivo es rellenar la clase `Practica4AController`. Para esta primera iteración vamos a simplificar el proceso. 

1. El método `search` tiene que hacer una Match Query sólo del campo `text_entry`. 
2. El método `get` sólo debe recuperar la entrada con el `line_id` introducido. 
3. El método `query` ejecutara una llamada Query String.

Por último, algunas notas que os serán utilidad:

- Recordar usar las constantes de la clase controller para hacer referencia al nombre de los campos.
- Repasar la practica3 donde encontrareis un ejemplo de match query.
- Consultar la referencia de ElasticSearch.

## Ejercicio 4. Mejorando las busqueda

Con el sistema funcionando es hora de hacer las consultas un poco más complejas.

1. El método `search` ahora tendrá en cuenta el titulo de la obra `play_name` y el nombre del personaje `speaker`. Para hacer esto utilizaremos una DIS_MAX query.
2. El método `get` debe recuperar la entrada con el `line_id` y las 5 lineas anteriores y posteriores. De esta forma daremos más contexto al texto.
3.  El método `query` no será modificado.





 

