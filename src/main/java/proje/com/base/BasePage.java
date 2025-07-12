// Tüm sayfa (Page Object) sınıflarının kalıtım aldığı temel sınıf.
// Ortak WebDriver referansı ve PageFactory başlatması burada yapılır.
package proje.com.base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public abstract class BasePage {
    // Sayfa nesnelerinde kullanılacak WebDriver referansı
    protected WebDriver driver;

    // Her sayfa için WebDriver atanır ve elementler PageFactory ile başlatılır.
    public BasePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
} 