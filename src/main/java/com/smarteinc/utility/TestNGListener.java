package com.smarteinc.utility;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;

public class TestNGListener extends TestListenerAdapter {

	public static Report report = new Report();
	
	@Override
	public void onTestSkipped(ITestResult result) {
		
		super.onTestSkipped(result);
		
		if (result.getThrowable() instanceof Exception) {
			//Reporter.log("Test is skipped due to failed configuration" +result.getName(),true);
			report.startTest(result);
			report.getResult(result);
		} else if(result.getThrowable().getMessage().contains("depends on not successfully finished methods")) {
			Reporter.log("Test is skipped " +result.getName(), true);
				report.startTest(result);
				report.getResult(result);
			}else {
				Reporter.log("Test is skipped " +result.getName(), true);
				report.startTest(result);
				report.getResult(result);
			}
	}
	
	 @Override
	 public void onTestFailure(ITestResult tr) {
		 // Reporter.reportLog("Test case ended");
		 System.out.println("Test Case Ended " + tr.getName());
		 report.getResult(tr);
		 super.onTestFailure(tr);
	     tr.getThrowable().printStackTrace();
	     Reporter.log(tr.getThrowable().toString(),true);
	 }
	
	 @Override
	 public void onTestSuccess(ITestResult tr) {
		
		 report.getResult(tr);
		 super.onTestSuccess(tr);

		 Reporter.log( "All Steps of Test are completed",true);
		 System.out.println("Test Case Ended " + tr.getName());
	 }	
	 
	 @Override()
	 public void onTestStart(ITestResult tr){
		  report.startTest(tr);
	  	  report.getResult(tr);
	  	  System.out.println("\nTest Case started " + tr.getName());
		 super.onTestStart(tr);

	 }
	 
	 @Override
	    public void onFinish(ITestContext context) {
		 report.saveReport();
	 }
	 

}
