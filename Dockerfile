FROM maven:3.8.1-jdk-11 AS nistagramUserMicroserviceTest
ARG STAGE=test
WORKDIR /usr/src/server
COPY . .

FROM maven:3.8.1-jdk-11  AS nistagramUserMicroserviceBuild
ARG STAGE=dev
WORKDIR /usr/src/server
COPY . .
RUN mvn package -Pdev -DskipTests

FROM openjdk:11.0-jdk as nistagramUserMicroserviceRuntime
COPY --from=nistagramUserMicroserviceBuild /usr/src/server/target/*.jar nistagram-user.jar
CMD java -jar nistagram-user.jar
