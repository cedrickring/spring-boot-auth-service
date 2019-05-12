FROM maven:3.6-jdk-8 as build

COPY . .
RUN mvn package

FROM openjdk:8-jre-slim
WORKDIR /home

COPY --from=build target/auth-service.jar /home

EXPOSE 8080

CMD ["java", "-jar", "auth-service.jar"]
