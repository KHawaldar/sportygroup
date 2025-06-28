# Use Java 17 base image
FROM eclipse-temurin:17-jdk-alpine

# Create a temporary directory inside the container
VOLUME /tmp

# Set the location of the JAR file to copy
ARG JAR_FILE=build/libs/*.jar

# Copy the JAR into the container
COPY ${JAR_FILE} app.jar

# Run the app
ENTRYPOINT ["java", "-jar", "/app.jar"]