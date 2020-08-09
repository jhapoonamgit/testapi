package com.smarteinc.workflow;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.testng.Assert;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jayway.restassured.response.Response;
import com.smarteinc.objects.PEApiInputObjects;
import com.smarteinc.peapi.PEAPI_Library;
import com.smarteinc.utility.APIUtility;
import com.smarteinc.utility.ExcelUtility;

public class PEAPI_Mode
{
	PEApiInputObjects obj;
	
	public void verifyPEAPIMode( String url, String file, String mode, String data) throws Exception {
		obj = new PEApiInputObjects();
		String strFailureReason = "";
		
		XSSFSheet modeSheet = null;
		XSSFSheet dataSheet = null;		
		
		List<String> al = new ArrayList<String>();
		modeSheet = ExcelUtility.openSpreadSheet(file, mode);
		
		dataSheet = ExcelUtility.openSpreadSheet(file, data);
		boolean flag = true;
		boolean modeStatus = true;
		List<String> modeStatusList = new ArrayList<String>();
		List<String> flagStatusList = new ArrayList<String>();
		List<String> reasonList = new ArrayList<String>();
		List<String> dataList = new ArrayList<String>();
		
		Map<String, String> hm = new HashMap<String, String>();
		PEAPI_Library peLib = new PEAPI_Library();
		
		int lastModeRow = modeSheet.getLastRowNum();
		int lastDataRow = dataSheet.getLastRowNum();		
	try
	{
		for (int row = 1; row <= lastDataRow; row++) {	
			flag = true;
			// Input fields
			
            obj.setRecordId(ExcelUtility.getCellData(dataSheet, row, "recordId"));
            obj.setContactGuid(ExcelUtility.getCellData(dataSheet, row, "contactGuid"));
            obj.setCompanyGuid(ExcelUtility.getCellData(dataSheet, row, "companyGuid"));
            obj.setContactFirstName(ExcelUtility.getCellData(dataSheet,row, "contactFirstName"));
            obj.setContactMiddleName(ExcelUtility.getCellData(dataSheet, row, "contactMiddleName"));
            obj.setContactLastName(ExcelUtility.getCellData(dataSheet, row, "contactLastName"));
            obj.setContactFullName(ExcelUtility.getCellData(dataSheet, row, "contactFullName"));
            obj.setContactEmail(ExcelUtility.getCellData(dataSheet, row, "contactEmail"));
            obj.setContactJobTitle(ExcelUtility.getCellData(dataSheet, row, "contactJobTitle"));
            obj.setCompanyName(ExcelUtility.getCellData(dataSheet, row, "companyName"));
            obj.setContactState(ExcelUtility.getCellData(dataSheet, row, "contactState"));
            obj.setContactZipCode(ExcelUtility.getCellData(dataSheet, row, "contactZipcode"));
            obj.setContactCountry(ExcelUtility.getCellData(dataSheet, row, "contactCountry"));
            obj.setContactPhone(ExcelUtility.getCellData(dataSheet, row, "Phone"));
            obj.setCompanyWebAddress(ExcelUtility.getCellData(dataSheet, row, "companyWebAddress"));
            obj.setPiid(ExcelUtility.getCellData(dataSheet, row, "piid"));
            obj.setAccountId(ExcelUtility.getCellData(dataSheet, row, "accountId"));
            obj.setObjectType(ExcelUtility.getCellData(dataSheet, row, "objectType"));
            obj.setContactUrl(ExcelUtility.getCellData(dataSheet, row, "contactUrl"));
            //obj.setContactGuid(ExcelUtility.getCellData(dataSheet, row, prop.getProperty("PersonGuid")));


				
			String strBody = APIUtility.getJsonBody(obj);			
			strFailureReason = "";			
			for (int rowMode = 1; rowMode <= lastModeRow; rowMode++) {				
				hm.put("client-id", ExcelUtility.getCellData(modeSheet, rowMode, "client-id"));
				hm.put("input-value", ExcelUtility.getCellData(modeSheet, rowMode, "input-value"));
				hm.put("ev-value", ExcelUtility.getCellData(modeSheet, rowMode, "ev-value"));
				hm.put("scope-value", ExcelUtility.getCellData(modeSheet, rowMode, "scope-value"));
				hm.put("output-value", ExcelUtility.getCellData(modeSheet, rowMode, "output-value"));
				String outputFlagsToMatch = ExcelUtility.getCellData(modeSheet, rowMode, "matchOutputFlags");
				
				String  lstOutputFlag[] = outputFlagsToMatch.split(",");
				al = Arrays.asList(lstOutputFlag);						
				Response res = peLib.getResponseForPEApi(strBody, url, hm );
				
								
				JsonParser parser = new JsonParser();
				JsonObject jo = parser.parse(res.asString()).getAsJsonObject();								
				List<String> keys = jo.entrySet()
						.stream()
						.map(i -> i.getKey())
						.collect(Collectors.toCollection(ArrayList::new));

				//keys.forEach(System.out::println);
						
//				if(!keys.containsAll(al))
//					flag = false;
				
				for(String item : al)
					{
						//System.out.println("Item " + item + keys.contains(item));
						if(keys.contains(item.trim()))
						{
							flag = true;
							flagStatusList.add("True");
						}
					else
						{
							flag = false;							
							flagStatusList.add("False");
							strFailureReason = strFailureReason + " Row Id : " + rowMode + ","+ item ;
							reasonList.add(strFailureReason);
						}						
					}
				if(flagStatusList.contains("False"))
					modeStatusList.add("False");
				else 
					modeStatusList.add("True");
				
				if(rowMode == lastModeRow)
				{
					if(modeStatusList.contains("False"))
					{
						peLib.updateExcelCell("Fail", dataSheet, row,"Status");
						dataList.add("Fail");
						peLib.updateExcelCell(strFailureReason, dataSheet, row,"Reason");
					}
					else
					{
						dataList.add("Pass");
						peLib.updateExcelCell("Pass", dataSheet, row, "Status");
					}
				}					
			}
		}		

		String fileName = file.substring(file.lastIndexOf('/') + 1);
		String newFile = file.substring(0, file.lastIndexOf('/')) + "/New" + fileName;
		try {

			ExcelUtility.saveChangesToAnother(newFile, dataSheet.getWorkbook());
			if(dataList.contains("Fail"))
				Assert.fail("Rule test has failed");
			//logger.info("Saved");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	catch(Exception ex){
		
		System.out.println(ex.getMessage());
		String fileName = file.substring(file.lastIndexOf('/') + 1);
		String newFile = file.substring(0, file.lastIndexOf('/')) + "/New" + fileName;
		ExcelUtility.saveChangesToAnother(newFile, dataSheet.getWorkbook());
		Assert.fail("PE API script has failed");
		
	   }
		
	}

}
