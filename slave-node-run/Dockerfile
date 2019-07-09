FROM openjdk:8u181-jre-alpine

COPY /target/slave-node-run-1.0-SNAPSHOT-jar-with-dependencies.jar slave.jar
COPY wait wait
RUN chmod +x wait
CMD wait && java -jar slave.jar
