# AutomationExercise E2E Test Automation Project

📖 Blog Yazısı  
👉 Docker ile PostgreSQL kurulumunu ve IDE + PgAdmin entegrasyonunu adım adım anlattığım Blog yazım için aşağıdaki bağlantıdan inceleyebilirsiniz:  
🔗 [**Docker Üzerinde PostgreSQL Kurulumu: IDE ile Entegrasyon ve PgAdmin ile Görsel Yönetim**](https://medium.com/@kavciresat/docker-%C3%BCzerinde-postgresql-kurulumu-ide-ile-entegrasyon-ve-pgadmin-ile-g%C3%B6rsel-y%C3%B6netim-2ba3ef059356)

---

## Proje Hakkında
Bu proje, [https://automationexercise.com](https://automationexercise.com) sitesi üzerinde uçtan uca (E2E) test otomasyonu yapmak için geliştirilmiştir. Java, Selenium WebDriver, TestNG, ExtentReports, PostgreSQL, Docker ve Jenkins teknolojileriyle, Page Object Model (POM) mimarisi kullanılarak hazırlanmıştır.

---

## İçerik
- [Kullanılan Teknolojiler](#kullanılan-teknolojiler)
- [Kurulum ve Çalıştırma (Adım Adım)](#kurulum-ve-çalıştırma-adım-adım)
- [Proje Mimarisi ve Klasör Yapısı](#proje-mimarisi-ve-klasör-yapısı)
- [Test Akışı](#test-akışı)
- [Veritabanı Entegrasyonu](#veritabanı-entegrasyonu)
- [Raporlama (ExtentReports)](#raporlama-extentreports)
- [CI/CD ve Jenkins](#cicd-ve-jenkins)
- [Sık Karşılaşılan Sorunlar ve Çözümler](#sık-karşılaşılan-sorunlar-ve-çözümler)
- [Katkı](#katkı)
- [İletişim](#iletişim)

---

## Kullanılan Teknolojiler
- **Java 21**
- **Selenium WebDriver 4.x**
- **TestNG**
- **ExtentReports 4.1.7**
- **WebDriverManager**
- **PostgreSQL** (Docker ile)
- **JDBC**
- **Docker & Docker Compose**
- **Jenkins**
- **Page Object Model (POM)**

---

## Kurulum ve Çalıştırma (Adım Adım)

### 1. **Projeyi Klonla**
```bash
git clone <repo-url>
cd AutomationExercise_E2E_Projec
```

### 2. **Docker ile PostgreSQL’i Başlat**
> **Neden?** Testler ve uygulama, veritabanına bağlanmak zorunda. Önce DB ayağa kalkmalı.

```bash
docker-compose up -d
```
veya
```bash
docker run --name jenkins-postgres-db -e POSTGRES_USER=testuser -e POSTGRES_PASSWORD=testpass -e POSTGRES_DB=testdb -p 5432:5432 -d postgres:15
```

**Bağlantı Bilgileri:**
- Host: `localhost`
- Port: `5432`
- User: `testuser`
- Password: `testpass`
- Database: `testdb`

### 3. **Java, Maven ve Docker Kurulu mu Kontrol Et**
```bash
java -version
mvn -version
docker --version
```
> Eksikse: [Java](https://adoptium.net/), [Maven](https://maven.apache.org/download.cgi), [Docker](https://www.docker.com/products/docker-desktop/)

### 4. **Bağımlılıkları Yükle**
```bash
mvn clean install
```

### 5. **Testleri Çalıştır**
```bash
mvn test
```
- Testler başlar, random kullanıcı oluşturulur, formlar doldurulur, DB’ye kayıt yapılır, ürün eklenir, ödeme yapılır, doğrulama yapılır.
- Test başarısız olursa otomatik screenshot alınır (`Screenshot/` klasörüne kaydedilir).
- Test raporu (`ExtentReport.html`) oluşur.

### 6. **Test Raporunu ve Sonuçları İncele**
- `ExtentReport.html` dosyasını tarayıcıda aç.
- Başarısız testlerde `Screenshot/` klasörüne bak.

### 7. **(Opsiyonel) Jenkins ile CI/CD Pipeline Kur**
- Jenkins kuruluysa, projenin kök dizinindeki `Jenkinsfile` ile pipeline oluştur.
- Jenkins arayüzünde yeni bir pipeline oluşturup repoyu bağla ve “Build Now” ile çalıştır.

---

## Proje Mimarisi ve Klasör Yapısı
```
AutomationExercise_E2E_Projec/
├── pom.xml
├── src/
│   ├── main/
│   │   └── java/
│   │       └── proje/com/
│   │           ├── base/           # BasePage, BaseTest
│   │           ├── model/          # User modeli
│   │           ├── pages/          # HomePage, SignupPage, LoginPage, ProductPage, CartPage, PaymentPage
│   │           └── util/           # DatabaseUtil, RandomUserGenerator, Messages
│   └── test/
│       └── java/
│           └── proje/com/tests/    # UserSignupAndOrderTest
├── Screenshot/                     # Otomatik alınan ekran görüntüleri
├── ExtentReport.html               # Test raporu
├── docker-compose.yml              # Docker servisleri için
├── Jenkinsfile                     # CI/CD pipeline tanımı
└── README.md
```

---

## Test Akışı
1. **Siteye git:** Ana sayfa açılır.
2. **Signup/Login:** Signup/Login butonuna tıklanır.
3. **Random kullanıcı oluştur:** Rastgele kullanıcı ve kart bilgisi üretilir.
4. **Signup formu doldur:** Random bilgilerle form doldurulur.
5. **Hesap oluştur:** Hesap oluşturulur, veritabanına kaydedilir.
6. **Account Created doğrulama:** Başarı mesajı kontrol edilir.
7. **Ürün ekle:** İlk ürün sepete eklenir.
8. **Sepet kontrolü:** Sepette ürün varlığı doğrulanır.
9. **Ödeme:** Random kart bilgisiyle ödeme formu doldurulur.
10. **Order placed doğrulama:** Sipariş onay mesajı kontrol edilir.
11. **Logout:** Kullanıcı çıkış yapar.
12. **Raporlama:** Tüm adımlar ExtentReport ile detaylı loglanır.

---

## Veritabanı Entegrasyonu ve Sorgulama: Adım Adım

### 1. Docker ile PostgreSQL Container’ını Başlat

**Terminalde:**
```bash
docker-compose up -d
```
veya
```bash
docker run --name jenkins-postgres-db -e POSTGRES_USER=testuser -e POSTGRES_PASSWORD=testpass -e POSTGRES_DB=testdb -p 5432:5432 -d postgres:15
```
- Bu komut, PostgreSQL veritabanını arka planda başlatır.

---

### 2. PostgreSQL Container’ına Bağlan

#### A) Docker Terminali Üzerinden Bağlanmak
```bash
docker exec -it jenkins-postgres-db psql -U testuser -d testdb
```
- Bu komut, doğrudan PostgreSQL’in komut satırına (psql) bağlar.

**Başarılı bağlantı çıktısı:**
```
psql (15.x)
Type "help" for help.

testdb=#
```

#### B) Host Makineden Bağlanmak (psql yüklü ise)
```bash
psql -h localhost -p 5432 -U testuser -d testdb
```
- Şifre sorarsa: `testpass`

#### C) IDE Üzerinden Bağlanmak (DBeaver, DataGrip, pgAdmin, vs.)
1. **Yeni bağlantı ekle** (PostgreSQL seç).
2. **Host:** `localhost`
3. **Port:** `5432`
4. **Database:** `testdb`
5. **User:** `testuser`
6. **Password:** `testpass`
7. **Bağlan** butonuna tıkla.

---

### 3. SQL Sorguları ile Veri Çekmek

Bağlantı kurulduktan sonra, aşağıdaki sorguları hem Docker terminalinde hem de IDE’de kullanabilirsin:

```sql
-- Tüm kullanıcıları listele
SELECT * FROM users;

-- Tüm kartları listele
SELECT * FROM cards;

-- Son 5 kullanıcıyı göster
SELECT * FROM users ORDER BY id DESC LIMIT 5;

-- Belirli bir email'e sahip kullanıcıyı bul
SELECT * FROM users WHERE email = 'test@example.com';

-- Kullanıcı sayısını göster
SELECT COUNT(*) FROM users;

-- Kart sayısını göster
SELECT COUNT(*) FROM cards;
```

---

### 4. Çıkış ve Temizlik

**psql’den çıkmak için:**
```sql
\q
```

**Docker container’dan çıkmak için:**
```bash
exit
```

**Container’ı durdurmak için:**
```bash
docker stop jenkins-postgres-db
```

**Container’ı silmek için:**
```bash
docker rm jenkins-postgres-db
```

---

### Kısa Özet Akış

1. **Docker ile PostgreSQL başlat:**  
   `docker-compose up -d`
2. **Container’a bağlan:**  
   `docker exec -it jenkins-postgres-db psql -U testuser -d testdb`
3. **SQL sorgularını çalıştır:**  
   `SELECT * FROM users;` vb.
4. **Çıkış ve temizlik:**  
   `\q`, `exit`, `docker stop ...`, `docker rm ...`

---

**Not:**
- Tüm bu adımlar hem Docker terminalinde hem de IDE’de aynıdır, sadece bağlantı yöntemi değişir.
- Sorgular ve veri erişimi her iki ortamda da aynıdır.

---

## Raporlama (ExtentReports)
- Her test adımı detaylı ve renkli şekilde loglanır.
- Başarısız adımlarda otomatik ekran görüntüsü alınır ve rapora eklenir.
- Rapor dosyası: `ExtentReport.html` (proje kökünde)
- Raporu tarayıcıda açarak adım adım testin nasıl ilerlediğini görebilirsin.
- **Not:** Jenkins arayüzünde rapor sade görünebilir, tam renkli görünüm için dosyayı indirip tarayıcıda aç.

---

## CI/CD ve Jenkins
### **Jenkins ile Pipeline Kurulumu**
1. **Jenkins’i kur ve başlat.**
2. **Yeni bir pipeline oluştur, repoyu bağla.**
3. **Jenkinsfile’daki adımlar:**
   - Kodun çekilmesi
   - Docker ile PostgreSQL’in başlatılması
   - Maven ile derleme ve test
   - Raporların arşivlenmesi
   - Temizlik (container’ların silinmesi)
4. **Pipeline’ı başlat ve raporları Jenkins arayüzünden görüntüle.**

---

## Katkı
- Katkıda bulunmak için fork’layıp PR gönderebilirsiniz.

---

## İletişim
Her türlü soru, öneri ve katkı için iletişime geçebilirsin.
