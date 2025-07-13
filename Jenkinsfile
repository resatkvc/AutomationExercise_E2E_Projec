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
        
        // ANSI renk kodları
        ANSI_RED = '\033[0;31m'
        ANSI_GREEN = '\033[0;32m'
        ANSI_YELLOW = '\033[1;33m'
        ANSI_BLUE = '\033[0;34m'
        ANSI_PURPLE = '\033[0;35m'
        ANSI_CYAN = '\033[0;36m'
        ANSI_WHITE = '\033[1;37m'
        ANSI_RESET = '\033[0m'
    }
    
    stages {
        stage('📥 Checkout') {
            steps {
                script {
                    echo "${ANSI_CYAN}╔══════════════════════════════════════════════════════════════╗${ANSI_RESET}"
                    echo "${ANSI_CYAN}║                    ADIM 1: KOD ÇEKME                        ║${ANSI_RESET}"
                    echo "${ANSI_CYAN}╚══════════════════════════════════════════════════════════════╝${ANSI_RESET}"
                    echo "${ANSI_WHITE}🔄 Git repository'den kod çekiliyor...${ANSI_RESET}"
                    echo "${ANSI_YELLOW}📁 Repository: https://github.com/resatkvc/AutomationExercise_E2E_Projec.git${ANSI_RESET}"
                    
                    checkout scm
                    
                    echo "${ANSI_GREEN}✅ Kod başarıyla çekildi!${ANSI_RESET}"
                    echo "${ANSI_WHITE}📂 Çalışma dizini: ${WORKSPACE}${ANSI_RESET}"
                    sh 'ls -la'
                }
            }
        }
        
        stage('🐘 Setup PostgreSQL') {
            steps {
                script {
                    echo "${ANSI_CYAN}╔══════════════════════════════════════════════════════════════╗${ANSI_RESET}"
                    echo "${ANSI_CYAN}║                ADIM 2: POSTGRESQL KURULUMU                   ║${ANSI_RESET}"
                    echo "${ANSI_CYAN}╚══════════════════════════════════════════════════════════════╝${ANSI_RESET}"
                    
                    echo "${ANSI_WHITE}🔍 Mevcut Docker container'ları kontrol ediliyor...${ANSI_RESET}"
                    
                    // Eğer container zaten varsa durdur ve sil
                    sh '''
                        echo "🧹 Eski container'lar temizleniyor..."
                        docker stop ${POSTGRES_CONTAINER_NAME} 2>/dev/null || echo "Container zaten durmuş"
                        docker rm ${POSTGRES_CONTAINER_NAME} 2>/dev/null || echo "Container zaten silinmiş"
                        echo "✅ Temizlik tamamlandı"
                    '''
                    
                    echo "${ANSI_WHITE}🔍 Port kullanımı kontrol ediliyor...${ANSI_RESET}"
                    
                    // Port kullanımını kontrol et ve uygun port bul
                    def port = POSTGRES_PORT.toInteger()
                    def maxAttempts = 10
                    def attempt = 0
                    def containerStarted = false
                    
                    while (!containerStarted && attempt < maxAttempts) {
                        attempt++
                        def currentPort = port + attempt - 1
                        
                        echo "${ANSI_YELLOW}🔍 Port ${currentPort} deneniyor... (Deneme ${attempt}/${maxAttempts})${ANSI_RESET}"
                        
                        def result = sh(
                            script: """
                                # Port kullanımını kontrol et
                                if lsof -i :${currentPort} > /dev/null 2>&1; then
                                    echo "❌ Port ${currentPort} kullanımda, sonraki port deneniyor..."
                                    exit 1
                                fi
                                
                                echo "✅ Port ${currentPort} müsait"
                                echo "🐳 PostgreSQL container başlatılıyor..."
                                
                                # Container'ı başlat
                                docker run -d \
                                    --name ${POSTGRES_CONTAINER_NAME} \
                                    -e POSTGRES_USER=${POSTGRES_USER} \
                                    -e POSTGRES_PASSWORD=${POSTGRES_PASSWORD} \
                                    -e POSTGRES_DB=${POSTGRES_DB} \
                                    -p ${currentPort}:5432 \
                                    postgres:15
                                
                                echo "⏳ Container başlatılıyor, bekleniyor..."
                                # Container'ın başladığını kontrol et
                                sleep 10
                                if docker ps | grep ${POSTGRES_CONTAINER_NAME}; then
                                    echo "✅ Container başarıyla başlatıldı (Port: ${currentPort})"
                                    exit 0
                                else
                                    echo "❌ Container başlatılamadı"
                                    docker stop ${POSTGRES_CONTAINER_NAME} 2>/dev/null || true
                                    docker rm ${POSTGRES_CONTAINER_NAME} 2>/dev/null || true
                                    exit 1
                                fi
                            """,
                            returnStatus: true
                        )
                        
                        if (result == 0) {
                            containerStarted = true
                            env.POSTGRES_PORT = currentPort.toString()
                            echo "${ANSI_GREEN}🎉 PostgreSQL container başarıyla başlatıldı!${ANSI_RESET}"
                            echo "${ANSI_WHITE}📊 Container Bilgileri:${ANSI_RESET}"
                            echo "${ANSI_WHITE}   • Port: ${currentPort}${ANSI_RESET}"
                            echo "${ANSI_WHITE}   • Database: ${POSTGRES_DB}${ANSI_RESET}"
                            echo "${ANSI_WHITE}   • User: ${POSTGRES_USER}${ANSI_RESET}"
                        } else {
                            echo "${ANSI_RED}❌ Port ${currentPort} başarısız, sonraki port deneniyor...${ANSI_RESET}"
                        }
                    }
                    
                    if (!containerStarted) {
                        error "${ANSI_RED}💥 PostgreSQL container başlatılamadı! Tüm portlar denendi.${ANSI_RESET}"
                    }
                    
                    echo "${ANSI_WHITE}⏳ PostgreSQL servisinin tamamen hazır olması bekleniyor...${ANSI_RESET}"
                    
                    // Container'ın tamamen hazır olmasını bekle
                    sh '''
                        echo "🔄 PostgreSQL servisi başlatılıyor..."
                        sleep 30
                        echo "✅ PostgreSQL servisi hazır!"
                        docker ps | grep ${POSTGRES_CONTAINER_NAME}
                        echo "📊 Container durumu:"
                        docker logs --tail 5 ${POSTGRES_CONTAINER_NAME}
                    '''
                }
            }
        }
        
        stage('🔨 Build') {
            steps {
                script {
                    echo "${ANSI_CYAN}╔══════════════════════════════════════════════════════════════╗${ANSI_RESET}"
                    echo "${ANSI_CYAN}║                    ADIM 3: PROJE DERLEME                    ║${ANSI_RESET}"
                    echo "${ANSI_CYAN}╚══════════════════════════════════════════════════════════════╝${ANSI_RESET}"
                    
                    echo "${ANSI_WHITE}🔨 Maven ile proje derleniyor...${ANSI_RESET}"
                    echo "${ANSI_YELLOW}📋 Derleme komutları:${ANSI_RESET}"
                    echo "${ANSI_WHITE}   • mvn clean compile${ANSI_RESET}"
                    
                    sh '''
                        echo "🧹 Eski build dosyaları temizleniyor..."
                        mvn clean
                        echo "🔨 Proje derleniyor..."
                        mvn compile
                        echo "✅ Derleme tamamlandı!"
                    '''
                    
                    echo "${ANSI_GREEN}✅ Proje başarıyla derlendi!${ANSI_RESET}"
                }
            }
        }
        
        stage('🧪 Test') {
            steps {
                script {
                    echo "${ANSI_CYAN}╔══════════════════════════════════════════════════════════════╗${ANSI_RESET}"
                    echo "${ANSI_CYAN}║                    ADIM 4: TEST ÇALIŞTIRMA                  ║${ANSI_RESET}"
                    echo "${ANSI_CYAN}╚══════════════════════════════════════════════════════════════╝${ANSI_RESET}"
                    
                    echo "${ANSI_WHITE}🧪 Selenium testleri çalıştırılıyor...${ANSI_RESET}"
                    echo "${ANSI_YELLOW}📊 Test Konfigürasyonu:${ANSI_RESET}"
                    echo "${ANSI_WHITE}   • PostgreSQL Port: ${POSTGRES_PORT}${ANSI_RESET}"
                    echo "${ANSI_WHITE}   • Test Framework: TestNG${ANSI_RESET}"
                    echo "${ANSI_WHITE}   • WebDriver: WebDriverManager (Otomatik)${ANSI_RESET}"
                    
                    sh '''
                        echo "🚀 Testler başlatılıyor..."
                        echo "📋 Test komutu: mvn test -DPOSTGRES_PORT=${POSTGRES_PORT}"
                        mvn test -DPOSTGRES_PORT=${POSTGRES_PORT}
                        echo "✅ Testler tamamlandı!"
                    '''
                    
                    echo "${ANSI_GREEN}✅ Tüm testler başarıyla tamamlandı!${ANSI_RESET}"
                }
            }
        }
        
        stage('📊 Archive Reports') {
            steps {
                script {
                    echo "${ANSI_CYAN}╔══════════════════════════════════════════════════════════════╗${ANSI_RESET}"
                    echo "${ANSI_CYAN}║                  ADIM 5: RAPOR ARŞİVLEME                    ║${ANSI_RESET}"
                    echo "${ANSI_CYAN}╚══════════════════════════════════════════════════════════════╝${ANSI_RESET}"
                    
                    echo "${ANSI_WHITE}📊 Test raporları arşivleniyor...${ANSI_RESET}"
                    
                    // Dosyaların varlığını kontrol et
                    sh '''
                        echo "🔍 Rapor dosyaları kontrol ediliyor..."
                        if [ -f "ExtentReport.html" ]; then
                            echo "✅ ExtentReport.html bulundu"
                            ls -la ExtentReport.html
                        else
                            echo "⚠️ ExtentReport.html bulunamadı"
                        fi
                        
                        if [ -d "Screenshot" ]; then
                            echo "✅ Screenshot klasörü bulundu"
                            ls -la Screenshot/
                        else
                            echo "⚠️ Screenshot klasörü bulunamadı"
                        fi
                    '''
                    
                    // ExtentReports HTML dosyasını arşivle
                    archiveArtifacts artifacts: 'ExtentReport.html', fingerprint: true
                    
                    // Screenshot klasörünü arşivle (varsa)
                    archiveArtifacts artifacts: 'Screenshot/**/*', fingerprint: true, allowEmptyArchive: true
                    
                    echo "${ANSI_GREEN}✅ Raporlar başarıyla arşivlendi!${ANSI_RESET}"
                }
            }
        }
        
        stage('🧹 Cleanup') {
            steps {
                script {
                    echo "${ANSI_CYAN}╔══════════════════════════════════════════════════════════════╗${ANSI_RESET}"
                    echo "${ANSI_CYAN}║                   ADIM 6: TEMİZLİK İŞLEMLERİ                ║${ANSI_RESET}"
                    echo "${ANSI_CYAN}╚══════════════════════════════════════════════════════════════╝${ANSI_RESET}"
                    
                    echo "${ANSI_WHITE}🧹 Docker container'ları temizleniyor...${ANSI_RESET}"
                    
                    // PostgreSQL container'ı durdur ve sil
                    sh '''
                        echo "🛑 PostgreSQL container durduruluyor..."
                        docker stop ${POSTGRES_CONTAINER_NAME} 2>/dev/null || echo "Container zaten durmuş"
                        echo "🗑️ PostgreSQL container siliniyor..."
                        docker rm ${POSTGRES_CONTAINER_NAME} 2>/dev/null || echo "Container zaten silinmiş"
                        echo "✅ Temizlik tamamlandı!"
                    '''
                    
                    echo "${ANSI_GREEN}✅ Tüm temizlik işlemleri tamamlandı!${ANSI_RESET}"
                }
            }
        }
    }
    
    post {
        always {
            script {
                echo "${ANSI_CYAN}╔══════════════════════════════════════════════════════════════╗${ANSI_RESET}"
                echo "${ANSI_CYAN}║                    PIPELINE ÖZETİ                            ║${ANSI_RESET}"
                echo "${ANSI_CYAN}╚══════════════════════════════════════════════════════════════╝${ANSI_RESET}"
                
                echo "${ANSI_WHITE}📋 Pipeline tamamlandı!${ANSI_RESET}"
                echo "${ANSI_WHITE}🏗️ Build #${BUILD_NUMBER}${ANSI_RESET}"
                echo "${ANSI_WHITE}⏱️ Süre: ${currentBuild.durationString}${ANSI_RESET}"
                
                // Test sonuçlarını göster
                if (currentBuild.result == 'SUCCESS') {
                    echo "${ANSI_GREEN}🎉 Tüm testler başarıyla geçti!${ANSI_RESET}"
                } else if (currentBuild.result == 'UNSTABLE') {
                    echo "${ANSI_YELLOW}⚠️ Bazı testler başarısız oldu!${ANSI_RESET}"
                } else {
                    echo "${ANSI_RED}❌ Pipeline başarısız oldu!${ANSI_RESET}"
                }
            }
        }
        
        success {
            echo "${ANSI_GREEN}🎊 Pipeline başarıyla tamamlandı! 🎊${ANSI_RESET}"
        }
        
        failure {
            echo "${ANSI_RED}💥 Pipeline başarısız oldu! 💥${ANSI_RESET}"
            
            // Hata durumunda PostgreSQL container'ı temizle
            script {
                echo "${ANSI_YELLOW}🧹 Hata durumunda temizlik yapılıyor...${ANSI_RESET}"
                sh '''
                    docker stop ${POSTGRES_CONTAINER_NAME} 2>/dev/null || echo "Container zaten durmuş"
                    docker rm ${POSTGRES_CONTAINER_NAME} 2>/dev/null || echo "Container zaten silinmiş"
                    echo "✅ Temizlik tamamlandı"
                '''
            }
        }
        
        cleanup {
            echo "${ANSI_WHITE}🧹 Son temizlik işlemleri...${ANSI_RESET}"
        }
    }
} 