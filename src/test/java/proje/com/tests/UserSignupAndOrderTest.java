// Tüm E2E akışı baştan sona test eden ana test sınıfı.
// Her adımda random kullanıcı oluşturulur, form doldurulur, veritabanına kayıt yapılır, ürün eklenir, ödeme yapılır ve raporlanır.
package proje.com.tests;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;
import proje.com.base.BaseTest;
import proje.com.model.User;
import proje.com.pages.*;
import proje.com.util.DatabaseUtil;
import proje.com.util.RandomUserGenerator;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import proje.com.util.Messages;

public class UserSignupAndOrderTest extends BaseTest {
    // ExtentReports raporlama nesneleri
    private static ExtentReports extentReports;
    private static ExtentTest extentTest;
    private static ExtentHtmlReporter htmlReporter;
    // Test için oluşturulan random kullanıcı
    private User user;

    // Test sınıfı başlamadan önce raporlama ayarlarını yapar
    @BeforeClass
    public void setupReport() {
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter("ExtentReport.html");
        htmlReporter.config().setTheme(Theme.STANDARD);
        htmlReporter.config().setDocumentTitle("Automation Exercise Test Report");
        htmlReporter.config().setReportName("E2E Test Report");
        extentReports = new ExtentReports();
        extentReports.attachReporter(htmlReporter);
        // Metadata
        extentReports.setSystemInfo("Browser", "Chrome");
        extentReports.setSystemInfo("OS", System.getProperty("os.name"));
        extentReports.setSystemInfo("User", System.getProperty("user.name"));
        extentReports.setSystemInfo("Test Date", java.time.LocalDateTime.now().toString());
    }

    // Ana test metodu - Tüm E2E akışını test eder
    @Test(description = "User Signup, Add Product, Cart Check, Payment")
    public void testUserSignupAndOrder() {
        String baseUrl = "https://automationexercise.com/";
        extentTest = extentReports.createTest("User Signup, Add Product, Cart Check, Payment");
        long startTime = System.currentTimeMillis();
        driver.get(baseUrl);
        extentTest.info(MarkupHelper.createLabel("[NAVIGATION] Siteye gidildi: " + baseUrl, ExtentColor.BLUE));

        HomePage homePage = new HomePage(driver);
        homePage.goToSignupLogin();
        extentTest.info(MarkupHelper.createLabel("[NAVIGATION] Signup/Login butonuna tıklandı.", ExtentColor.BLUE));

        // Signup Bölümü
        extentTest.info(MarkupHelper.createLabel("[SIGNUP] Signup/Login sayfasına gidildi: " + baseUrl + "login", ExtentColor.BLUE));
        SignupPage signupPage = new SignupPage(driver);
        user = RandomUserGenerator.generate();
        signupPage.signupBasic(user.name, user.email);
        extentTest.info(MarkupHelper.createLabel("Kullanıcı Bilgileri: Name=" + user.name + ", Email=" + user.email + ", Password=" + user.password, ExtentColor.BLUE));
        signupPage.fillAccountInfo(user);
        extentTest.info(MarkupHelper.createLabel("Adres Bilgileri: " + user.firstName + " " + user.lastName + ", " + user.company + ", " + user.address1 + ", " + user.address2 + ", " + user.country + ", " + user.state + ", " + user.city + ", " + user.zipcode + ", " + user.mobileNumber, ExtentColor.GREEN));
        DatabaseUtil.insertUser(user);
        DatabaseUtil.insertCard(user);
        extentTest.info(MarkupHelper.createLabel("Kullanıcı ve kart bilgisi PostgreSQL'e kaydedildi: User=" + user.email + ", Card=" + user.cardNumber.substring(user.cardNumber.length()-4), ExtentColor.GREEN));
        Assert.assertTrue(signupPage.isAccountCreatedMessageVisible(), Messages.ACCOUNT_CREATED + " mesajı görünmedi!");
        extentTest.pass(MarkupHelper.createLabel(Messages.ACCOUNT_CREATED + " mesajı başarıyla görüntülendi.", ExtentColor.GREEN));
        signupPage.clickContinueAfterAccountCreated();
        extentTest.info(MarkupHelper.createLabel("Account Created sonrası Continue tıklandı, kullanıcı login oldu", ExtentColor.BLUE));

        // Ürün ve Sepet Bölümü
        extentTest.info(MarkupHelper.createLabel("[PRODUCT & CART] Ürün ekleme ve sepet işlemleri başlıyor.", ExtentColor.BLUE));
        ProductPage productPage = new ProductPage(driver);
        productPage.addFirstProductToCart();
        extentTest.info(MarkupHelper.createLabel("İlk ürün sepete eklendi (Add to cart butonuna tıklandı)", ExtentColor.GREEN));
        productPage.goToCart();
        extentTest.info(MarkupHelper.createLabel("Sepete gidildi: " + baseUrl + "view_cart", ExtentColor.GREEN));
        CartPage cartPage = new CartPage(driver);
        String productName = cartPage.getProductNameInCart();
        if (productName == null) {
            extentTest.warning(MarkupHelper.createLabel("Sepette ürün bulunamadı!", ExtentColor.RED));
        } else {
            extentTest.pass(MarkupHelper.createLabel("Sepette ürün bulundu: " + productName, ExtentColor.GREEN));
        }
        cartPage.proceedToCheckout();
        extentTest.info(MarkupHelper.createLabel("Ödeme adımına geçildi: " + baseUrl + "checkout", ExtentColor.GREEN));

        // Ödeme Bölümü
        extentTest.info(MarkupHelper.createLabel("[PAYMENT] Ödeme işlemleri başlıyor.", ExtentColor.BLUE));
        PaymentPage paymentPage = new PaymentPage(driver);
        paymentPage.placeOrder();
        extentTest.info(MarkupHelper.createLabel("Place Order butonuna tıklandı", ExtentColor.BLUE));
        extentTest.info(MarkupHelper.createLabel("Kart Bilgisi: Name=" + user.cardName + ", Number=**** **** **** " + user.cardNumber.substring(user.cardNumber.length()-4) + ", CVC=" + user.cvc + ", Exp=" + user.expMonth + "/" + user.expYear, ExtentColor.BLUE));
        paymentPage.fillAndSubmitPaymentForm(user);
        extentTest.pass(MarkupHelper.createLabel("Ödeme formu random kart bilgisiyle dolduruldu ve sipariş tamamlandı (simüle edildi)", ExtentColor.GREEN));
        Assert.assertTrue(paymentPage.isOrderConfirmedVisible(), "Order confirmation yazısı görünmedi!");
        extentTest.pass(MarkupHelper.createLabel("Order confirmation yazısı başarıyla görüntülendi.", ExtentColor.GREEN));
        paymentPage.clickContinueAfterOrderPlaced();
        extentTest.info(MarkupHelper.createLabel("Order placed sonrası Continue tıklandı.", ExtentColor.BLUE));
        paymentPage.clickLogout();
        extentTest.info(MarkupHelper.createLabel("Logout işlemi başarıyla yapıldı, test tamamlandı.", ExtentColor.GREEN));

        // Test Sonucu Özeti
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;
        extentTest.info(MarkupHelper.createLabel("[SUMMARY] Test toplam süresi: " + duration + " saniye | Kullanıcı: " + user.email + " | Kart: ****" + user.cardNumber.substring(user.cardNumber.length()-4), ExtentColor.BLUE));
    }

