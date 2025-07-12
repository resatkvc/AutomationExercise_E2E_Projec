// Ana sayfa için Page Object sınıfı.
// Sadece Signup/Login butonuna tıklama işlemi var.
package proje.com.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import proje.com.base.BasePage;

public class HomePage extends BasePage {
    // Signup/Login butonunun locator'ı
    @FindBy(xpath = "//a[contains(text(),'Signup / Login')]")
    private WebElement signupLoginButton;

    // Constructor - BasePage'e WebDriver'ı geçer
    public HomePage(WebDriver driver) {
        super(driver);
    }

    // Signup/Login sayfasına yönlendirir
    public void goToSignupLogin() {
        signupLoginButton.click();
    }
} 