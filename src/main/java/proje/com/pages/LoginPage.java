// Login (giriş) sayfası için Page Object sınıfı.
// Email ve şifre ile giriş işlemi burada yapılır.
package proje.com.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import proje.com.base.BasePage;
import java.time.Duration;

public class LoginPage extends BasePage {
    // Login form alanları
    @FindBy(xpath = "//input[@data-qa='login-email']")
    private WebElement emailInput;
    @FindBy(xpath = "//input[@data-qa='login-password']")
    private WebElement passwordInput;
    @FindBy(xpath = "//button[@data-qa='login-button']")
    private WebElement loginButton;

    // WebDriverWait nesnesi - güvenli etkileşim için
    private WebDriverWait wait;

    // Constructor - BasePage'e WebDriver'ı geçer ve wait nesnesini başlatır
    public LoginPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Email ve şifre ile giriş yapar
    public void login(String email, String password) {
        wait.until(ExpectedConditions.visibilityOf(emailInput)).sendKeys(email);
        wait.until(ExpectedConditions.visibilityOf(passwordInput)).sendKeys(password);
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
    }
} 