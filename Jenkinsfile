pipeline {
    agent any

    tools {
        maven 'Maven 3.9.6' // Jenkins'te yüklü Maven ismiyle aynı olmalı
        jdk 'jdk-17'         // Jenkins'te yüklü JDK ismiyle aynı olmalı
    }

    environment {
        MAVEN_OPTS = '-Dmaven.test.failure.ignore=false'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Test') {
            steps {
                sh 'mvn clean test'
            }
        }
        stage('Archive Reports') {
            steps {
                archiveArtifacts artifacts: 'test-output/ExtentReport.html', fingerprint: true
                archiveArtifacts artifacts: 'Screenshot/*.png', allowEmptyArchive: true
                junit 'target/surefire-reports/*.xml'
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
} 