pipeline {
    agent any

     parameters {
                    separator(name: "GITHUB_CONFIGURATION", sectionHeader: "GITHUB CONFIGURATION");
                    string(name: 'GITHUB_BRANCH', defaultValue: 'master', description: 'GitHub Branch name');
                    string(name: 'GITHUB_URL', defaultValue: "https://github.com/Anthonyier/spring-boot-angular-17-postgresql-example.git", description: 'GitHub URL project');
                    
                    separator(name: "WEB_SERVER_CONFIGURATION", sectionHeader: "WEB SERVER CONFIGURATION");                                                                                           
                    string(name: 'TARGET_PATH', defaultValue: "C:\\nginx\\html\\Angular20", description: 'Target folder path');                                                                                                                        
               } 

    stages {
        stage('Stop nginx') {
            steps {
                    modifyService("stop", "nginx");
            }
        }        
        stage('Checkout github project') {
            steps {
                git(branch:"${GITHUB_BRANCH}", url:"${GITHUB_URL}")
            }
        }
        stage('Install dependencies') {
            steps {
			    dir('angular-17-client')
				{
                bat 'npm install'
				}
            }
        }
        stage('Build project') {
            steps {
			 dir('angular-17-client') {
                bat 'npm run build'
				}
            }
        }
        stage('Clean-Up old files') {
            steps {
                script {        
                        emptyFolder("${TARGET_PATH}");
                }
            }
        }
        stage('Copy new files') {
            steps {
                script {
                        copyFolder("${WORKSPACE}\\angular-17-client\\dist\\angular-17-crud\\*", "${TARGET_PATH}")
                }
            }
        }
        stage('Start nginx') {
            steps {
                    modifyService("start", "nginx");
            }
        }                        
    }
    post {
        always {
            echo 'Proceso de despliegue del frontend completado.'
        }
        failure {
            echo 'El despliegue falló, revisa los logs para más detalles.'
        }
    }
}
/*************************************************************************************************
*                                FUNCTIONS SECTION                                               *
**************************************************************************************************/
def emptyFolder(String path) {
     
     String command = "Remove-Item -Path " + path + "\\* -Include *.* -Force";       
     powershell(returnStdout:true, script:command);
     println('CleanUp folder done.');
}

def copyFolder(String sourcePath, String targetPath) {
     
    String command = "Copy-Item -Path '" + sourcePath + "' -Destination '" + targetPath + "' -Recurse -Force";
    powershell(returnStdout:true, script:command);
    println('Copy folder done.');
}

def modifyService(String action, String serviceName) {
    
     String command = "net " + action + " " + serviceName;  
    powershell(returnStdout:true, script:command);
}