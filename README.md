# AutomationExercise E2E Test Automation Project

## Proje Hakkında
Bu proje, [https://automationexercise.com](https://automationexercise.com) sitesi üzerinde uçtan uca (E2E) test otomasyonu yapmak için geliştirilmiştir. Proje, profesyonel bir test otomasyon framework’ü olarak Java, Selenium WebDriver, TestNG, ExtentReports, PostgreSQL, Docker ve Jenkins teknolojilerini bir arada kullanır. Kod yapısı Page Object Model (POM) mimarisiyle düzenlenmiştir.

---

## İçerik
- [Kullanılan Teknolojiler](#kullanılan-teknolojiler)
- [Kod Mimarisi ve Açıklamaları](#kod-mimarisi-ve-açıklamaları)
- [Kurulum ve Çalıştırma](#kurulum-ve-çalıştırma)
- [PostgreSQL: Docker ile Kurulum, Erişim ve Sorgu](#postgresql-docker-ile-kurulum-erisim-ve-sorgu)
- [Proje Mimarisi ve Klasör Yapısı](#proje-mimarisi-ve-klasör-yapısı)
- [Test Akışı](#test-akışı)
- [Veritabanı Entegrasyonu](#veritabanı-entegrasyonu)
- [Random Veri Üretimi](#random-veri-üretimi)
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
- **ExtentReports 4.1.7** (HTML raporlama)
- **WebDriverManager** (otomatik driver yönetimi)
- **PostgreSQL** (Docker ile)
- **JDBC** (veritabanı bağlantısı)
- **Docker & Docker Compose**
- **Jenkins** (CI/CD pipeline)
- **Page Object Model (POM)**

---

## Kod Mimarisi ve Açıklamaları

### **Page Object Model (POM)**
- Her sayfa için ayrı bir Java sınıfı (`HomePage`, `SignupPage`, `LoginPage`, `ProductPage`, `CartPage`, `PaymentPage`) oluşturulmuştur.
- Her sayfa class’ında, o sayfadaki elementler ve işlemler metotlar halinde tanımlanır.
- Kodun bakımı ve genişletilmesi kolaylaşır.

### **BasePage & BaseTest**
- `BasePage`: Tüm page class’larının ortak fonksiyonlarını ve WebDriver referansını içerir.
- `BaseTest`: Testlerin setup/teardown işlemlerini ve driver yönetimini yapar.

### **User Modeli**
- `User.java`: Testlerde kullanılacak kullanıcı ve kart bilgilerini tek bir modelde toplar.

### **RandomUserGenerator**
- Her testte farklı kullanıcı ve kart bilgisi üretir.
- Testlerin tekrar tekrar çalıştırılabilmesini ve veri çakışmalarını önler.

### **DatabaseUtil**
- PostgreSQL’e JDBC ile bağlanıp kullanıcı ve kart bilgilerini kaydeder.
- Bağlantı bilgileri ve SQL işlemleri burada merkezi olarak yönetilir.

### **Messages**
- Tüm doğrulama (verify) mesajları merkezi olarak burada tutulur.
- Kodun farklı yerlerinde sabit string kullanmak yerine tek noktadan yönetim sağlar.

### **UserSignupAndOrderTest**
- Tüm E2E akışı baştan sona test eden ana test class’ı.
- Her adımda detaylı loglama ve raporlama yapılır.

### **Kodda Açıklama ve Yorumlar**
- Her önemli class, method ve blok başında **neden kullanıldığı** ve **ne yaptığı** açıklanmıştır.
- Kodun okunabilirliği ve sürdürülebilirliği için bolca açıklama eklenmiştir.

---

## Kurulum ve Çalıştırma
### 1. **Projeyi Klonla**
```bash
git clone <repo-url>
cd AutomationExercise_E2E_Projec
```

### 2. **PostgreSQL’i Docker ile Başlat**
- `docker-compose.yml` veya Docker Desktop ile aşağıdaki ayarlarla bir container başlat:
  - **Port:** 5432:5432
  - **Kullanıcı:** testuser
  - **Şifre:** testpass
  - **Veritabanı:** testdb
- Örnek environment:
  - POSTGRES_USER=testuser
  - POSTGRES_PASSWORD=testpass
  - POSTGRES_DB=testdb

### 3. **Bağımlılıkları Yükle**
```bash
mvn clean install
```

### 4. **Testi Çalıştır**
- IDE üzerinden `UserSignupAndOrderTest.java` dosyasındaki class veya method başındaki “Run” tuşuna bas.
- Veya terminalden:
```bash
mvn test
```

### 5. **Raporu Görüntüle**
- Proje kökünde oluşan `ExtentReport.html` dosyasını tarayıcıda aç.

---

## PostgreSQL: Docker ile Kurulum, Erişim ve Sorgu

### 1. Docker ile PostgreSQL Container'ı Başlat

#### a) Docker Desktop ile (Görsel Arayüz)
1. **Docker Desktop'ı aç**
2. **"Containers" sekmesine git**
3. **"Run" butonuna tıkla**
4. **"postgres" image'ını seç**
5. **Aşağıdaki ayarları yap:**
   - **Container name:** `postgres-db`
   - **Ports:** `5432:5432`
   - **Environment variables:**
     - `POSTGRES_USER=testuser`
     - `POSTGRES_PASSWORD=testpass`
     - `POSTGRES_DB=testdb`
6. **"Run" butonuna bas**

#### b) Terminal ile (Komut Satırı)
```bash
docker run --name postgres-db -e POSTGRES_USER=testuser -e POSTGRES_PASSWORD=testpass -e POSTGRES_DB=testdb -p 5432:5432 -d postgres:15
```

**Komut Açıklaması:**
- `--name postgres-db`: Konteyner adı
- `-e POSTGRES_USER=testuser`: Kullanıcı adı
- `-e POSTGRES_PASSWORD=testpass`: Şifre
- `-e POSTGRES_DB=testdb`: Veritabanı adı
- `-p 5432:5432`: Port yönlendirmesi (host:container)
- `-d postgres:15`: Arka planda çalıştır, 15 sürümü

### 2. Konteynerin Çalıştığını Kontrol Et

```bash
docker ps
```

**Beklenen Çıktı:**
```
CONTAINER ID   IMAGE         COMMAND                  CREATED         STATUS         PORTS                    NAMES
abc123def456   postgres:15   "docker-entrypoint.s…"   2 minutes ago   Up 2 minutes   0.0.0.0:5432->5432/tcp   postgres-db
```

### 3. Terminalden PostgreSQL'e Bağlanmak

#### a) Docker İçine Girip psql ile Bağlanmak (Önerilen)
```bash
docker exec -it postgres-db psql -U testuser -d testdb
```

**Komut Açıklaması:**
- `docker exec -it`: Konteyner içinde interaktif terminal aç
- `postgres-db`: Konteyner adı
- `psql`: PostgreSQL komut satırı aracı
- `-U testuser`: Kullanıcı adı
- `-d testdb`: Veritabanı adı

**Başarılı Bağlantı Belirtisi:**
```
psql (15.5 (Debian 15.5-1.pgdg120+1))
Type "help" for help.

testdb=#
```

#### b) Host Makineden Bağlanmak (psql yüklü ise)
```bash
psql -h localhost -p 5432 -U testuser -d testdb
```
Şifre sorarsa: `testpass`

### 4. pgAdmin ile Bağlanmak (Görsel Arayüz)

1. **pgAdmin'i aç** (yüklü değilse [pgadmin.org](https://www.pgadmin.org/download/) adresinden indir)

2. **"Add New Server" de**

3. **General sekmesinde:**
   - Name: `AutomationExercise DB`

4. **Connection sekmesinde:**
   - Host name/address: `localhost`
   - Port: `5432`
   - Maintenance database: `testdb`
   - Username: `testuser`
   - Password: `testpass`

5. **"Save" butonuna bas**

6. **Bağlantı başarılıysa:**
   - Sol panelde "AutomationExercise DB" görünür
   - Databases → testdb → Schemas → public → Tables

### 5. Java/JDBC ile Bağlanmak (Kod İçinde)

`DatabaseUtil.java` dosyasında bağlantı stringi şöyle olmalı:

```java
String url = "jdbc:postgresql://localhost:5432/testdb";
String user = "testuser";
String password = "testpass";
Connection conn = DriverManager.getConnection(url, user, password);
```

### 6. Veritabanı Sorguları

#### a) Temel PostgreSQL Komutları
```sql
-- Tüm tabloları listele
\dt

-- Veritabanı listesini göster
\l

-- Kullanıcı listesini göster
\du

-- Yardım al
\help

-- Çıkış yap
\q
```

#### b) Test Verilerini Sorgulama
```sql
-- users tablosundaki tüm kayıtları göster
SELECT * FROM users;

-- cards tablosundaki tüm kayıtları göster
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

#### c) Java ile Sorgu Örneği
```java
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery("SELECT * FROM users");
while (rs.next()) {
    System.out.println("ID: " + rs.getInt("id"));
    System.out.println("Email: " + rs.getString("email"));
    System.out.println("Name: " + rs.getString("name"));
    System.out.println("---");
}
```

### 7. Sık Karşılaşılan Hatalar ve Çözümleri

#### a) Bağlantı Reddedildi Hatası
**Hata:** `connection to server at "localhost" (127.0.0.1), port 5432 failed: Connection refused`

**Çözümler:**
1. Docker konteynerinin çalıştığını kontrol et: `docker ps`
2. Port yönlendirmesini kontrol et: `docker port postgres-db`
3. Firewall ayarlarını kontrol et
4. Konteyneri yeniden başlat: `docker restart postgres-db`

#### b) Şifre Hatası
**Hata:** `password authentication failed for user "testuser"`

**Çözümler:**
1. Doğru kullanıcı adını kullandığından emin ol
2. Doğru şifreyi kullandığından emin ol
3. Konteyneri yeniden oluştur

#### c) Veritabanı Bulunamadı Hatası
**Hata:** `database "testdb" does not exist`

**Çözümler:**
1. Konteyner oluştururken `POSTGRES_DB=testdb` environment variable'ını eklediğinden emin ol
2. Veritabanını manuel oluştur: `CREATE DATABASE testdb;`

#### d) Tablo Bulunamadı Hatası
**Hata:** `relation "users" does not exist`

**Çözümler:**
1. Test çalıştırarak tabloların otomatik oluşturulmasını bekle
2. Manuel tablo oluştur:
```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100),
    password VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 8. Çıkış ve Temizlik

#### a) psql'den Çıkmak
```sql
\q
```

#### b) Docker Container'dan Çıkmak
```bash
exit
```

#### c) Container'ı Durdurmak
```bash
docker stop postgres-db
```

#### d) Container'ı Silmek
```bash
docker rm postgres-db
```

### 9. Kısa Özet Akış

1. **Docker ile PostgreSQL başlat** (Docker Desktop veya terminal)
2. **`docker ps` ile port ve konteyneri kontrol et**
3. **Terminal veya pgAdmin ile bağlan**
4. **Java/JDBC ile bağlantı stringini doğru yaz**
5. **SELECT sorgusu ile veri çek**
6. **Hata olursa yukarıdaki troubleshooting adımlarını kontrol et**

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
└── docker-compose.yml              # Docker servisleri için
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

## Veritabanı Entegrasyonu
- **Kullanıcı ve kart bilgileri** random üretilir ve PostgreSQL’e kaydedilir.
- `users` ve `cards` tabloları kullanılır.
- Kayıtlar DBeaver, pgAdmin veya terminalden sorgulanabilir:
  ```sql
  SELECT * FROM users;
  SELECT * FROM cards;
  ```

---

## Random Veri Üretimi
- `RandomUserGenerator.java` ile her testte farklı kullanıcı ve kart bilgisi üretilir.
- Bu bilgiler hem formda kullanılır hem de veritabanına kaydedilir.

---

## Raporlama (ExtentReports)
- Her test adımı detaylı ve renkli şekilde loglanır.
- Başarısız adımlarda otomatik ekran görüntüsü alınır ve rapora eklenir.
- Rapor dosyası: `ExtentReport.html` (proje kökünde)
- Raporu tarayıcıda açarak adım adım testin nasıl ilerlediğini görebilirsin.

---

## CI/CD ve Jenkins
### **Jenkins ile Pipeline Kurulumu**
1. **Jenkins’i Docker ile başlat:**
   - Docker Desktop’ta yeni bir Jenkins container’ı başlat.
   - Gerekli portları (8080:8080) ve volume’leri ayarla.
2. **Jenkins’e ilk giriş:**
   - Admin şifresini terminalden veya volume’den bul.
   - Gerekli pluginleri yükle.
3. **Pipeline oluştur:**
   - Projeyi GitHub’dan çek.
   - `docker-compose.yml` ile PostgreSQL’i başlat.
   - `mvn clean test` ile testleri çalıştır.
   - `ExtentReport.html` dosyasını build artifact olarak arşivle.
4. **Jenkinsfile örneği:**
   ```groovy
   pipeline {
     agent any
     stages {
       stage('Build') {
         steps {
           sh 'mvn clean install'
         }
       }
       stage('Test') {
         steps {
           sh 'mvn test'
         }
       }
       stage('Archive Report') {
         steps {
           archiveArtifacts artifacts: 'ExtentReport.html', fingerprint: true
         }
       }
     }
   }
   ```
5. **Pipeline’ı başlat ve raporları Jenkins arayüzünden görüntüle.**

---

## Sık Karşılaşılan Sorunlar ve Çözümler
- **Element bulunamıyor:** Doğru sayfada, doğru adımda olduğundan ve bekleme (wait) kullandığından emin ol.
- **Veritabanı bağlantı hatası:** Docker container ayarlarını, port ve kullanıcı bilgilerini kontrol et.
- **ExtentReport oluşmuyor:** Sürüm uyumsuzluğu veya FreeMarker hatası varsa 4.1.7 sürümünü kullan.
- **Türkçe karakter sorunu:** Proje yolunda ve dosya adlarında Türkçe karakter kullanma.
- **Test adımları atlanıyor:** Akış sırasını ve locator’ları gözden geçir.
- **Jenkins pipeline hatası:** Docker ve Maven adımlarının doğru sırada ve ortamda çalıştığından emin ol.

---

## Katkı
- Katkıda bulunmak için fork’layıp PR gönderebilirsin.

---

## İletişim
Her türlü soru, öneri ve katkı için iletişime geçebilirsin.

---

**Not:**
- Proje, gerçek bir E2E test otomasyon framework’ü örneğidir ve kurumsal projelerde kullanılabilecek profesyonel bir altyapı sunar.
- Kodun her adımı, loglar ve raporlar ile şeffaf şekilde izlenebilir ve geliştirilebilir.