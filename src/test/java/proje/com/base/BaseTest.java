// Tüm test sınıflarının kalıtım aldığı temel test sınıfı.
// Test başlatma, bitirme, driver yönetimi burada yapılır.
package proje.com.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {
    // Tüm test sınıflarında kullanılacak WebDriver referansı
    protected WebDriver driver;

    // Her test metodu öncesi çalışır - WebDriver'ı başlatır
    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
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