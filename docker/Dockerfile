FROM eclipse-temurin:19_36-jdk
ENV TZ="Europe/Budapest"
RUN mkdir -p /home/seeder/torrents/
WORKDIR /home/seeder/

COPY libs libs
ADD AutoDownloader.jar .

RUN ["java", "-XX:ArchiveClassesAtExit=/home/seeder/dynamic-cds.jsa" ,"--enable-preview", "-jar", "/home/seeder/AutoDownloader.jar"]

ENTRYPOINT  ["java", "-XX:SharedArchiveFile=dynamic-cds.jsa",  "--enable-preview", "-jar", "/home/seeder/AutoDownloader.jar"]
#dokcer build -f Dockerfile --tag kiatt210/testing:auto-downloader --push
