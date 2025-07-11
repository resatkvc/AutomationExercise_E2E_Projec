# AutomationExercise E2E Test Automation Project

## Proje Hakkında
Bu proje, [https://automationexercise.com](https://automationexercise.com) sitesi üzerinde uçtan uca (E2E) test otomasyonu yapmak için geliştirilmiştir. Proje, profesyonel bir test otomasyon framework’ü olarak Java, Selenium WebDriver, TestNG, ExtentReports, PostgreSQL, Docker ve Jenkins teknolojilerini bir arada kullanır. Kod yapısı Page Object Model (POM) mimarisiyle düzenlenmiştir.

---

## İçerik
- [Kullanılan Teknolojiler](#kullanılan-teknolojiler)
- [Kurulum ve Çalıştırma](#kurulum-ve-çalıştırma)
- [Proje Mimarisi ve Klasör Yapısı](#proje-mimarisi-ve-klasör-yapısı)
- [Test Akışı](#test-akışı)
- [Veritabanı Entegrasyonu](#veritabanı-entegrasyonu)
- [Random Veri Üretimi](#random-veri-üretimi)
- [Raporlama (ExtentReports)](#raporlama-extentreports)
- [CI/CD ve Jenkins](#cicd-ve-jenkins)
- [Sık Karşılaşılan Sorunlar ve Çözümler](#sık-karşılaşılan-sorunlar-ve-çözümler)
- [Katkı ve Lisans](#katkı-ve-lisans)

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

### 3. **Veritabanı Bağlantısı**
- Bağlantı bilgileri `src/main/java/proje/com/util/DatabaseUtil.java` dosyasında:
  - URL: `jdbc:postgresql://localhost:5432/testdb`
  - USER: `testuser`
  - PASSWORD: `testpass`
- Dilersen DBeaver, pgAdmin gibi araçlarla da bağlanıp kayıtları görebilirsin.

### 4. **Bağımlılıkları Yükle**
```bash
mvn clean install
```

### 5. **Testi Çalıştır**
- IDE üzerinden `UserSignupAndOrderTest.java` dosyasındaki class veya method başındaki “Run” tuşuna bas.
- Veya terminalden:
```bash
mvn test
```

### 6. **Raporu Görüntüle**
- Proje kökünde oluşan `ExtentReport.html` dosyasını tarayıcıda aç.

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
└── postgres_data/                  # (Opsiyonel) Docker volume için
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
- Jenkins ile pipeline kurulumu için örnek `Jenkinsfile` eklenebilir.
- Pipeline’da:
  - Docker servisleri başlatılır
  - Maven ile testler çalıştırılır
  - Raporlar arşivlenir
- Jenkins kurulumu ve pipeline entegrasyonu için adım adım dökümantasyon hazırlanmıştır.

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