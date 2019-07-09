FROM openjdk:8u181-jre-alpine

COPY /target/master-node-run-1.0-SNAPSHOT-jar-with-dependencies.jar master.jar
COPY wait wait
RUN chmod +x wait
CMD wait && java -jar master.jar
