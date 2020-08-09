package com.smarteinc.api.tests;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jayway.restassured.response.Response;
import com.smarteinc.objects.PEApiInputObjects;
import com.smarteinc.peapi.PEAPI_Library;
import com.smarteinc.utility.APIUtility;
import com.smarteinc.utility.ExcelUtility;
import com.smarteinc.workflow.PEAPI_Mode;

public class PEAPI_Modes extends APIBaseTest {
	
	Properties prop = new Properties();
	Properties prop2 = new Properties();
	
	PEApiInputObjects obj;
	
	private static Logger logger = initializeLogger(new MatchBackTest());

	public static Logger initializeLogger(Object classObject) {
		System.setProperty("logDirectory", "..\\com.smarteinc.automation\\logs");
		logger = LogManager.getLogger(classObject.getClass().getSimpleName());

		return logger;
	}

	@BeforeTest
	public void beforeTest() throws IOException, IOException {
	    obj = new PEApiInputObjects();
		String propFileName = "/Config/Tranalyzer.properties";
		String propFileName1 = "/Config/PEApiOutput.properties";

		InputStream inputStream = MatchBackTest.class.getResourceAsStream(propFileName);
		InputStream inputStream1 = MatchBackTest.class.getResourceAsStream(propFileName1);

		if (inputStream != null) {
			try {
				prop.load(inputStream);
				prop2.load(inputStream1);
			} catch (IOException e) {

			}
		}		
	}
	
		@Test
		public void verify_PEMode()
		{		
			String url = "http://qcpeapi.smarteinc.com/api/v1/enrich";
			
			String file = PEAPI_Mode.class.getResource("/TestData/Mode/PEAPI_client.xlsx").getPath();
			
			PEAPI_Mode pemode = new PEAPI_Mode();		
			try {
				pemode.verifyPEAPIMode(url, file, "PE-Mode", "PE-Data");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Test
		public void verify_DGMode()
		{		
			String url = "http://qcpeapi.smarteinc.com/api/v1/enrich";
			
			String file = PEAPI_Mode.class.getResource("/TestData/Mode/PEAPI_client.xlsx").getPath();
			
			PEAPI_Mode pemode = new PEAPI_Mode();		
			try {
				pemode.verifyPEAPIMode(url, file, "DG", "DG-Data");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Test
		public void verify_DGODMode()
		{		
			String url = "http://qcpeapi.smarteinc.com/api/v1/enrich";
			
			String file = PEAPI_Mode.class.getResource("/TestData/Mode/PEAPI_client.xlsx").getPath();
			
			PEAPI_Mode pemode = new PEAPI_Mode();		
			try {
				pemode.verifyPEAPIMode(url, file, "DGOD", "DGOD-Data");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Test
		public void verify_CEMode()
		{		
			String url = "http://qcpeapi.smarteinc.com/api/v1/enrich";
			
			String file = PEAPI_Mode.class.getResource("/TestData/Mode/PEAPI_client.xlsx").getPath();
			
			PEAPI_Mode pemode = new PEAPI_Mode();		
			try {
				pemode.verifyPEAPIMode(url, file, "CE", "CE-Data");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
