pipeline {
    agent any
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

    }
}