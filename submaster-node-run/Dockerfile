FROM openjdk:8-jdk

WORKDIR /opt/submaster

COPY /target/submaster-node-run-1.0-SNAPSHOT-jar-with-dependencies.jar /opt/submaster/submaster.jar

ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.5.0/wait /opt/submaster/wait
RUN chmod +x /opt/submaster/wait
CMD /opt/submaster/wait && java -jar submaster.jar --masterConfigPath config/submaster-server-config.json --slaveConfigPath config/connect-config.json
