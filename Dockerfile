FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar
ENV JAVA_OPTS=""
EXPOSE 8080
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar --notifier.uri=http://ec2-35-158-112-200.eu-central-1.compute.amazonaws.com:8080/v1/update" ]