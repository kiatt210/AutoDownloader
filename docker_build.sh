#!/bin/bash
JAR_FILE=$1
VERSION=$2
echo "jar: $JAR_FILE"
echo "Version: $VERSION"
docker buildx build -f docker/Dockerfile --builder rpi-builder --platform linux/arm/v7 --build-arg="JAR_FILE=target/${JAR_FILE}" --tag 192.168.0.20:5000/auto-downloader:${VERSION} --push .
