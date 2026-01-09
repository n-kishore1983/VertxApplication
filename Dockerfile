FROM eclipse-temurin:23-jre-alpine

WORKDIR /app

COPY target/VertxApplication-1.0-SNAPSHOT-fat.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]


