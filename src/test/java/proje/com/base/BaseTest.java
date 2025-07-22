// Tüm test sınıflarının kalıtım aldığı temel test sınıfı.
// Test başlatma, bitirme, driver yönetimi ve raporlama burada yapılır.
package proje.com.base;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

public class BaseTest {
    // Tüm test sınıflarında kullanılacak WebDriver referansı
    protected WebDriver driver;
    
    // ExtentReports raporlama nesneleri - Tüm test sınıfları için ortak
    protected static ExtentReports extentReports;
    protected static ExtentTest extentTest;
    protected static ExtentHtmlReporter htmlReporter;

    // Test sınıfı başlamadan önce raporlama ayarlarını yapar
    @BeforeClass
    public void setupReport() {
        htmlReporter = new ExtentHtmlReporter("ExtentReport.html");
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

    // Her test metodu öncesi çalışır - WebDriver'ı başlatır
    @BeforeMethod
    public void setUp() {
        // WebDriverManager otomatik olarak uygun driver'ı indirir
        WebDriverManager.chromedriver().setup();
        
        // Chrome options ayarları
        ChromeOptions options = new ChromeOptions();
        
        // Jenkins ortamında headless mode kullan
        if (System.getenv("JENKINS_URL") != null || System.getenv("BUILD_ID") != null) {
            options.addArguments("--headless");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--remote-allow-origins=*");
        }
        
        // Temel güvenlik ayarları
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-plugins");
        options.addArguments("--disable-web-security");
        options.addArguments("--allow-running-insecure-content");
        options.addArguments("--disable-blink-features=AutomationControlled");
        
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }

    // Her test metodu sonrası çalışır - Başarı/başarısızlık durumunda screenshot alır ve raporlar
    @AfterMethod
    public void tearDown(ITestResult result) {
        // Test sonucunu raporla
        reportTestResult(result);
        
        // WebDriver'ı kapat
        if (driver != null) {
            driver.quit();
        }
    }
    
    // Test sınıfı bittikten sonra raporu kaydeder
    @AfterClass
    public void tearDownReport() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }
    
    // Test sonucunu raporlar
    private void reportTestResult(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String caseDescription = result.getMethod().getDescription();
        
        if (result.getStatus() == ITestResult.SUCCESS) {
            System.out.println("[Passed] -" + caseDescription + "\n" + result.getClass().getName() + "\n");
            String logText = "Test Method " + methodName + " Successful<br>";
            if (extentTest != null) {
                extentTest.log(Status.PASS, MarkupHelper.createLabel(logText, ExtentColor.GREEN));
            }
        } else if (result.getStatus() == ITestResult.FAILURE) {
            System.out.println("[Failed] -" + caseDescription + "\n" + result.getClass().getName() + "\n");
            System.out.println(result.getThrowable());
            System.out.println(Arrays.toString(result.getThrowable().getStackTrace()) + "\n");
            
            if (extentTest != null) {
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
    
    // Test başlatma için yardımcı metod
    protected void startTest(String testName, String description) {
        extentTest = extentReports.createTest(testName, description);
    }
    
    // Bilgi logu için yardımcı metod
    protected void logInfo(String message) {
        if (extentTest != null) {
            extentTest.info(MarkupHelper.createLabel(message, ExtentColor.BLUE));
        }
    }
    
    // Başarı logu için yardımcı metod
    protected void logPass(String message) {
        if (extentTest != null) {
            extentTest.pass(MarkupHelper.createLabel(message, ExtentColor.GREEN));
        }
    }
    
    // Uyarı logu için yardımcı metod
    protected void logWarning(String message) {
        if (extentTest != null) {
            extentTest.warning(MarkupHelper.createLabel(message, ExtentColor.ORANGE));
        }
    }
    
    // Hata logu için yardımcı metod
    protected void logFail(String message) {
        if (extentTest != null) {
            extentTest.fail(MarkupHelper.createLabel(message, ExtentColor.RED));
        }
    }
} 