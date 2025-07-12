// Ödeme sayfası için Page Object sınıfı.
// Kart bilgisi doldurma, siparişi tamamlama, logout işlemleri burada.
package proje.com.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import proje.com.base.BasePage;
import java.time.Duration;

public class PaymentPage extends BasePage {
    // Ödeme form alanları
    @FindBy(xpath = "//a[contains(text(),'Place Order')]")
    private WebElement placeOrderButton;
    @FindBy(name = "name_on_card")
    private WebElement nameOnCardInput;
    @FindBy(name = "card_number")
    private WebElement cardNumberInput;
    @FindBy(name = "cvc")
    private WebElement cvcInput;
    @FindBy(name = "expiry_month")
    private WebElement expMonthInput;
    @FindBy(name = "expiry_year")
    private WebElement expYearInput;
    @FindBy(xpath = "//button[contains(text(),'Pay and Confirm Order')]")
    private WebElement payAndConfirmOrderButton;
    @FindBy(xpath = "//h2[contains(text(),'ORDER PLACED!')]")
    private WebElement orderPlacedHeader;
    @FindBy(xpath = "//a[@data-qa='continue-button']")
    private WebElement continueButton;
    @FindBy(xpath = "//a[contains(text(),'Logout')]")
    private WebElement logoutButton;
    @FindBy(xpath = "//p[contains(text(),'Congratulations! Your order has been confirmed!')]")
    private WebElement orderConfirmedText;

    // WebDriverWait nesnesi - güvenli etkileşim için
    private WebDriverWait wait;

    // Constructor - BasePage'e WebDriver'ı geçer ve wait nesnesini başlatır
    public PaymentPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Sipariş verme butonuna tıklar
    public void placeOrder() {
        wait.until(ExpectedConditions.elementToBeClickable(placeOrderButton)).click();
    }

    // Kart bilgilerini doldurur ve ödemeyi tamamlar
    public void fillAndSubmitPaymentForm(proje.com.model.User user) {
        wait.until(ExpectedConditions.visibilityOf(nameOnCardInput)).sendKeys(user.cardName);
        wait.until(ExpectedConditions.visibilityOf(cardNumberInput)).sendKeys(user.cardNumber);
        wait.until(ExpectedConditions.visibilityOf(cvcInput)).sendKeys(user.cvc);
        wait.until(ExpectedConditions.visibilityOf(expMonthInput)).sendKeys(user.expMonth);
        wait.until(ExpectedConditions.visibilityOf(expYearInput)).sendKeys(user.expYear);
        wait.until(ExpectedConditions.elementToBeClickable(payAndConfirmOrderButton)).click();
    }

    // Sipariş verildi mesajı görünüyor mu kontrolü
    public boolean isOrderPlacedVisible() {
        return wait.until(ExpectedConditions.visibilityOf(orderPlacedHeader)).isDisplayed();
    }

    // Sipariş onaylandı mesajı görünüyor mu kontrolü
    public boolean isOrderConfirmedVisible() {
        return wait.until(ExpectedConditions.visibilityOf(orderConfirmedText)).isDisplayed();
    }

    // Sipariş verildikten sonra devam butonuna tıklar
    public void clickContinueAfterOrderPlaced() {
        wait.until(ExpectedConditions.elementToBeClickable(continueButton)).click();
    }

    // Çıkış yapar
    public void clickLogout() {
        wait.until(ExpectedConditions.elementToBeClickable(logoutButton)).click();
    }
} 