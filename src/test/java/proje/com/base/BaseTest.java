// Tüm test sınıflarının kalıtım aldığı temel test sınıfı.
// Test başlatma, bitirme, driver yönetimi burada yapılır.
package proje.com.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {
    // Tüm test sınıflarında kullanılacak WebDriver referansı
    protected WebDriver driver;

    // Her test metodu öncesi çalışır - WebDriver'ı başlatır
    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        
        // Chrome options ayarları
        ChromeOptions options = new ChromeOptions();
        
        // Jenkins ortamında headless mode kullan
        if (System.getenv("JENKINS_URL") != null || System.getenv("BUILD_ID") != null) {
            options.addArguments("--headless");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--remote-allow-origins=*");
        }
        
        // Diğer güvenlik ve performans ayarları
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-plugins");
        options.addArguments("--disable-images");
        options.addArguments("--disable-javascript");
        options.addArguments("--disable-web-security");
        options.addArguments("--allow-running-insecure-content");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        
        // Chrome binary path'i ayarla (Jenkins ortamında)
        String chromeBin = System.getenv("CHROME_BIN");
        if (chromeBin != null && !chromeBin.isEmpty()) {
            options.setBinary(chromeBin);
        }
        
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }

    // Her test metodu sonrası çalışır - WebDriver'ı kapatır
    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
} 