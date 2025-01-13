# Step 1: Build the Java application
FROM 653528873951.dkr.ecr.us-west-2.amazonaws.com/docker-hub/library/eclipse-temurin:17-jammy AS build
RUN mkdir /app
COPY . /app
WORKDIR /app
RUN ./gradlew build --no-daemon

# Step 2: Create the runtime image
FROM 653528873951.dkr.ecr.us-west-2.amazonaws.com/docker-hub/library/nginx:1.27

# Install Java runtime
RUN apt-get update && apt-get install -y openjdk-17-jre-headless supervisor

# Copy the built Java application
RUN mkdir /app
COPY --from=build /app/build/libs/lol-api-service-0.0.1-SNAPSHOT.jar /app/app.jar

# Copy Nginx configuration
COPY config/nginx.conf /etc/nginx/nginx.conf

# Configure Supervisor to manage both Nginx and the Java service
COPY config/supervisord.conf /etc/supervisor/supervisord.conf

# Expose the required port for Nginx (App Runner uses this)
EXPOSE 8080

# Start Supervisor
CMD ["/usr/bin/supervisord", "-c", "/etc/supervisor/supervisord.conf"]
