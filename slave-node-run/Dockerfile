FROM openjdk:8u181-jre-alpine

COPY /target/slave-node-run-1.0-SNAPSHOT-jar-with-dependencies.jar slave.jar

## Wait for script (see https://github.com/ufoscout/docker-compose-wait/)

ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.5.0/wait wait
RUN chmod +x wait
CMD wait && java -jar slave.jar
