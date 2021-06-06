FROM maven:3.8.1-jdk-11 AS nistagramUserMicroserviceTest
ARG STAGE=test
WORKDIR /usr/src/server
COPY . .
