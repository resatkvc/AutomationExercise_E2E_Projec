// Tüm E2E akışı baştan sona test eden ana test sınıfı.
// Her adımda random kullanıcı oluşturulur, form doldurulur, veritabanına kayıt yapılır, ürün eklenir, ödeme yapılır ve raporlanır.
package proje.com.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import proje.com.base.BaseTest;
import proje.com.model.User;
import proje.com.pages.*;
import proje.com.util.DatabaseUtil;
import proje.com.util.RandomUserGenerator;
import proje.com.util.Messages;

public class UserSignupAndOrderTest extends BaseTest {
    // Test için oluşturulan random kullanıcı
    private User user;

    // Ana test metodu - Tüm E2E akışını test eder
    @Test(description = "User Signup, Add Product, Cart Check, Payment")
    public void testUserSignupAndOrder() {
        String baseUrl = "https://automationexercise.com/";
        
        // Test başlatma - BaseTest'ten gelen metod
        startTest("User Signup, Add Product, Cart Check, Payment", "Complete E2E test flow");
        
        long startTime = System.currentTimeMillis();
        driver.get(baseUrl);
        logInfo("[NAVIGATION] Siteye gidildi: " + baseUrl);

        HomePage homePage = new HomePage(driver);
        homePage.goToSignupLogin();
        logInfo("[NAVIGATION] Signup/Login butonuna tıklandı.");

        // Signup Bölümü
        logInfo("[SIGNUP] Signup/Login sayfasına gidildi: " + baseUrl + "login");
        SignupPage signupPage = new SignupPage(driver);
        user = RandomUserGenerator.generate();
        signupPage.signupBasic(user.name, user.email);
        logInfo("Kullanıcı Bilgileri: Name=" + user.name + ", Email=" + user.email + ", Password=" + user.password);
        signupPage.fillAccountInfo(user);
        logPass("Adres Bilgileri: " + user.firstName + " " + user.lastName + ", " + user.company + ", " + user.address1 + ", " + user.address2 + ", " + user.country + ", " + user.state + ", " + user.city + ", " + user.zipcode + ", " + user.mobileNumber);
        DatabaseUtil.insertUser(user);
        DatabaseUtil.insertCard(user);
        logPass("Kullanıcı ve kart bilgisi PostgreSQL'e kaydedildi: User=" + user.email + ", Card=" + user.cardNumber.substring(user.cardNumber.length()-4));
        Assert.assertTrue(signupPage.isAccountCreatedMessageVisible(), Messages.ACCOUNT_CREATED + " mesajı görünmedi!");
        logPass(Messages.ACCOUNT_CREATED + " mesajı başarıyla görüntülendi.");
        signupPage.clickContinueAfterAccountCreated();
        logInfo("Account Created sonrası Continue tıklandı, kullanıcı login oldu");

        // Ürün ve Sepet Bölümü
        logInfo("[PRODUCT & CART] Ürün ekleme ve sepet işlemleri başlıyor.");
        ProductPage productPage = new ProductPage(driver);
        productPage.addFirstProductToCart();
        logPass("İlk ürün sepete eklendi (Add to cart butonuna tıklandı)");
        productPage.goToCart();
        logPass("Sepete gidildi: " + baseUrl + "view_cart");
        CartPage cartPage = new CartPage(driver);
        String productName = cartPage.getProductNameInCart();
        if (productName == null) {
            logWarning("Sepette ürün bulunamadı!");
        } else {
            logPass("Sepette ürün bulundu: " + productName);
        }
        cartPage.proceedToCheckout();
        logPass("Ödeme adımına geçildi: " + baseUrl + "checkout");

        // Ödeme Bölümü
        logInfo("[PAYMENT] Ödeme işlemleri başlıyor.");
        PaymentPage paymentPage = new PaymentPage(driver);
        paymentPage.placeOrder();
        logInfo("Place Order butonuna tıklandı");
        logInfo("Kart Bilgisi: Name=" + user.cardName + ", Number=**** **** **** " + user.cardNumber.substring(user.cardNumber.length()-4) + ", CVC=" + user.cvc + ", Exp=" + user.expMonth + "/" + user.expYear);
        paymentPage.fillAndSubmitPaymentForm(user);
        logPass("Ödeme formu random kart bilgisiyle dolduruldu ve sipariş tamamlandı (simüle edildi)");
        Assert.assertTrue(paymentPage.isOrderConfirmedVisible(), "Order confirmation yazısı görünmedi!");
        logPass("Order confirmation yazısı başarıyla görüntülendi.");
        paymentPage.clickContinueAfterOrderPlaced();
        logInfo("Order placed sonrası Continue tıklandı.");
        paymentPage.clickLogout();
        logPass("Logout işlemi başarıyla yapıldı, test tamamlandı.");

        // Test Sonucu Özeti
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;
        logInfo("[SUMMARY] Test toplam süresi: " + duration + " saniye | Kullanıcı: " + user.email + " | Kart: ****" + user.cardNumber.substring(user.cardNumber.length()-4));
    }
} 