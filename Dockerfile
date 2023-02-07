WORKDIR /app

COPY src ./src

CMD ["mvnw", "spring-boot:run"]

ENTRYPOINT ["java", "-jar" , "/app/docker-challenge-0.0.1-SNAPSHOT.jar"]
