FROM eclipse-temurin:17.0.1_12-jre
EXPOSE 8080
WORKDIR /app
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
