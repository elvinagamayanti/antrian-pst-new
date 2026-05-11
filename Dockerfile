FROM maven:3.9.9-eclipse-temurin-24 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:24-jdk
WORKDIR /app
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
COPY --from=build /app/target/antrian-0.0.1-SNAPSHOT.jar antrian.jar
EXPOSE 8080
ENTRYPOINT exec java $JAVA_OPTS -jar antrian.jar
