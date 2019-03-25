pipeline {
    agent any
    environment {
        GRADLE_PUBLISH_KEY = credentials('gradle.publish.key')
        GRADLE_PUBLISH_SECRET = credentials('gradle.publish.secret')
        RU_PEARX_REPO = credentials('pearxRepo')
    }
    stages {
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