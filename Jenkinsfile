pipeline {
    agent any
    environment {
        gradle.publish.key = credentials('gradle.publish.key')
        gradle.publish.secret = credentials('gradle.publish.secret')
        pearxRepo = credentials('pearxRepo')
        pearxRepoUsername = env.pearxRepo_USR
        pearxRepoPassword = env.pearxRepo_PSW
    }
    stages {
        stage('checkout') {
            steps {
                checkout scm
            }
        }
        stage('build') {
            steps {
                sh './gradlew build'
            }
        }
        stage('deploy-develop') {
            when { branch 'develop' }
            steps {
                sh "./gradlew publishDevelop"
            }
        }

        stage('deploy-release') {
            when { branch 'master' }
            steps {
                sh "./gradlew publishRelease"
            }
        }
    }
}