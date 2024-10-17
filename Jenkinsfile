pipeline {
    agent any
        environment {
            dockerComposeFile = 'docker-compose.yml'
        }
    stages {


        stage('mvn clean') {
                    steps {
                        sh 'mvn clean'
                    }
                }

        stage('Maven Compile') {
            steps {
                sh 'mvn compile'
            }
        }

        stage('Unit Testing') {
                    steps {
                        sh 'mvn test'
                    }
                }


        stage('Deploying with Docker Compose') {
                    steps {
                        script {
                                sh "docker-compose -f ${dockerComposeFile} up -d"
                               }
                            }
                        }


        stage("SonarQube Analysis") {
                                    steps {
                                        script {
                                            withSonarQubeEnv('SonarQube') {
                                                sh 'mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent -Djacoco.version=0.8.8 test jacoco:report'
                                                sh "mvn sonar:sonar -Dsonar.projectKey=supplier_anouer -Dsonar.projectName='supplier_anouer' -Dsonar.jacoco.reportPaths=target/site/jacoco/jacoco.xml"

                                            }
                                        }
                                    }
                                }

    }
}