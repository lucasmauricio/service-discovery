
FROM frolvlad/alpine-oraclejdk8:slim

VOLUME /tmp

ADD target/registrator-0.0.1-SNAPSHOT.jar registrador.jar

RUN sh -c 'touch /registrador.jar'

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/registrador.jar"]
