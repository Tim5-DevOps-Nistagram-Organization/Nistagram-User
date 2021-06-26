#!/bin/sh

./consul agent -config-dir=/consul-config &

java -jar nistagram-user.jar