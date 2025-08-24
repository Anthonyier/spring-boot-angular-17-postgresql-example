
pipeline {
    agent any

     parameters {
                    separator(name: "GITHUB_CONFIGURATION", sectionHeader: "GITHUB CONFIGURATION");
                    string(name: 'GITHUB_BRANCHDEV', defaultValue: 'master', description: 'GitHub Branch DEV name');
					string(name: 'GITHUB_BRANCHTEST', defaultValue: 'Test' , description: 'GitHub Branch TEST name');
					string(name: 'GITHUB_BRANCHSTAGING', defaultValue: 'Staging', description: 'GitHub Branch STAGING name');
                    string(name: 'GITHUB_URL', defaultValue: "https://github.com/Anthonyier/spring-boot-angular-17-postgresql-example.git", description: 'GitHub URL project');
                    
                    separator(name: "WEB_SERVER_CONFIGURATION", sectionHeader: "WEB SERVER CONFIGURATION");                                                                                           
					choice(name: 'DEPLOY_TO_DEV', choices: ['Yes','No'], description: 'Deploy DEV Environment?')
                    
					choice(name: 'DEPLOY_TO_TEST', choices: ['Yes','No'], description: 'Deploy TEST Environment?')			
					
					choice(name: 'DEPLOY_TO_STAGING', choices: ['Yes','No'], description: 'Deploy STAGING Environment?')					
					
					string(name: 'TARGET_BASE_PATH', defaultValue: "C:\\nginx\\html\\Angular20", description: 'Folder para Todos los ambientes')
               } 

    stages {
               
       
		 stage('Stop nginx') {
            steps {
                    modifyService("stop", "nginx");
            }
        }
		
		stage('Deploy environments') {
            parallel {
                stage('Deploy DEV environment') {
                    when { expression { return params.DEPLOY_TO_DEV == 'Yes' } }
                    steps {
                        dir('workspace-dev') {
                            git(branch: "${GITHUB_BRANCHDEV}", url: "${GITHUB_URL}")
                            dir('angular-17-client') {
                                bat 'npm install'
                                bat 'npx ng build --configuration production --base-href /dev/'
                            }
                            script {
								ensureFolderWithPermissions("${TARGET_BASE_PATH}\\dev")
                                emptyFolder("${TARGET_BASE_PATH}\\dev")
								copyFolder("${WORKSPACE}\\workspace-dev\\angular-17-client\\dist\\angular-17-crud\\browser\\*", "${TARGET_BASE_PATH}\\dev")
                            }
                        }
                    }
                }
                stage('Deploy TEST environment') {
                    when { expression { return params.DEPLOY_TO_TEST == 'Yes' } }
                    steps {
                        dir('workspace-test') {
                            git(branch: "${GITHUB_BRANCHTEST}", url: "${GITHUB_URL}")
                            dir('angular-17-client') {
                                bat 'npm install'
                                bat 'npx ng build --configuration production --base-href /test/'
                            }
                            script {
								ensureFolderWithPermissions("${TARGET_BASE_PATH}\\test")
                                emptyFolder("${TARGET_BASE_PATH}\\test")
								copyFolder("${WORKSPACE}\\workspace-test\\angular-17-client\\dist\\angular-17-crud\\browser\\*", "${TARGET_BASE_PATH}\\test")
                            }
                        }
                    }
                }
                stage('Deploy STAGING environment') {
                    when { expression { return params.DEPLOY_TO_STAGING == 'Yes' } }
                    steps {
                        dir('workspace-staging') {
                            git(branch: "${GITHUB_BRANCHSTAGING}", url: "${GITHUB_URL}")
                            dir('angular-17-client') {
                                bat 'npm install'
                                bat 'npx ng build --configuration production --base-href /staging/'
                            }
                            script {
								ensureFolderWithPermissions("${TARGET_BASE_PATH}\\staging")
                                emptyFolder("${TARGET_BASE_PATH}\\staging")
							    copyFolder("${WORKSPACE}\\workspace-staging\\angular-17-client\\dist\\angular-17-crud\\browser\\*", "${TARGET_BASE_PATH}\\staging")
                            }
                        }
                    }
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
def ensureFolderWithPermissions(String path) {
    // Crear carpeta si no existe y dar permisos de lectura y ejecución a todos
    String command = """
    if (-Not (Test-Path '${path}')) {
        New-Item -ItemType Directory -Path '${path}' | Out-Null
    }
    icacls '${path}' /grant 'Todos:(OI)(CI)RX' /T /C
    """
    powershell(returnStdout:true, script:command)
    println("Folder ensured with permissions: ${path}")
}