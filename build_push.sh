#!/usr/bin/env bash
TAG="1.0"
NODE_MASTER_DOCKER_TAG="babobka/node-project-master:$TAG"
NODE_SLAVE_DOCKER_TAG="babobka/node-project-slave:$TAG"
NODE_SUBMASTER_DOCKER_TAG="babobka/node-project-submaster:$TAG"
# install all modules, run unit tests, create task jars
 mvn clean install &&
# create runnable jars
mvn clean compile assembly:single -f master-node-run/pom.xml &&
mvn clean compile assembly:single -f slave-node-run/pom.xml &&
mvn clean compile assembly:single -f submaster-node-run/pom.xml &&
# create docker containers for testing
docker build -f master-node-run/Dockerfile -t test/node-project-master ./master-node-run &&
docker build -f slave-node-run/Dockerfile -t test/node-project-slave ./slave-node-run &&
docker build -f submaster-node-run/Dockerfile -t test/node-project-submaster ./submaster-node-run &&
# run integration tests
mvn test-compile failsafe:integration-test failsafe:verify &&
# build containers for release
docker build -f master-node-run/Dockerfile -t ${NODE_MASTER_DOCKER_TAG} ./master-node-run &&
docker build -f slave-node-run/Dockerfile -t ${NODE_SLAVE_DOCKER_TAG} ./slave-node-run &&
docker build -f submaster-node-run/Dockerfile -t ${NODE_SUBMASTER_DOCKER_TAG} ./submaster-node-run &&
# push containers
docker push ${NODE_MASTER_DOCKER_TAG} &&
docker push ${NODE_SLAVE_DOCKER_TAG} &&
docker push ${NODE_SUBMASTER_DOCKER_TAG}
