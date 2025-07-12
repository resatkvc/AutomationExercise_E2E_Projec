// Signup (kayıt) sayfası için Page Object sınıfı.
// Form doldurma, hesap oluşturma ve doğrulama işlemleri burada.
package proje.com.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import proje.com.base.BasePage;
import proje.com.model.User;
import java.time.Duration;

public class SignupPage extends BasePage {
    // Temel signup form alanları
    @FindBy(xpath = "//input[@data-qa='signup-name']")
    private WebElement nameInput;
    @FindBy(xpath = "//input[@data-qa='signup-email']")
    private WebElement emailInput;
    @FindBy(xpath = "//button[@data-qa='signup-button']")
    private WebElement signupButton;

    // Account info form alanları
    @FindBy(id = "id_gender1")
    private WebElement mrRadio;
    @FindBy(id = "id_gender2")
    private WebElement mrsRadio;
    @FindBy(id = "password")
    private WebElement passwordInput;
    @FindBy(id = "days")
    private WebElement daysDropdown;
    @FindBy(id = "months")
    private WebElement monthsDropdown;
    @FindBy(id = "years")
    private WebElement yearsDropdown;
    @FindBy(id = "first_name")
    private WebElement firstNameInput;
    @FindBy(id = "last_name")
    private WebElement lastNameInput;
    @FindBy(id = "company")
    private WebElement companyInput;
    @FindBy(id = "address1")
    private WebElement address1Input;
    @FindBy(id = "address2")
    private WebElement address2Input;
    @FindBy(id = "country")
    private WebElement countryDropdown;
    @FindBy(id = "state")
    private WebElement stateInput;
    @FindBy(id = "city")
    private WebElement cityInput;
    @FindBy(id = "zipcode")
    private WebElement zipcodeInput;
    @FindBy(id = "mobile_number")
    private WebElement mobileNumberInput;
    @FindBy(xpath = "//button[@data-qa='create-account']")
    private WebElement createAccountButton;
    @FindBy(xpath = "//a[@data-qa='continue-button']")
    private WebElement continueButton;
    @FindBy(xpath = "//p[contains(text(),'Congratulations! Your new account has been successfully created!')]")
    private WebElement accountCreatedMessage;

    // WebDriverWait nesnesi - güvenli etkileşim için
    private WebDriverWait wait;

    // Constructor - BasePage'e WebDriver'ı geçer ve wait nesnesini başlatır
    public SignupPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Temel signup adımı (isim/email girip devam)
    public void signupBasic(String name, String email) {
        wait.until(ExpectedConditions.visibilityOf(nameInput)).sendKeys(name);
        wait.until(ExpectedConditions.visibilityOf(emailInput)).sendKeys(email);
        wait.until(ExpectedConditions.elementToBeClickable(signupButton)).click();
    }

    // Tüm kullanıcı bilgilerini doldurur ve hesap oluşturur
    public void fillAccountInfo(User user) {
        if (user.title.equalsIgnoreCase("Mr")) {
            wait.until(ExpectedConditions.elementToBeClickable(mrRadio)).click();
        } else {
            wait.until(ExpectedConditions.elementToBeClickable(mrsRadio)).click();
        }
        wait.until(ExpectedConditions.visibilityOf(passwordInput)).sendKeys(user.password);
        wait.until(ExpectedConditions.visibilityOf(daysDropdown)).sendKeys(user.birthDay);
        wait.until(ExpectedConditions.visibilityOf(monthsDropdown)).sendKeys(user.birthMonth);
        wait.until(ExpectedConditions.visibilityOf(yearsDropdown)).sendKeys(user.birthYear);
        wait.until(ExpectedConditions.visibilityOf(firstNameInput)).sendKeys(user.firstName);
        wait.until(ExpectedConditions.visibilityOf(lastNameInput)).sendKeys(user.lastName);
        wait.until(ExpectedConditions.visibilityOf(companyInput)).sendKeys(user.company);
        wait.until(ExpectedConditions.visibilityOf(address1Input)).sendKeys(user.address1);
        wait.until(ExpectedConditions.visibilityOf(address2Input)).sendKeys(user.address2);
        wait.until(ExpectedConditions.visibilityOf(countryDropdown)).sendKeys(user.country);
        wait.until(ExpectedConditions.visibilityOf(stateInput)).sendKeys(user.state);
        wait.until(ExpectedConditions.visibilityOf(cityInput)).sendKeys(user.city);
        wait.until(ExpectedConditions.visibilityOf(zipcodeInput)).sendKeys(user.zipcode);
        wait.until(ExpectedConditions.visibilityOf(mobileNumberInput)).sendKeys(user.mobileNumber);
        wait.until(ExpectedConditions.elementToBeClickable(createAccountButton)).click();
    }

    // Hesap oluşturulduktan sonra devam butonuna tıklar
    public void clickContinueAfterAccountCreated() {
        wait.until(ExpectedConditions.elementToBeClickable(continueButton)).click();
    }

    // Hesap oluşturma mesajı görünüyor mu kontrolü
    public boolean isAccountCreatedMessageVisible() {
        return wait.until(ExpectedConditions.visibilityOf(accountCreatedMessage)).isDisplayed();
    }
} 