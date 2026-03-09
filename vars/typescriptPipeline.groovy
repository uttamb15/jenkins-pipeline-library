def call() {

    pipeline {
        //We can use any agent like linux or windows
        agent any

        tools {
            nodejs 'NodeJS'
        }

        environment {
            BASE_OUTPUT_DIR = 'C:\\ts-js-output'
            CURRENT_BRANCH = "${env.BRANCH_NAME}"
            LOCAL_OUTPUT_DIR = "C:\\ts-js-output\\${env.BRANCH_NAME}"
        }

        stages {

            stage('Checkout Code') {
                steps {
                    echo "Checked out branch: ${env.BRANCH_NAME}"
                }
            }

            stage('Install Dependencies') {
                steps {
                    bat 'npm install'
                }
            }

            stage('Compile TypeScript') {
                steps {
                    bat 'npx tsc'
                }
            }

            stage('Store JS Locally (Branch-wise)') {
                steps {
                    bat """
                        echo Storing compiled JS for branch: %CURRENT_BRANCH%

                        if not exist "%BASE_OUTPUT_DIR%" (
                            mkdir "%BASE_OUTPUT_DIR%"
                        )

                        if not exist "%LOCAL_OUTPUT_DIR%" (
                            mkdir "%LOCAL_OUTPUT_DIR%"
                        )

                        copy /Y dist\\*.js "%LOCAL_OUTPUT_DIR%\\"
                    """
                }
            }

        }

        post {
            success {
                echo "Build successful for branch: ${env.BRANCH_NAME}"
            }
            failure {
                echo "Build failed for branch: ${env.BRANCH_NAME}"
            }
        }

    }

}