    // Her test metodu sonrası çalışır - Başarı/başarısızlık durumunda screenshot alır
    @AfterMethod
    public void screenShotOnFailure(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String caseDescription = result.getMethod().getDescription();
        if (result.getStatus() == ITestResult.SUCCESS) {
            System.out.println("[Passed] -" + caseDescription + "\n" + result.getClass().getName() + "\n");
            String logText = "Test Method " + methodName + " Successful<br>";
            extentTest.log(Status.PASS, MarkupHelper.createLabel(logText, ExtentColor.GREEN));
        } else if (result.getStatus() == ITestResult.FAILURE) {
            System.out.println("[Failed] -" + caseDescription + "\n" + result.getClass().getName() + "\n");
            System.out.println(result.getThrowable());
            System.out.println(Arrays.toString(result.getThrowable().getStackTrace()) + "\n");
            String exceptionMessage = Arrays.toString(result.getThrowable().getStackTrace());
            extentTest.fail("<details><summary><b><font color=red>Exception Occured, click to see details:" + "</font></b></summary>" + exceptionMessage + "</details>");
            String path = takeScreenshot(methodName);
            try {
                extentTest.fail("<b><font color=red>Screen of failing</font></b><br>", MediaEntityBuilder.createScreenCaptureFromPath(path).build());
            } catch (Exception e) {
                extentTest.fail("Test failed, cannot attach screenshot");
            }
            String logText = "Test Method " + methodName + " Failed<br>";
            extentTest.log(Status.FAIL, MarkupHelper.createLabel(logText, ExtentColor.RED));
        }
    }

    // Test sınıfı bittikten sonra raporu kaydeder
    @AfterClass
    public void tearDown() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }

    // Screenshot dosya adını oluşturur
    private String getScreenshotName(String methodName) {
        Date d = new Date();
        String fileName = methodName + "_" + d.toString().replace(":", "_").replace(" ", "_") + ".png";
        return fileName;
    }

    // Screenshot alır ve dosyaya kaydeder
    private String takeScreenshot(String methodName) {
        String fileName = getScreenshotName(methodName);
        String directory = "./Screenshot/";
        new File(directory).mkdirs();
        String path = directory + fileName;
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshot, new File(path));
            System.out.println("****************");
            System.out.println("Screenshot stored at: " + path);
            System.out.println("****************");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }
} 