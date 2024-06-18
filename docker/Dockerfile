FROM eclipse-temurin:19.0.2_7-jdk
ARG VERSION
ARG JAR_FILE
ENV VERSION=$VERSION
ENV TZ="Europe/Budapest"
ENV API_KEY="<EMBY_API_KEY>"
ENV USER_ID="<EMBY_USER_ID>"

RUN mkdir -p /home/seeder/torrents/
WORKDIR /home/seeder/

COPY src/main/resources/secrets.json secrets.json
COPY target/libs libs
COPY $JAR_FILE app.jar

CMD ["java", "-XX:ArchiveClassesAtExit=/home/seeder/dynamic-cds.jsa", "-jar", "/home/seeder/app.jar"]

ENTRYPOINT  ["java", "-XX:SharedArchiveFile=dynamic-cds.jsa",  "-jar", "/home/seeder/app.jar"]
#dokcer build -f Dockerfile --tag kiatt210/testing:auto-downloader --push
#docker run --name auto-downloader -v /opt/docker/emby/config/data:/emby auto-downloader:1.6
