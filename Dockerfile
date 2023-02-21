FROM openjdk:8-jdk-alpine

WORKDIR /app

COPY ./target/*.jdk /app/

CMD ["mvnw", "spring-boot:run"]

ENTRYPOINT ["java", "-jar", "demo-0.0.1-SNAPSHOT.jar"]
