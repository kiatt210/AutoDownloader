#!/bin/bash
JAR_FILE=$1
VERSION=$2

cp ../../src/main/resources/secrets.json src/main/resources/
docker buildx build -f docker/Dockerfile --builder rpi-builder --platform linux/arm/v7 --build-arg="JAR_FILE=target/${JAR_FILE}" --build-arg="VERSION=${VERSION}" --tag 192.168.0.20:5000/auto-downloader:${VERSION} --push .
