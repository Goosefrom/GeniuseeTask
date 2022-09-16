FROM openjdk:17
ADD target/cinema-docker.jar cinema-docker.jar
ENTRYPOINT ["java", "-jar", "cinema-docker.jar"]
EXPOSE 8080