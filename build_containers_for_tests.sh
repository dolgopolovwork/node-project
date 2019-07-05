#!/usr/bin/env bash
# install all modules, run unit tests, create task jars
mvn clean install -DskipTests=true -Dcobertura.skip=true &&
# create runnable jars
mvn clean compile assembly:single -f master-node-run/pom.xml &&
mvn clean compile assembly:single -f slave-node-run/pom.xml &&
mvn clean compile assembly:single -f submaster-node-run/pom.xml &&
# create docker containers for testing
docker build -f master-node-run/Dockerfile -t test/node-project-master ./master-node-run &&
docker build -f slave-node-run/Dockerfile -t test/node-project-slave ./slave-node-run &&
docker build -f submaster-node-run/Dockerfile -t test/node-project-submaster ./submaster-node-run
