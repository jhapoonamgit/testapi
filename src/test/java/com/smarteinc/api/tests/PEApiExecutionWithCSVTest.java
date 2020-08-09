package com.smarteinc.api.tests;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.smarteinc.utility.*;

public class PEApiExecutionWithCSVTest {
	

	InputStream input;
	String formattedDate;
			
	Properties config = new Properties();
	
	
	@BeforeTest
	public void beforeTest() throws IOException, IOException
	{	
		String propFileName = "/Config/header.properties";
			
		InputStream inputStream = PEApiExecutionWithCSVTest.class.getResourceAsStream(propFileName);
				
		if (inputStream != null) {
			try {
				config.load(inputStream);
	
			} catch (IOException e) {

			}
		}
	}
	
    @Test
	public void readCSV() throws Exception
	{	
		Calendar c = Calendar.getInstance();
	    SimpleDateFormat dateformat = new SimpleDateFormat("hh:mm:ss"); 
	    String time1 = dateformat.format(c.getTime()); 
	    System.out.print("Time1..." + time1 + "--Time1");
	  		
	    Map<String,String> hm = new HashMap<String,String>();
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		
		Date date1 = format.parse(time1);

		String outputfields = null;
		hm.put("threads", config.getProperty("no_of_threads"));
		hm.put("inputpath", config.getProperty("inputFolderpath"));
		hm.put("outputpath", config.getProperty("outputFolderpath"));
		//hm.put("apiKey", config.getProperty("apiKey"));
		hm.put("client-id", config.getProperty("client-id"));
		hm.put("input-value", config.getProperty("input-value"));
		hm.put("ev-value", config.getProperty("ev-value"));
		hm.put("scope-value", config.getProperty("scope-value"));
		hm.put("output-value", config.getProperty("output-value"));
		hm.put("url", config.getProperty("url"));
		
       		
		csvSSLRead csvRead = new csvSSLRead();
		 if (hm.get("output-value").toString().equalsIgnoreCase("ALL")||(hm.get("output-value").toString().equalsIgnoreCase(""))) {
				outputfields = config.getProperty("internalOutputFields");
			} else {
				outputfields = config.getProperty("externalOutputFields");
			}
		
		csvSSLRead.readRecordsFromTestCRMFile(hm,outputfields);

		Calendar c1 = Calendar.getInstance();
		String time2 = dateformat.format(c1.getTime()); 
		System.out.print("Time2 ---" + time2);
		
		Date date2 = format.parse(time2);
		
		long diff = date2.getTime() - date1.getTime();

		long diffSeconds = diff / 1000 % 60;
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000) % 24;
		long diffDays = diff / (24 * 60 * 60 * 1000);

		System.out.print(diffDays + " days, ");
		System.out.print(diffHours + " hours, ");
		System.out.print(diffMinutes + " minutes, ");
		System.out.print(diffSeconds + " seconds.");
	
	}


}
