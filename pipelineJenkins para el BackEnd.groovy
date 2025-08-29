pipeline {
    agent any

    parameters {
        // GITHUB
        separator(name: "GITHUB_CONFIGURATION", sectionHeader: "GITHUB CONFIGURATION");
        string(name: 'GITHUB_BRANCHDEV', defaultValue: 'master', description: 'GitHub Branch DEV name');
        string(name: 'GITHUB_BRANCHTEST', defaultValue: 'Test' , description: 'GitHub Branch TEST name');
        string(name: 'GITHUB_BRANCHSTAGING', defaultValue: 'Staging', description: 'GitHub Branch STAGING name');
        string(name: 'GITHUB_URL', defaultValue: "https://github.com/Anthonyier/spring-boot-angular-17-postgresql-example.git", description: 'GitHub URL project');

        // SERVIDORES
        separator(name: "WEB_SERVER_CONFIGURATION", sectionHeader: "WEB SERVER CONFIGURATION");
        choice(name: 'DEPLOY_TO_DEV', choices: ['Yes','No'], description: 'Deploy DEV Environment?')
        choice(name: 'DEPLOY_TO_TEST', choices: ['Yes','No'], description: 'Deploy TEST Environment?')
        choice(name: 'DEPLOY_TO_STAGING', choices: ['Yes','No'], description: 'Deploy STAGING Environment?')

        string(name: 'TARGET_BASE_PATH', defaultValue: "C:\\nginx\\html\\Angular20", description: 'Folder para Todos los ambientes')

        // BACKEND
        separator(name: "BACKEND_CONFIGURATION", sectionHeader: "BACKEND CONFIGURATION");
        string(name: 'JAVA_OPTS', defaultValue: '-Xms256m -Xmx512m', description: 'Opciones de JVM')

       
    }

    stages {



        stage('Deploy environments') {
            parallel {
                // ====================== DEV ======================
                stage('Deploy DEV environment') {
                    when { expression { return params.DEPLOY_TO_DEV == 'Yes' } }
                    steps {
                        dir('workspace-dev') {
                            git(branch: "${GITHUB_BRANCHDEV}", url: "${GITHUB_URL}")
							 // --------- BACKEND ----------
                            dir('spring-boot-server') {
							powershell '''
							Get-CimInstance Win32_Process -Filter "CommandLine LIKE '%backdev04%'" | ForEach-Object {
							Stop-Process -Id $_.ProcessId -Force
							}
							'''

                                bat 'mvn clean'
								bat 'mvn package'
								
								dir('target'){
								 bat "java -jar -Dspring.application.name=backdev04 -Djava.process.name=backdev04 spring-boot-jpa-postgresql-0.0.1-SNAPSHOT.jar --server.port=8085 &"
								}
								
                                
                            }
                            
                        }
                    }
                }

                // ====================== TEST ======================
                stage('Deploy TEST environment') {
                    when { expression { return params.DEPLOY_TO_TEST == 'Yes' } }
                    steps {
                        dir('workspace-test') {
                            git(branch: "${GITHUB_BRANCHTEST}", url: "${GITHUB_URL}")

                            // --------- BACKEND ----------
                            dir('spring-boot-server') {
							powershell '''
							Get-CimInstance Win32_Process -Filter "CommandLine LIKE '%backtest04%'" | ForEach-Object {
							Stop-Process -Id $_.ProcessId -Force
							}
							'''

                                bat 'mvn clean'
								bat 'mvn package'
								
								dir('target'){
								 bat "java -jar -Dspring.application.name=backtest04 -Djava.process.name=backtest04 spring-boot-jpa-postgresql-0.0.1-SNAPSHOT.jar --server.port=8086 &"
								}
								
                                
                            }

                            
                        }
                    }
                }

                // ====================== STAGING ======================
                stage('Deploy STAGING environment') {
                    when { expression { return params.DEPLOY_TO_STAGING == 'Yes' } }
                    steps {
                        dir('workspace-staging') {
                            git(branch: "${GITHUB_BRANCHSTAGING}", url: "${GITHUB_URL}")

                            // --------- BACKEND ----------
                            dir('spring-boot-server') {
							powershell '''
							Get-CimInstance Win32_Process -Filter "CommandLine LIKE '%backstaging04%'" | ForEach-Object {
							Stop-Process -Id $_.ProcessId -Force
							}
							'''

                                bat 'mvn clean'
								bat 'mvn package'
								
								dir('target'){
								 bat "java -jar -Dspring.application.name=backstaging04 -Djava.process.name=backstaging04 spring-boot-jpa-postgresql-0.0.1-SNAPSHOT.jar --server.port=8087 &"
								}
								
                                
                            }
                            
                        }
                    }
                }
            }
        }

        
    }

    post {
        always {
            echo 'Proceso de despliegue completado.'
        }
        failure {
            echo 'El despliegue falló, revisa los logs para más detalles.'
        }
    }
}

