FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/card-processing-service.jar /app/app.jar

EXPOSE 9999

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
