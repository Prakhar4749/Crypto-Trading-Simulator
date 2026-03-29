# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the entire backend directory (Parent POM, shared-common, and all services)
COPY . .

# Build ONLY this specific service and its local dependencies (like shared-common)
RUN mvn clean package -pl service-registry -am -DskipTests

# Stage 2: Create the runtime container
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the built .jar file from the target folder of this specific module
COPY --from=build /app/service-registry/target/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
