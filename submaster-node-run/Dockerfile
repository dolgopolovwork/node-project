FROM openjdk:8u181-jre-alpine

COPY /target/submaster-node-run-1.0-SNAPSHOT-jar-with-dependencies.jar submaster.jar
COPY wait wait
RUN chmod +x wait
CMD wait && java -jar submaster.jar
