#FROM openjdk:17-slim
#LABEL authors="TRUNG KIEN"
#
## Set the working directory
#WORKDIR /app
#
#COPY target/DatingApp-0.0.1-SNAPSHOT.jar app.jar
#
#ENTRYPOINT ["java", "-jar", "app.jar"]
FROM maven:3-openjdk-17 AS build
WORKDIR /app

COPY . .
RUN mvn clean package -DskipTests


# Run stage

FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=build /app/target/DrComputer-0.0.1-SNAPSHOT.war drcomputer.war
EXPOSE 8088

ENTRYPOINT ["java","-jar","drcomputer.war"]