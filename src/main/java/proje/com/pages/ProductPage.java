package proje.com.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import proje.com.base.BasePage;
import java.time.Duration;

public class ProductPage extends BasePage {
    @FindBy(xpath = "(//a[contains(text(),'Add to cart')])[1]")
    private WebElement firstProductAddToCartButton;
    @FindBy(xpath = "//button[contains(text(),'Continue Shopping')]")
    private WebElement continueShoppingButton;
    @FindBy(xpath = "//a[@href='/view_cart']")
    private WebElement viewCartButton;

    private WebDriverWait wait;

    public ProductPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void addFirstProductToCart() {
        wait.until(ExpectedConditions.elementToBeClickable(firstProductAddToCartButton));
        // Scroll ile butonu görünür yap
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", firstProductAddToCartButton);
        // JS ile tıkla (reklam/iframe engeline karşı)
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", firstProductAddToCartButton);
        wait.until(ExpectedConditions.elementToBeClickable(continueShoppingButton)).click();
    }

    public void goToCart() {
        wait.until(ExpectedConditions.elementToBeClickable(viewCartButton)).click();
    }
} 