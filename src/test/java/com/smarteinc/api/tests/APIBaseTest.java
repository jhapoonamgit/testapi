package com.smarteinc.api.tests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.OptionalDouble;
import java.util.Properties;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import com.smarteinc.peapi.PEAPI_ResponseFactory;
import static com.smarteinc.peapi.PEAPI_ResponseFactory.*;
import com.smarteinc.utility.APIUtility;

import com.smarteinc.core.ResponseService;

@Listeners({ APIBaseTest.class, com.smarteinc.utility.TestNGListener.class })
public class APIBaseTest  implements ISuiteListener {
	public static PEAPI_ResponseFactory apiResponseFactory;
	Properties prop = new Properties();
	
	@BeforeSuite(alwaysRun = true)
	public void intializeManager()
	{
		apiResponseFactory = new PEAPI_ResponseFactory();
	}
	
	
	@AfterSuite(alwaysRun = true)
	public void teardownManagers() {

		Map<String, Double> avgRspTime = new HashMap<String, Double>();
		for (String apis : apiRespTimes.keySet()) {

			List<Long> l = apiRespTimes.get(apis);
			OptionalDouble average = l.stream().mapToLong(i -> i).average();
			avgRspTime.put(apis, average.getAsDouble());

		}
		BufferedWriter bufferedWriter = null;
		try {
		
			String path = APIBaseTest.class.getResource("/ResponseTimeOfApi/RespTime.txt").getPath().replace("%20", " ").replaceFirst("/", "");

			File myFile = new File(path);
			// check if file exist, otherwise create the file before writing
			if (!myFile.exists()) {
				myFile.createNewFile();
			}
			Writer writer = new FileWriter(myFile);
			bufferedWriter = new BufferedWriter(writer);
			//bufferedWriter.write("Environment : " + getValue("environment") + "\t"
					//+ getCurrentDate("dd-MMM-yyyy HH:mm:ss-z") + "\r\n");
			bufferedWriter.write("Response time of APIs: \r\n");
			for (Entry<String, Double> rspTime : avgRspTime.entrySet()) {
				bufferedWriter.write(rspTime.getKey() + " : " + rspTime.getValue() + " ms \r\n");
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//TestReporter.endReporting();
	
			try {
				if (bufferedWriter != null)
					bufferedWriter.close();
			} catch (Exception ex) {

			}
		}
		

	}

	@Override
	public void onStart(ISuite suite) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFinish(ISuite suite) {
		// TODO Auto-generated method stub
		
	}

}
