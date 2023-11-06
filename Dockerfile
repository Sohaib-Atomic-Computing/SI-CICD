FROM openjdk:8-jdk-alpine
WORKDIR /
COPY target/iconnect-0.0.1-SNAPSHOT.jar iconnect-0.0.1-SNAPSHOT.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "iconnect-0.0.1-SNAPSHOT.jar"]