# Práctica final. Montando un stack completo.

El objetivo de esta práctica final es plantear una ejercicio que os permita seguir explorando las capacidades del stack de Elastic. Para ello vamos a plantear el siguiente problema.

1. Tenemos una arquitectura compuesta por los siguientes elementos:
   - **Un servidor Apache.**
     - Monitorizar el estado del servicio.
     - Capturar los logs tanto de acceso, como de error.
   - **Un servidor NGINX**
     - Monitorizar el estado del servicio.
     - Capturar los logs tanto de acceso, como de error.
   - **Un servidor MySQL**
     - Monitorizar el estado del servicio.
     - Capturar los logs tanto de error, como queries lentas.

2. Además queremos monitorizar el sistema.
   - Las estadísticas y logs del sistema.
   - Las estadísticas y logs de docker.

3. Por otro lado también queremos analizar el trafico de red, usando Packetbeat, de ICMP, DNS, Flows, HTTP y MySQL.

4. Por último queremos monitorizar el estado de todos los elementos de elastic, los Beats, Kibana y ElasticSearch.

## Ejercicio 1. Montar el compose.

Lo primero que debes hacer es montar el docker compose que lance los tres servicios. 

1. Para el servicio de NGINX, puedes utilizar el ejemplo que vimos en la practica 8.
2. Para el servicio de Apache, contáis con una imagen docker en la carpeta apache2.
3. Pare el servicio de MySQL, contáis con una imagen en la carpeta mysql.
4. Para ayudaros con la ejecución de MySQL os pongo este ejemplo para que lo uses como plantilla.

```yaml
#Mysql container
  mysql:
    container_name: mqsql
    hostname: mysql
    build: ./mysql
    environment:
      - "MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD}"
    networks: ['stack']
    #Expose port 3306 to allow users to connect and perform operations. These will be picked up by Packetbeat, Filebeat and Metricbeat
    ports: ['3306:3306']
    command: bash -c "chown -R mysql:mysql /var/log/mysql && exec /entrypoint.sh mysqld"
    volumes:
      #Use named volume so mysql data is persisted across restart
      - mysqldata:/var/lib/mysql/
      #Logs are mounted to a relative path. These are also accessed by Filebeat and consumed by the Mysql module
      - ./logs/mysql:/var/log/mysql/
```

5. Cuando consigáis tener los servicios arrancados. Empezar con el ejercicio 2.

## Ejercicio 2. Capturar los logs.

En este ejercicio tendréis que capturar todos los logs necesarios. Hazte con todos.

1. Los logs de sistema, si tu maquina es Linux se suelen encontrar en `/var/log/host/system.log`, pero debe verificarlo.
2. Los logs de docker, depende de como instalaste docker, hay muchos ejemplos en ejercicios anteriores.
3. Cuando arranques los servicios Apache, NGINX y MySQL, tendrás que indicarles una carpeta de binding para pode compartir la información con el servicio filebeat.
4. Repasa los modulos disponibles para cada componente, si utilizas los modelos en vez de la entrada de tipo Log genérica podrás extraer mucha más información. Lee más sobre los módulos de Filebeat [aquí](https://www.elastic.co/guide/en/beats/filebeat/current/filebeat-modules-overview.html).

## Ejercicio 3. Monitorizando la maquina.

En este caso es fácil, ya hemos hecho ejemplos de como monitorizar docker. Por lo que sólo tendríamos que seguir los mismos pasos.

## Ejercicio 4. Analisis de paquetes.

 Ahora vamos a utilizar Packetbeat, para capturar diferentes paquetes de red.

1. Como Packetbeat no hemos hecho ningún ejemplo os pongo más detalles aquí.
2. Para poder arrancarlo necesitamos a concederle a docker algunos permisos especiales, para que nos deje acceder a las funciones de red.
3. A modo de ejemplo os poco un configuración de Packetbeat.

```yaml
packetbeat:
    container_name: packetbeat
    hostname: packetbeat
    image: "docker.elastic.co/beats/packetbeat:${ELASTIC_VERSION}"
    volumes:
      - ./config/beats/packetbeat/packetbeat.yml:/usr/share/packetbeat/packetbeat.yml
    # Packetbeat needs some elevated privileges to capture network traffic.
    # We'll grant them with POSIX capabilities.
    cap_add: ['NET_RAW', 'NET_ADMIN']
    # Use "host mode" networking to allow Packetbeat to capture traffic from
    # the real network interface on the host, rather than being isolated to the
    # container's virtual interface.
    network_mode: host
    command: packetbeat -e strict.perms=false
```

4. Una vez que tengamos el docker compose que usaremos para Packetbeat debería ser similar a este.

```yaml
#We monitor any devices on the host OS. For windows and OSX this is the VM hosting docker.
packetbeat.interfaces.device: any
packetbeat.flows:
  enabled: true
  timeout: 30s
  period: 10s
packetbeat.protocols.icmp:
  enabled: true
packetbeat.protocols.dns:
  enabled: true
  ports: [53]
  include_authorities: true
  include_additionals: true
#We monitor any traffic to kibana, apache, ngnix and ES
packetbeat.protocols.http:
  enabled: true
  ports: [9200, 80, 8080, 8000, 5000, 8002, 5601]
  send_headers: true
  send_all_headers: true
  split_cookie: true
  send_request: false
  send_response: false
  transaction_timeout: 10s
packetbeat.protocols.mysql:
  ports: [3306]
output.elasticsearch:
  hosts: ["localhost:9200"]
logging.to_files: false
logging.to_files: false
```

5. Para más información sobre Packetbeat, podéis ir a la documentación oficial de [Elastic](https://www.elastic.co/guide/en/beats/packetbeat/current/index.html).

## Ejercicio 5. Comprobar el estado.

En este caso, sólo debemos añadir más servicios al fichero de configuración que teníamos en otros ejercicios. Sólo hay que tener cuidad de MySQL, en este caso la configuración debería parecerse a esta.

```yaml
- type: tcp
  enabled: true
  schedule: '@every 5s'
  hosts: ["tcp://mysql:3306"]
```

## Ejercicio 6. Prueba, crea, experimenta.

Con todas estas fuentes de datos se pueden hacer muchas cosas. Intenta crear un dashboard que saque todo el potencial del sistema, utiliza Timelion para combinar las métricas, intenta usar los datos geoposicionados para extraer más valor a la información, lanza más servicios...

## Dudas y comentarios

Mandarme por correo vuestras dudas, o en los issues de GitHub intentaré resolverlas ASAP.



