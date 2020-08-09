package com.smarteinc.api.tests;

import static com.jayway.restassured.RestAssured.given;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.smarteinc.objects.PEApiInputObjects;
import com.smarteinc.objects.PIPLInputObjects;
import com.smarteinc.peapi.PEAPI_Library;
import static com.smarteinc.utility.APIUtility.*;

import com.smarteinc.utility.ExcelUtility;

public class DirectDialTest {

	Properties prop = new Properties();
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

		InputStream inputStream = MatchBackTest.class.getResourceAsStream(propFileName);

		if (inputStream != null) {
			try {
				prop.load(inputStream);
			} catch (IOException e) {

			}
		}

		// ConnectHttps.execute();
	}

	@Test
	public void DirectdialTest() throws Exception {
		PEAPI_Library peLib = new PEAPI_Library();
		Response res = null;
		XSSFSheet sheet = null;
		String file = MatchBackTest.class.getResource("/TestData/DirectDial/directdial.xlsx").getPath();

		try {
			String token = "Bearer 67e92e0c-3f3f-359e-8a66-9c186d41f720";
			String URL = "https://34.68.215.208:8243/dial/1/fetch";
			sheet = ExcelUtility.openSpreadSheet(file, "direct");
			int lastRow = sheet.getLastRowNum();

			Map<String, String> hm = new HashMap<String, String>();
			PEAPI_Library tran = new PEAPI_Library();

			PIPLInputObjects obj = new PIPLInputObjects();
			for (int row = 1; row <= lastRow; row++) {
				// Input fields
				System.out.println("row" + row);
				String pcGuid = ExcelUtility.getCellData(sheet, row, "pc_guid");
				String rcGuid = ExcelUtility.getCellData(sheet, row, "rc_guid");
				String linkedInURL = ExcelUtility.getCellData(sheet, row, "linkedin_url");
				String action = ExcelUtility.getCellData(sheet, row, "action");
				String key_name = ExcelUtility.getCellData(sheet, row, "key_name");

				String strBody = getJsonBody("pc_guid", pcGuid, "rc_guid", rcGuid, "linkedin_url", linkedInURL,
						"action", action, "key_name", key_name);

				RestAssured.useRelaxedHTTPSValidation();
				res = peLib.getResponseFordirectDial(strBody, URL, token);

				// System.out.println(res.asString());
				List<String> lstOutput = Arrays.asList("errorMessage", "direct_dial_flag", "direct_dial_1",
						"direct_dial_2");

				tran.updateExcelCell(lstOutput, sheet, row, res);

			}

			String fileName = file.substring(file.lastIndexOf('/') + 1);
			String newFile = file.substring(0, file.lastIndexOf('/')) + "/New" + fileName;

			try {

				ExcelUtility.saveChangesToAnother(newFile, sheet.getWorkbook());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception ex) {
			//ex.printStackTrace();
			System.out.println(ex.getMessage());
			String fileName = file.substring(file.lastIndexOf('/') + 1);
			String newFile = file.substring(0, file.lastIndexOf('/')) + "/New" + fileName;
			ExcelUtility.saveChangesToAnother(newFile, sheet.getWorkbook());

		}
	}

}
