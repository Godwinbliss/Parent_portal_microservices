# Dockerfile for communication-service

# Use a lightweight OpenJDK 17 image as the base
FROM openjdk:17-jdk-slim

LABEL authors="ojima"
# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file of your Spring Boot application
# Assuming your JAR is named 'communication-service-0.0.1-SNAPSHOT.jar'
# You should build your Spring Boot application first using 'mvn clean package'
COPY target/communication-service-0.0.1-SNAPSHOT.jar app.jar

# Expose the port that the Spring Boot application runs on
EXPOSE 8084

# Set the entry point to run the Spring Boot application
# Use environment variables for Kafka and MongoDB hosts, which will be provided by Docker Compose
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.data.mongodb.host=mongodb", "--spring.kafka.bootstrap-servers=kafka:9092"]
