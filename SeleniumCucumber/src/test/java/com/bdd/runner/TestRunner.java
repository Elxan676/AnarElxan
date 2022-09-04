package com.bdd.runner;

import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import com.cucumber.listener.Reporter;

import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
		plugin = {"com.cucumber.listener.ExtentCucumberFormatter:target/cucumber-reports/report.html"},
		snippets = SnippetType.CAMELCASE,
		features = {"src/test/resources/features"},
		glue = {"com.bdd.stepdefinitions", "com.bdd.sharedstepdefinitions"},
		monochrome = true,
		 tags = {"@Smoke,@Regression"}
		//tags = {"@SmokeTest"}		
		)
public class TestRunner {

	public static WebDriver driver;
	
	@AfterClass
	public static void generateExtentReport() {
		Reporter.loadXMLConfig("src/test/resources/extentConfig.xml");
		Reporter.setSystemInfo("User Name", System.getProperty("user.name"));
		Reporter.setSystemInfo("Time Zone", System.getProperty("user.timezone"));
		Reporter.setSystemInfo("Env", "QA");
	}
	
}















