FROM 653528873951.dkr.ecr.us-west-2.amazonaws.com/openjdk:17 AS build
RUN mkdir /app
RUN microdnf install findutils
COPY . /app
WORKDIR /app
RUN ./gradlew build --no-daemon

FROM 653528873951.dkr.ecr.us-west-2.amazonaws.com/openjdk:17-jdk-slim

RUN mkdir /app

COPY --from=build /app/build/libs/*SNAPSHOT.jar /app/app.jar

ENTRYPOINT ["java", "-Xms2G", "-Xmx2G", "-jar", "app/app.jar"]
