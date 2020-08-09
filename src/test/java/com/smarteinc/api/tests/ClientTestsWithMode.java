package com.smarteinc.api.tests;

import org.testng.annotations.Test;

import com.smarteinc.workflow.PEAPI_Mode;

public class ClientTestsWithMode {
	
	@Test
	public void verify_DGODClients()
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

}
