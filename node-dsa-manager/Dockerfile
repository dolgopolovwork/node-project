FROM openjdk:8u181-jre-alpine

COPY /target/node-dsa-manager-1.0-SNAPSHOT-jar-with-dependencies.jar dsa-server.jar
COPY wait wait
RUN chmod +x wait
CMD wait && java -jar dsa-server.jar
