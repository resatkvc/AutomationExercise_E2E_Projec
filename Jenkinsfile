pipeline {
    agent any

    tools {
        maven 'Maven 3.9.6'
        jdk 'jdk-17'
    }

    environment {
        // PostgreSQL Docker container ayarları
        POSTGRES_CONTAINER_NAME = 'jenkins-postgres-db'
        POSTGRES_USER = 'testuser'
        POSTGRES_PASSWORD = 'testpass'
        POSTGRES_DB = 'testdb'
        POSTGRES_PORT = '5432'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '🔄 Kod deposundan proje çekiliyor...'
                checkout scm
            }
        }
        
        stage('Setup PostgreSQL') {
            steps {
                script {
                    echo '🐘 PostgreSQL Docker container başlatılıyor...'
                    
                    // Eğer container zaten varsa durdur ve sil
                    sh '''
                        docker stop ${POSTGRES_CONTAINER_NAME} || true
                        docker rm ${POSTGRES_CONTAINER_NAME} || true
                    '''
                    
                    // Yeni PostgreSQL container başlat
                    sh '''
                        docker run -d \
                            --name ${POSTGRES_CONTAINER_NAME} \
                            -e POSTGRES_USER=${POSTGRES_USER} \
                            -e POSTGRES_PASSWORD=${POSTGRES_PASSWORD} \
                            -e POSTGRES_DB=${POSTGRES_DB} \
                            -p ${POSTGRES_PORT}:5432 \
                            postgres:15
                    '''
                    
                    // Container'ın başlamasını bekle
                    sh '''
                        echo "PostgreSQL container başlatılıyor..."
                        sleep 10
                        docker ps | grep ${POSTGRES_CONTAINER_NAME}
                    '''
                    
                    echo '✅ PostgreSQL container başarıyla başlatıldı!'
                }
            }
        }
        
        stage('Build') {
            steps {
                echo '🔨 Proje derleniyor...'
                sh 'mvn clean compile'
            }
        }
        
        stage('Test') {
            steps {
                echo '🧪 Testler çalıştırılıyor...'
                // WebDriverManager otomatik olarak driver'ı indirecek
                sh 'mvn test'
            }
        }
        
        stage('Archive Reports') {
            steps {
                echo '📊 Raporlar arşivleniyor...'
                
                // ExtentReports HTML dosyasını arşivle
                archiveArtifacts artifacts: 'ExtentReport.html', fingerprint: true
                
                // Screenshot klasörünü arşivle (varsa)
                archiveArtifacts artifacts: 'Screenshot/**/*', fingerprint: true, allowEmptyArchive: true
                
                // TestNG raporlarını arşivle
                publishTestNG results: '**/testng-results.xml', failureOnFailedTestConfig: false
            }
        }
        
        stage('Cleanup') {
            steps {
                script {
                    echo '🧹 Temizlik yapılıyor...'
                    
                    // PostgreSQL container'ı durdur ve sil
                    sh '''
                        docker stop ${POSTGRES_CONTAINER_NAME} || true
                        docker rm ${POSTGRES_CONTAINER_NAME} || true
                    '''
                    
                    echo '✅ Temizlik tamamlandı!'
                }
            }
        }
    }
    
    post {
        always {
            echo '📋 Pipeline tamamlandı!'
            
            // Test sonuçlarını göster
            script {
                if (currentBuild.result == 'SUCCESS') {
                    echo '🎉 Tüm testler başarıyla geçti!'
                } else if (currentBuild.result == 'UNSTABLE') {
                    echo '⚠️ Bazı testler başarısız oldu!'
                } else {
                    echo '❌ Pipeline başarısız oldu!'
                }
            }
        }
        
        success {
            echo '✅ Pipeline başarıyla tamamlandı!'
        }
        
        failure {
            echo '❌ Pipeline başarısız oldu!'
            
            // Hata durumunda PostgreSQL container'ı temizle
            script {
                sh '''
                    docker stop ${POSTGRES_CONTAINER_NAME} || true
                    docker rm ${POSTGRES_CONTAINER_NAME} || true
                '''
            }
        }
        
        cleanup {
            echo '🧹 Son temizlik işlemleri...'
        }
    }
} 