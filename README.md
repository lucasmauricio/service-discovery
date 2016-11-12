# service-discovery

PoC (Proof of Concept) project to test service registration and discovery in micrservices architecture.



## Trying it on

To try this project on, we suggest to use Docker.

1) download de git repository

2) build the Java application

```shell
mvn clean package
```

3) create the docker image

```shell
docker build -t my_registrator .
```

4) run your docker image

```shell
docker run -d --name my_registrator -p 9000:8080 registrator
```

5) just access the base URL to see data

Type this URL on your web browser: http://localhost:9000/assets

or

```shell
curl http://localhost:9000/assets
```

### PS: some useful hints to deal with Docker

To see running containers:
```shell
docker ps
```

To see all containers:
```shell
docker ps -a
```

To stop a container:
```shell
docker stop my_registrator
```

To start an existing container:
```shell
docker start my_registrator
```

To see the logs (STDIN) of a container in the terminal:
```shell
docker logs start my_registrator
```

To attach the logs (STDIN) of a container to the current terminal:
```shell
docker logs -f start my_registrator
```

## We got some tips at:

https://spring.io/guides/gs/spring-boot-docker/

https://blog.tutum.co/2015/06/03/docker-angularjs-and-tutum-part-1/
