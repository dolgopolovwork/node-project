FROM openjdk:8u181-jre-alpine

WORKDIR /opt/master

COPY /target/master-node-run-1.0-SNAPSHOT-jar-with-dependencies.jar /opt/master/master.jar

CMD ["java","-jar","master.jar","--masterConfigPath","config/master-server-config.json"]
