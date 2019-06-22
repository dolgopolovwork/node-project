FROM openjdk:8u181-jre-alpine

COPY /target/submaster-node-run-1.0-SNAPSHOT-jar-with-dependencies.jar submaster.jar

ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.5.0/wait wait
RUN chmod +x wait
CMD wait && java -jar submaster.jar
