FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/BakeBudget-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 443

CMD ["java", "-jar", "app.jar"]
