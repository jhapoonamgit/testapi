package com.smarteinc.utility;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class callAPISSL implements Callable<JSONObject> {
	private static Logger logger = Logger.getLogger(callAPISSL.class);
	JSONObject jsonstring;
	private String mode;
	String result;
	String key;
	String jsonresult;
	String urlStr;
	String clientId;
	String inputValue;
	String evValue;
	String scopeValue;
	String outputValue;

	// public callAPISSL(JSONObject jsonstring, String mode, String key, String url)
	// {
	public callAPISSL(JSONObject jsonstring, String mode, String key, String url, String clientId, String inputValue,
			String evValue, String scopeValue, String outputValue) {
		super();
		this.jsonstring = jsonstring;
		this.key = key;
		this.mode = mode;
		this.urlStr = url;

		this.clientId = clientId;
		this.inputValue = inputValue;
		this.evValue = evValue;
		this.scopeValue = scopeValue;
		this.outputValue = outputValue;
	}

	public callAPISSL() {
		// TODO Auto-generated constructor stub
	}

	public JSONObject call() throws Exception {
		JSONObject jsonObject = null;
		try {
			ConnectHttps.execute();
//			String urlStr = "http://10.0.15.5:8080/api/v1/enrich";
			URL url = new URL(urlStr);
			 HttpsURLConnection httpCon = (HttpsURLConnection) url.openConnection();
			//HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();

			httpCon.setDoOutput(true);
			httpCon.setReadTimeout(600000);
			httpCon.setRequestMethod("POST");
			// httpCon.setRequestProperty("mode", mode);
			httpCon.setRequestProperty("output-value", outputValue);
			httpCon.setRequestProperty("client-id", clientId);
			httpCon.setRequestProperty("input-value", inputValue);
			httpCon.setRequestProperty("ev-value", evValue);
			httpCon.setRequestProperty("scope-value", scopeValue);

			httpCon.setRequestProperty("Authorization", "Bearer " + key);
			httpCon.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			OutputStream os = httpCon.getOutputStream();
			os.write(jsonstring.toJSONString().getBytes());
			os.flush(); // don't forget to close the OutputStream
			httpCon.connect();

			// read the inputstream and print it
			if (httpCon.getResponseCode() == 200) {
				BufferedInputStream bis = new BufferedInputStream(httpCon.getInputStream());
				ByteArrayOutputStream buf = new ByteArrayOutputStream();
				int result2 = bis.read();
				while (result2 != -1) {
					buf.write((byte) result2);
					result2 = bis.read();
				}
				result = buf.toString();
//			jsonObject = new JSONObject(result);
				JSONParser parser = new JSONParser();
				jsonObject = (JSONObject) parser.parse(result);
//			jsonObject = gson.fromJson(result, JSONObject.class);
				jsonresult = jsonObject.toJSONString();
				logger.info("Success ::");
			} else {
				logger.info("Error records :" + httpCon.getResponseCode() + " :: " + httpCon.getResponseMessage()
						+ " :: " + jsonstring.toJSONString());
			}
		} catch (Exception e) {
			logger.error(jsonstring.toJSONString(), e);
			e.printStackTrace();
		}
		return jsonObject;

	}

}
