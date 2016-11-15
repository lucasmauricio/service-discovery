
FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ADD target/registrator-0.0.1-SNAPSHOT.jar registrator.jar
RUN sh -c 'touch /registrator.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /registrator.jar" ]
