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
                    def workspace = pwd()
                    docker.image("maven:3-jdk-8-slim")
                            .inside("-v /var/run/docker.sock:/var/run/docker.sock -v /tmp:/tmp -e NODE_LOGS=${workspace}/logs -e NODE_TASKS=${workspace}/tasks") {
                        sh "mvn clean install"
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
