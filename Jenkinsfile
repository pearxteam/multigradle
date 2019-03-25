pipeline {
    stages {
        stage('checkout') {
            checkout scm
        }
        stage('build') {
            sh './gradlew build'
        }
        withCredentials([usernamePassword(credentialsId: 'pearxRepo', passwordVariable: 'pearxRepoPassword', usernameVariable: 'pearxRepoUsername'), string(credentialsId: 'gradle.publish.key', variable: 'gradle.publish.key'), string(credentialsId: 'gradle.publish.secret', variable: 'gradle.publish.secret')]) {
            stage('deploy-develop') {
                when { branch 'develop' }
                sh "./gradlew publishDevelop"
            }

            stage('deploy-release') {
                when { branch 'master' }
                sh "./gradlew publishRelease"
            }
        }
    }
}