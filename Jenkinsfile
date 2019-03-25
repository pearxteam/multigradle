pipeline {
    agent any
    stages {
        stage('build') {
            steps {
                sh './gradlew build'
            }
        }
        stage('deploy-develop') {
            when { branch 'develop' }
            steps {
                withCredentials([file(credentialsId: 'gradle-secret-file', variable: 'GRADLE_PRIVATE_PROPERTIES_PATH')]) {
                    sh "./gradlew publishDevelop -PprivatePropertiesPath=${GRADLE_PRIVATE_PROPERTIES_PATH}"
                }
            }
        }

        stage('deploy-release') {
            when { branch 'master' }
            steps {
                withCredentials([file(credentialsId: 'gradle-secret-file', variable: 'GRADLE_PRIVATE_PROPERTIES_PATH')]) {
                    sh "./gradlew publishRelease -PprivatePropertiesPath=${GRADLE_PRIVATE_PROPERTIES_PATH}"
                }
            }
        }
    }
}