// Sepet sayfası için Page Object sınıfı.
// Sepetteki ürün adını okuma ve ödeme adımına geçiş burada.
package proje.com.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import proje.com.base.BasePage;
import java.time.Duration;

public class CartPage extends BasePage {
    // Sepet sayfası elementleri
    @FindBy(xpath = "//td[@class='cart_description']/h4/a")
    private WebElement productNameInCart;
    @FindBy(xpath = "//a[contains(text(),'Proceed To Checkout')]")
    private WebElement proceedToCheckoutButton;

    // WebDriverWait nesnesi - güvenli etkileşim için
    private WebDriverWait wait;

    // Constructor - BasePage'e WebDriver'ı geçer ve wait nesnesini başlatır
    public CartPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Sepetteki ürün adını döndürür
    public String getProductNameInCart() {
        return wait.until(ExpectedConditions.visibilityOf(productNameInCart)).getText();
    }

    // Ödeme adımına geçer
    public void proceedToCheckout() {
        wait.until(ExpectedConditions.elementToBeClickable(proceedToCheckoutButton)).click();
    }
} 