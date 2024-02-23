#
# Build stage
#
FROM maven:3.8.2-jdk-11 AS build
COPY . .
RUN mvn clean package -DskipTests

#
# Package stage
#
FROM openjdk:11-jdk-slim
COPY --from=build /target/demo-0.0.1-SNAPSHOT.jar demo.jar
# ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java","-jar","demo.jar"]

#FROM openjdk:11-jdk-slim
#
## Create app directory and set working directory
#WORKDIR /app
#
## Copy dependencies (adjust location if needed)
#COPY pom.xml ./
#RUN mvn dependency:copy-dependencies
#
## Copy your application classes and resources
#COPY src/main/java/com/limiter/demo ./
#COPY src/main/resources ./
#
## Expose container port (adjust if needed)
#EXPOSE 8080
#
## Build a JAR from your classes and resources
#RUN mvn package
#
## Entrypoint to run your application
#ENTRYPOINT ["java", "-jar", "target/demo.jar"]
