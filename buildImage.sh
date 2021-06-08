#!/bin/bash

VERSION=${1}
DOCKERHUB_PASSWORD=${2}
DOCKERHUB_USERNAME=${3:-dusanbucan}

APP_NAME=nistagramuser

APP_IMAGE_NAME=${DOCKERHUB_USERNAME}/${APP_NAME}:${VERSION}

DOCKER_BUILDKIT=1 docker build \
-t "${APP_IMAGE_NAME}" \
--target nistagramUserMicroserviceRuntime \
--no-cache \
.

## copy build .jar to machine and to add to artifact repository
docker cp $APP_IMAGE_NAME:/nistagram-user.jar nistagram-user:${VERSION}.jar

docker login --username ${DOCKERHUB_USERNAME} --password=${DOCKERHUB_PASSWORD}
docker push "$APP_IMAGE_NAME"
