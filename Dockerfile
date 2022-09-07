FROM 653528873951.dkr.ecr.us-west-2.amazonaws.com/openjdk:11 AS build
RUN mkdir /app
COPY . /app
WORKDIR /app
RUN ./gradlew build --no-daemon

FROM 653528873951.dkr.ecr.us-west-2.amazonaws.com/openjdk:11-jre-slim

RUN mkdir /app

COPY --from=build /app/build/libs/*SNAPSHOT.jar /app/app.jar

ENTRYPOINT ["java", "-Xms1G", "-Xmx1G", "-jar", "app/app.jar"]
