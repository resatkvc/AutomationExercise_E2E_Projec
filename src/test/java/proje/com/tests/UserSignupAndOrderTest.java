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
    private static ExtentReports extentReports;
    private static ExtentTest extentTest;
    private static ExtentHtmlReporter htmlReporter;
    private User user;

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

    @Test(description = "User Signup, Add Product, Cart Check, Payment")
    public void testUserSignupAndOrder() {
        String baseUrl = "https://automationexercise.com/";
        extentTest = extentReports.createTest("User Signup, Add Product, Cart Check, Payment");
        long startTime = System.currentTimeMillis();
        driver.get(baseUrl);
        extentTest.info("<b style='color:#1976d2'>[NAVIGATION]</b> Siteye gidildi (<span style='color:#388e3c'>" + baseUrl + "</span>)");

        HomePage homePage = new HomePage(driver);
        homePage.goToSignupLogin();
        extentTest.info("<b style='color:#1976d2'>[NAVIGATION]</b> Signup/Login butonuna tıklandı.");

        // Signup Bölümü
        extentTest.info("<b style='color:#1976d2'>[SIGNUP]</b> Signup/Login sayfasına gidildi (<span style='color:#388e3c'>" + baseUrl + "login</span>)");
        SignupPage signupPage = new SignupPage(driver);
        user = RandomUserGenerator.generate();
        signupPage.signupBasic(user.name, user.email);
        extentTest.info("<b>Kullanıcı Bilgileri:</b> Name=<span style='color:#1976d2'>" + user.name + "</span>, Email=<span style='color:#1976d2'>" + user.email + "</span>, Password=<span style='color:#1976d2'>" + user.password + "</span>");
        signupPage.fillAccountInfo(user);
        extentTest.info("<b>Adres Bilgileri:</b> <span style='color:#388e3c'>" + user.firstName + " " + user.lastName + ", " + user.company + ", " + user.address1 + ", " + user.address2 + ", " + user.country + ", " + user.state + ", " + user.city + ", " + user.zipcode + ", " + user.mobileNumber + "</span>");
        DatabaseUtil.insertUser(user);
        DatabaseUtil.insertCard(user);
        extentTest.info("<b>Kullanıcı ve kart bilgisi PostgreSQL'e kaydedildi:</b> [User: <span style='color:#1976d2'>" + user.email + "</span>, Card: <span style='color:#d32f2f'>" + user.cardNumber.substring(user.cardNumber.length()-4) + "</span>]");
        Assert.assertTrue(signupPage.isAccountCreatedMessageVisible(), Messages.ACCOUNT_CREATED + " mesajı görünmedi!");
        extentTest.pass("<span style='color:#388e3c'>" + Messages.ACCOUNT_CREATED + "</span> mesajı başarıyla görüntülendi.");
        signupPage.clickContinueAfterAccountCreated();
        extentTest.info("Account Created sonrası Continue tıklandı, kullanıcı login oldu");

        // Ürün ve Sepet Bölümü
        extentTest.info("<b style='color:#1976d2'>[PRODUCT & CART]</b> Ürün ekleme ve sepet işlemleri başlıyor.");
        ProductPage productPage = new ProductPage(driver);
        productPage.addFirstProductToCart();
        extentTest.info("İlk ürün sepete eklendi (Add to cart butonuna tıklandı)");
        productPage.goToCart();
        extentTest.info("Sepete gidildi (<span style='color:#388e3c'>" + baseUrl + "view_cart</span>)");
        CartPage cartPage = new CartPage(driver);
        String productName = cartPage.getProductNameInCart();
        if (productName == null) {
            extentTest.warning("<b style='color:#d32f2f'>Sepette ürün bulunamadı!</b>");
        } else {
            extentTest.pass("Sepette ürün bulundu: <b style='color:#388e3c'>" + productName + "</b>");
        }
        cartPage.proceedToCheckout();
        extentTest.info("Ödeme adımına geçildi (<span style='color:#388e3c'>" + baseUrl + "checkout</span>)");

        // Ödeme Bölümü
        extentTest.info("<b style='color:#1976d2'>[PAYMENT]</b> Ödeme işlemleri başlıyor.");
        PaymentPage paymentPage = new PaymentPage(driver);
        paymentPage.placeOrder();
        extentTest.info("Place Order butonuna tıklandı");
        extentTest.info("<b>Kart Bilgisi:</b> Name=<span style='color:#1976d2'>" + user.cardName + "</span>, Number=<span style='color:#d32f2f'>**** **** **** " + user.cardNumber.substring(user.cardNumber.length()-4) + "</span>, CVC=<span style='color:#1976d2'>" + user.cvc + "</span>, Exp=<span style='color:#1976d2'>" + user.expMonth + "/" + user.expYear + "</span>");
        paymentPage.fillAndSubmitPaymentForm(user);
        extentTest.pass("Ödeme formu random kart bilgisiyle dolduruldu ve sipariş tamamlandı (simüle edildi)");
        Assert.assertTrue(paymentPage.isOrderConfirmedVisible(), "Order confirmation yazısı görünmedi!");
        extentTest.pass("<span style='color:#388e3c'>Order confirmation yazısı başarıyla görüntülendi.</span>");
        paymentPage.clickContinueAfterOrderPlaced();
        extentTest.info("Order placed sonrası Continue tıklandı.");
        paymentPage.clickLogout();
        extentTest.info("Logout işlemi başarıyla yapıldı, test tamamlandı.");

        // Test Sonucu Özeti
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;
        extentTest.info("<b style='color:#1976d2'>[SUMMARY]</b> Test toplam süresi: <span style='color:#388e3c'>" + duration + " saniye</span> | Kullanıcı: <span style='color:#1976d2'>" + user.email + "</span> | Kart: <span style='color:#d32f2f'>****" + user.cardNumber.substring(user.cardNumber.length()-4) + "</span>");
    }

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

    @AfterClass
    public void tearDown() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }

    private String getScreenshotName(String methodName) {
        Date d = new Date();
        String fileName = methodName + "_" + d.toString().replace(":", "_").replace(" ", "_") + ".png";
        return fileName;
    }

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