package proje.com.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import proje.com.base.BasePage;

public class HomePage extends BasePage {
    @FindBy(xpath = "//a[contains(text(),'Signup / Login')]")
    private WebElement signupLoginButton;

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void goToSignupLogin() {
        signupLoginButton.click();
    }
} 