FROM maven:3.9-eclipse-temurin-17 AS builder
#3.9.5-openjdk-17-slim AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml first for better Docker layer caching
COPY pom.xml .

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src/ ./src/

# Build the plugin
RUN mvn clean package # -DskipTests
