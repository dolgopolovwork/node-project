FROM openjdk:8u181-jre-alpine

WORKDIR /opt/slave

COPY /target/slave-node-run-1.0-SNAPSHOT-jar-with-dependencies.jar /opt/slave/slave.jar

## Wait for script (see https://github.com/ufoscout/docker-compose-wait/)

ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.5.0/wait /opt/slave/wait
RUN chmod +x /opt/slave/wait
CMD /opt/slave/wait && java -jar slave.jar --slaveConfigPath config/slave-server-config.json
