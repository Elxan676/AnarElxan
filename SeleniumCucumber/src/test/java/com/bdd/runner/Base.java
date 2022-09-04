package com.automation.library;


import static org.testng.Assert.assertTrue;



import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import com.automation.library.SeleniumGlobalLibrary.Browser;

public class Base {
	// static final Logger log = LogManager.getLogger(Base.class);
	//final static Logger log = Logger.getLogger(Base.class);
	public static Logger log = LoggerFactory.getLogger(Base.class);

	public static WebDriver driver;
	public static SeleniumGlobalLibrary myLibrary;
	private String browserType;
	private String demoMode;
	private String isRemote;
	private String isHeadless;
	
	private String sendEmail;
	private String toAddres;
	private String ccAddres;
	private String bccAddres;

	@BeforeClass
	public void beforeAllTestMethods() {
		log.info("Automation Test suite started ...");
		JavaPropertiesManager writeManager = new JavaPropertiesManager("src/test/resources/session.properties");
		writeManager.setProperty("session", getCurrentTime());
				
		// read config file here
		JavaPropertiesManager readManager = new JavaPropertiesManager("src/test/resources/config.properties");
		browserType = readManager.readProperty("browser");
		log.debug("Reading property file: " + "src/test/resources/config.properties");
		log.debug("Browser Type: " + browserType);
		demoMode = readManager.readProperty("demoMode");
		isRemote = readManager.readProperty("remote");
		isHeadless = readManager.readProperty("headless");
					
		// reading email config keys
		sendEmail = readManager.readProperty("sendEmail");
		toAddres = readManager.readProperty("toAddress");
		ccAddres = readManager.readProperty("ccAddress");
		bccAddres = readManager.readProperty("bccAddress");

	}

	@AfterClass
	public void cleanAfterAllTestMethods() {		
		if (driver != null) {
			driver.quit();
		}
		// send email
		if(sendEmail.toLowerCase().contains("on")){
			EmailManager email = new EmailManager();
			email.setToAddress(toAddres);
			email.setCcAddress(ccAddres);
			email.setBccAddress(bccAddres);
			// adding attachment not yet implemented...
			List<String> attachments = new ArrayList<String>();
			attachments = myLibrary.autoAttachErrorImageToEmail();		
			//attachments.add("test-output/index.html");
			email.setAttachments(attachments);
			
			String emailBody = "Automation tests are Competed." + "<br><br>"					
					+"Contact us if you have any questions."
					+"<br><br>"
					+"Thank you,"
					+"<br>"
					+ "Test Automation Team";
			
			// seding email...
			email.sendEmail("Automation Test Completed", emailBody);
			
		}
		log.info("Automated Test suite ended ...");
	}

	// setupBeforeEachTest
	@BeforeMethod
	public void setUp() {
		try {
			log.info("Running before test setup ...");
			myLibrary = new SeleniumGlobalLibrary();
			// driver = myLibrary.startABrowser(Browser.CHROME);
			
			if(isHeadless.toLowerCase().contains("on")) {
				myLibrary.setIsHeadless(true);
			}

			if(isRemote.toLowerCase().contains("on")) {				
				// remote run
				JavaPropertiesManager reader = new JavaPropertiesManager("src/test/resources/config.properties");
				String hubURL = reader.readProperty("hubURL");
				log.info("hubURL: " + hubURL);
				myLibrary.setIsRemote(true);
				
				if (browserType.toLowerCase().contains("chrome")) {
					driver = myLibrary.startARemoteBrowser(hubURL, Browser.CHROME);
				} else if (browserType.toLowerCase().contains("edge")) {
					driver = myLibrary.startARemoteBrowser(hubURL, Browser.EDGE_CHROMIUM);
				} else if (browserType.toLowerCase().contains("firefox")) {
					driver = myLibrary.startARemoteBrowser(hubURL, Browser.FIREFOX);
				} else if (browserType.toLowerCase().contains("safari")) {
					driver = myLibrary.startARemoteBrowser(hubURL, Browser.SAFARI);
				} else {
					log.error("not implemented yet!", new NullPointerException());
				}	
				
			}else {
				// local run				
				if (browserType.toLowerCase().contains("chrome")) {
					driver = myLibrary.startABrowser(Browser.CHROME);
				} else if (browserType.toLowerCase().contains("edge")) {
					driver = myLibrary.startABrowser(Browser.EDGE_CHROMIUM);
				} else if (browserType.toLowerCase().contains("firefox")) {
					driver = myLibrary.startABrowser(Browser.FIREFOX);
				} else if (browserType.toLowerCase().contains("safari")) {
					driver = myLibrary.startABrowser(Browser.SAFARI);
				} else {
					log.error("not implemented yet!", new NullPointerException());
				}				
			}		

			// driver = myLibrary.startABrowser(Browser.EDGE_CHROMIUM);
			myLibrary.setDriver(driver);
			if (demoMode.toLowerCase().contains("on")) {
				myLibrary.setDemoMode(true);
			}

		} catch (Exception e) {
			log.error("Error", e);
			assertTrue(false);
		}
	}

	// cleanUpAfterEachTest
	@AfterMethod
	public void cleanUp(ITestResult iResult) {
		log.info("Running after test cleanup ...");
		if (ITestResult.FAILURE == iResult.getStatus()) {
			// test failed, call capture screenshot method
			// System.out.println("Test Name: " + iResult.getName());
			myLibrary.takeScreenShot("target/screenshots/", iResult.getName() + "_");
		}

		if (driver != null) {
			//driver.close();
		}
	}

	
	
	//// temporarily we copy the getCurrentTime method here in Base.java
	
	public String getCurrentTime() {
		String finalTimeStamp = null;
		try {
			Date date = new Date();
			String tempTime = new Timestamp(date.getTime()).toString();
			log.debug("original timestamp: [" + tempTime + "]");

			finalTimeStamp = tempTime.replace("-", "").replace(":", "").replace(".", "").replace(" ", "");
			log.debug("final timestamp: [" + finalTimeStamp + "]");
		} catch (Exception e) {
			log.error("Error", e);
			assertTrue(false);
		}
		return finalTimeStamp;
	}
	
	
}
