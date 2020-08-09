package com.smarteinc.peapi;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.google.gson.JsonObject;
import com.jayway.restassured.response.Response;
import com.opencsv.CSVWriter;
import com.smarteinc.objects.PEApiInputObjects;
import com.smarteinc.utility.APIUtility;
import com.smarteinc.utility.ExcelUtility;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVFormat;

public class PEAPI_Library extends PEAPI_ResponseFactory {

	public Response BuildPEInputJson(Map<String, String> hmCountry, String URL, String mode) {
		String json = APIUtility.getJsonBody("companyId", hmCountry.get("Id"), "companyGuid", hmCountry.get("CompGuid"),
				"contactFirstName", hmCountry.get("FirstName"), "contactMiddleName", hmCountry.get("MiddleName"),
				"contactLastName", hmCountry.get("LastName"), "contactFullName", hmCountry.get("FullName"),
				"contactEmail", hmCountry.get("Email"), "contactJobTitle", hmCountry.get("JobTitle"), "companyName",
				hmCountry.get("Company"), "contactState", hmCountry.get("State"), "contactZipcode",
				hmCountry.get("PostalCode"), "contactCountry", hmCountry.get("Country"), "phone",
				hmCountry.get("Phone"), "companyWebAddress", hmCountry.get("Website"), "contactGuid",
				hmCountry.get("PersonGuid"));

		// System.out.println(json);
		Response res = peResponse(URL, json, mode);

		return res;
	}

//	public Response getResponseForPEApi(String strJson, String URL, String mode ) {
//		
//		//System.out.println(json);
//		Response res = peResponse(URL, strJson, mode);
//		return res;
//	}

	public Response getResponseForPEApi(String strJson, String URL, Map<String, String> modeParam) {

		// System.out.println(json);
		Response res = peModeResponse(URL, strJson, modeParam);
		return res;
	}

	public Triple<String, String, String> assertOutputFlags(List<String> lstExpectedOutput, List<String> actualOutput,
			List<String> columnToWrite) {
		String flag = "True";
		for (int i = 0; i < lstExpectedOutput.size(); i++) {
			if (!lstExpectedOutput.get(i).equals(actualOutput.get(i))) {
				flag = "False";
				return Triple.of(flag, columnToWrite.get(i), actualOutput.get(i));
			}
		}
		return Triple.of(flag, "", "");
	}

	public void updateExcelCell(String flagMode, XSSFSheet sheet, int row, String colName) {
		ExcelUtility.updateCellData(sheet, row, colName, flagMode);

	}

	public void updateExcelCell(List<String> cellValue, XSSFSheet sheet, int row) {
		int col = 6;
		for (String str : cellValue) {
			// System.out.println(str + " "+ APIUtility.getMemberValueAsString(res, str));

			ExcelUtility.updateCellData(sheet, row, col, str);

			// col++ ;
		}
	}

	public void updateExcelCell(List<String> cellValue, XSSFSheet sheet, int row, Response res) {
		 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		for (String str : cellValue) {
			JsonObject dataObj = APIUtility.getMemberValue(res, "data").getAsJsonObject();
			
			ExcelUtility.updateCellData(sheet,row, str, APIUtility.getMemberValue(dataObj, str.trim()).toString().replaceAll("^\"+|\"+$", ""));
				
			ExcelUtility.updateCellData(sheet,row, "request_sent_time", startTime.format(formatter));
			
			ExcelUtility.updateCellData(sheet,row, "request_response_time", endTime.format(formatter));
			
			//long minutes = (respTime / 1000)  / 60;
			int seconds = (int)((respTime / 1000) % 60);
			
			//int seconds = (int)((respTime / 1000));
		     int millisec = (int)respTime % 1000 ;
						
			//ExcelUtility.updateCellData(sheet,row, "elapsedTime", "Total Time : "+ respTime + " Seconds : " + String.valueOf(seconds) + " MilliSec :" + String.valueOf(millisec));
			ExcelUtility.updateCellData(sheet,row, "elapsedTime", String.valueOf(seconds) + " " + String.valueOf(millisec));
			//ExcelUtility.updateCellData(sheet,row, "elapsedTime", String.valueOf(seconds));

		}
	}

//	public void updateExcelCell(List<String> cellValue, XSSFSheet sheet, int row, String res)
//	{
//		for(String str : cellValue)
//		{
//			//System.out.println(str + " "+ APIUtility.getMemberValueAsString(res, str));
//		
//			ExcelUtility.updateCellData(sheet, row, str , res);
//		
//	
//		}
//	}
//		
	public Response getResponseFordirectDial(String strBody, String URL, String wso2Code) {

		// System.out.println(json);
		Response res = directDialResponse(URL, strBody, wso2Code);
		return res;
	}

}
