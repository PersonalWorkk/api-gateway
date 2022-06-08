FROM openjdk:16-alpine3.13


RUN apk update && apk add bash


WORKDIR /app

RUN ./gradlew clean
RUN ./gradlew build

COPY gradle/ gradle

COPY gradlew build.gradle settings.gradle ./

COPY src ./src
RUN export ENV=prod

RUN ./gradlew assemble


ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","build/libs/api-gateway-0.0.1-SNAPSHOT.jar"]