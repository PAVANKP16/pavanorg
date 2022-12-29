package generic;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseTest {

	public WebDriver driver;
	public WebDriverWait wait;
	public static final String XLPATH = "./data/input.xlsx";
	public static final String REPORTPATH = "./target/MyReport.html";
	public static ExtentReports eReport;
	public ExtentTest test;
	
	@BeforeSuite
	public void initReport()
	{
		eReport = new ExtentReports();
		ExtentSparkReporter reportType = new ExtentSparkReporter(REPORTPATH);
		eReport.attachReporter(reportType);
	}
	
	@AfterSuite
	public void publishReport()
	{
		eReport.flush();
	}
	
	
	@Parameters("property")
	@BeforeMethod
	public void openApp(@Optional("config.properties") String propertyfile, Method testMethod) throws MalformedURLException
	{
		String testName = testMethod.getName();
		test = eReport.createTest(testName);
		
		
		String useGrid = Utility.getPropertyValue(propertyfile, "USERGRID");
		test.info("USERGRID: " + useGrid);
		String gridURL = Utility.getPropertyValue(propertyfile, "GRIDURL");
		test.info("GRIDURL: "+gridURL);
		String browser = Utility.getPropertyValue(propertyfile, "BROWSER");
		test.info("BROWSER: "+browser);
		String appurl = Utility.getPropertyValue(propertyfile, "APP_URL");
		test.info("APP_URL: "+appurl);
		String strITO = Utility.getPropertyValue(propertyfile, "ITO");
		long lITO = Long.parseLong(strITO);
		test.info("SITO: "+strITO);
		String strETO = Utility.getPropertyValue(propertyfile, "ETO");
		long lETO = Long.parseLong(strETO);
		test.info("strETO: "+strETO);
		
		if (useGrid.equalsIgnoreCase("yes")) 
		{
			test.info("Using GRID");
			URL remoteURL = new URL(gridURL);
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setBrowserName(browser);
			driver = new RemoteWebDriver(remoteURL,capabilities);
			
			
		} else {
			
			test.info("Not using GRID , running Locally");
			switch (browser.toLowerCase()) {
			
			case "chrome":
				test.info("Riunning in Chrome");
				WebDriverManager.chromedriver().setup();
				driver = new ChromeDriver();
				break;

			default:
				test.warning("Running on default browser");
			case "firefox":
				test.info("Running on Firefox browser");
				WebDriverManager.firefoxdriver().setup();
				driver = new FirefoxDriver();
				break;
			}

		}
		test.info("Entering url");
		driver.get(appurl);
		test.info("setting ITO");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(lITO));
		test.info("Setting ETO");
		wait = new WebDriverWait(driver, Duration.ofSeconds(lETO));
	}
	
	
	@AfterMethod
	public void closeApp(ITestResult result) throws IOException
	{
		String testName = result.getName();
		int status = result.getStatus();
		
		if (status==2) {
			TakesScreenshot tDriver = (TakesScreenshot)driver;
			File scffile = tDriver.getScreenshotAs(OutputType.FILE);
			File dstfile = new File("./screenshot/"+testName+".png");
			FileUtils.copyFile(scffile, dstfile);
			test.info("Test: "+testName+ " Failed and screenshot has been taken :" +dstfile);
			test.addScreenCaptureFromPath("./../screenshot/"+testName+".png");
			String msg = result.getThrowable().getMessage();
			test.fail("Test "+testName+" is Failed due to "+msg);
			
		}
		test.info("closing the browser");
		driver.close();
	}
	
}
