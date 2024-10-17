# Use a base image with OpenJDK
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the packaged JAR file from the target directory into the container
COPY target/gestion-station-ski-1.0.jar my-spring-app.jar
# Copy the application.properties file into the image
COPY src/main/resources/application.properties application.properties

# Expose the application port (default is 8080)
EXPOSE 8080

# Set the command to run when the container starts
ENTRYPOINT ["java", "-jar", "my-spring-app.jar"]