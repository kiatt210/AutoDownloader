FROM eclipse-temurin:19.0.2_7-jdk
ENV TZ="Europe/Budapest"
ENV API_KEY="<EMBY_API_KEY>"
ENV USER_ID="<EMBY_USER_ID>"
RUN mkdir -p /home/seeder/torrents/
WORKDIR /home/seeder/

COPY libs libs
ADD AutoDownloader.jar .

RUN ["java", "-XX:ArchiveClassesAtExit=/home/seeder/dynamic-cds.jsa" ,"--enable-preview", "-jar", "/home/seeder/AutoDownloader.jar"]

ENTRYPOINT  ["java", "-XX:SharedArchiveFile=dynamic-cds.jsa",  "--enable-preview", "-jar", "/home/seeder/AutoDownloader.jar"]
#dokcer build -f Dockerfile --tag kiatt210/testing:auto-downloader --push
#docker run --name auto-downloader -v /opt/docker/emby/config/data:/emby auto-downloader:1.6
