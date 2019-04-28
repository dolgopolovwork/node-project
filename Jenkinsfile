pipeline {
    options {
        skipDefaultCheckout()
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }
    agent any
    stages {
        stage('Tests') {
            steps {
                script {
                    checkout scm
                    docker.image("maven:3-jdk-8-slim ")
                            .inside("-v /var/run/docker.sock:/var/run/docker.sock -v /tmp:/tmp") {
                        sh "mvn clean install"
                        sh "build_test_containers.sh"
                        sh "mvn test-compile failsafe:integration-test failsafe:verify"
                    }
                }
            }
            post {
                cleanup {
                    cleanWs()
                }
            }
        }
    }
}
