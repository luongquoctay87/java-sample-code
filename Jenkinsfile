pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'echo "Hello World"'
                sh '''
                    echo "==================> "
                    mvn clean install
                    pwd
                '''
            }
        }
    }
}
