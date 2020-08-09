package com.smarteinc.utility;

import org.testng.ITestResult;
import org.testng.Reporter;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

public class Report {
	
	public ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir")+"/target/surefire-reports/Smarteinc_Automation_Report_V2.html");
	public ExtentReports extent = new ExtentReports();
	public ExtentTest test;
	
	public void startTest(ITestResult result) {
		
		String testClass = result.getTestClass().getName();
		testClass = testClass.substring(testClass.lastIndexOf(".")+1,testClass.length());
		
		test = extent.createTest(testClass+"::"+result.getName());
		if(extent.getStartedReporters().size() == 0) {
			
		    extent.attachReporter(htmlReporter);
			extent.setSystemInfo("Environment","QC".toUpperCase());
			extent.setSystemInfo("User Name",System.getProperty("user.name"));
			
			htmlReporter.config().enableTimeline(false);
			htmlReporter.config().setReportName("Smarte Automation Report");
			htmlReporter.config().setDocumentTitle("Smarte Automation Report");
			
		}
	}
	
	public void saveReport() {
		extent.flush();
	}
	
	public void getResult(ITestResult result) {
		
		String testClass = result.getTestClass().getName();
		testClass = testClass.substring(testClass.lastIndexOf(".")+1,testClass.length());
		
		for(String message : Reporter.getOutput(result)){
			test.log(Status.INFO, message);
		}
		

		if(result.getStatus() == ITestResult.FAILURE){
				test.log(Status.FAIL, MarkupHelper.createLabel("Test Case Failed : "+result.getName(), ExtentColor.RED));
				test.log(Status.FAIL, MarkupHelper.createLabel("Reason : "+result.getThrowable(), ExtentColor.RED));
				test.log(Status.FAIL, result.getThrowable());
				test.assignCategory(testClass);
		}
		else if(result.getStatus() == ITestResult.SKIP && result.getThrowable() instanceof Exception) {
			test.assignCategory(testClass);
			test.log(Status.SKIP, MarkupHelper.createLabel(result.getName() + " - Test Case Skipped due to failed configuration", ExtentColor.ORANGE));
		}
		else if(result.getStatus() == ITestResult.SKIP ) {
			test.assignCategory(testClass);
			test.log(Status.SKIP, MarkupHelper.createLabel(result.getName() + " - Test Case Skipped because  it depends on not successfully finished methods", ExtentColor.ORANGE));
		}
		else if(result.getStatus() == ITestResult.SUCCESS){
			test.log(Status.PASS, MarkupHelper.createLabel(result.getName()+" Test Case PASSED", ExtentColor.GREEN));
			test.assignCategory(testClass);
		}
	}


}
