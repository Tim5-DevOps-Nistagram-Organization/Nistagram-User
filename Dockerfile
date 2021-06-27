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


FROM openjdk:11.0-jdk as nistagramUserMicroserviceRuntimeDev
COPY ./entrypoint.sh /entrypoint.sh
COPY ./consul-client.json /consul-config/consul-client.json
RUN apt-get install -y \
    curl \
    unzip \
    && curl https://releases.hashicorp.com/consul/1.9.5/consul_1.9.5_linux_amd64.zip -o consul.zip \
    && unzip consul.zip \
    && chmod +x consul \
    && rm -f consul.zip \
    && chmod +x /entrypoint.sh \
    && mkdir consul-data \
    && apt-get remove -y \
    curl \
    unzip

COPY --from=nistagramUserMicroserviceBuild /usr/src/server/target/*.jar nistagram-user.jar
EXPOSE 8080
CMD ["/entrypoint.sh"]