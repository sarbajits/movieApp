FROM eclipse-temurin:24-jdk

WORKDIR /app

COPY target/movieApp-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-XX:+UseContainerSupport","-Xms256m","-Xmx512m","-jar","/app/app.jar"]
