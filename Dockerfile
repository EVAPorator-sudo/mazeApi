FROM eclipse-temurin:25-jdk-jammy
WORKDIR /app
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]