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
        POSTGRES_PORT = '5433'  // 5432 yerine 5433 kullanıyoruz
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
                    
                    // Port kullanımını kontrol et ve uygun port bul
                    def port = POSTGRES_PORT.toInteger()
                    def maxAttempts = 10
                    def attempt = 0
                    def containerStarted = false
                    
                    while (!containerStarted && attempt < maxAttempts) {
                        attempt++
                        def currentPort = port + attempt - 1
                        
                        echo "Port ${currentPort} deneniyor... (Deneme ${attempt}/${maxAttempts})"
                        
                        def result = sh(
                            script: """
                                # Port kullanımını kontrol et
                                if lsof -i :${currentPort} > /dev/null 2>&1; then
                                    echo "Port ${currentPort} kullanımda, sonraki port deneniyor..."
                                    exit 1
                                fi
                                
                                # Container'ı başlat
                                docker run -d \
                                    --name ${POSTGRES_CONTAINER_NAME} \
                                    -e POSTGRES_USER=${POSTGRES_USER} \
                                    -e POSTGRES_PASSWORD=${POSTGRES_PASSWORD} \
                                    -e POSTGRES_DB=${POSTGRES_DB} \
                                    -p ${currentPort}:5432 \
                                    postgres:15
                                
                                # Container'ın başladığını kontrol et
                                sleep 10
                                if docker ps | grep ${POSTGRES_CONTAINER_NAME}; then
                                    echo "Container başarıyla başlatıldı (Port: ${currentPort})"
                                    exit 0
                                else
                                    echo "Container başlatılamadı"
                                    docker stop ${POSTGRES_CONTAINER_NAME} || true
                                    docker rm ${POSTGRES_CONTAINER_NAME} || true
                                    exit 1
                                fi
                            """,
                            returnStatus: true
                        )
                        
                        if (result == 0) {
                            containerStarted = true
                            env.POSTGRES_PORT = currentPort.toString()
                            echo "✅ PostgreSQL container başarıyla başlatıldı! Port: ${currentPort}"
                        } else {
                            echo "❌ Port ${currentPort} başarısız, sonraki port deneniyor..."
                        }
                    }
                    
                    if (!containerStarted) {
                        error "PostgreSQL container başlatılamadı! Tüm portlar denendi."
                    }
                    
                    // Container'ın tamamen hazır olmasını bekle
                    sh '''
                        echo "PostgreSQL container'ının tamamen hazır olması bekleniyor..."
                        sleep 30
                        docker ps | grep ${POSTGRES_CONTAINER_NAME}
                    '''
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
                sh '''
                    echo "PostgreSQL port: ${POSTGRES_PORT}"
                    mvn test -DPOSTGRES_PORT=${POSTGRES_PORT}
                '''
            }
        }
        
        stage('Archive Reports') {
            steps {
                echo '📊 Raporlar arşivleniyor...'
                
                // ExtentReports HTML dosyasını arşivle
                archiveArtifacts artifacts: 'ExtentReport.html', fingerprint: true
                
                // Screenshot klasörünü arşivle (varsa)
                archiveArtifacts artifacts: 'Screenshot/**/*', fingerprint: true, allowEmptyArchive: true
                
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