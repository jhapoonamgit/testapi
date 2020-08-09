package com.smarteinc.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jayway.restassured.response.Response;

public class APIUtility {
	
	public static String getJsonBody(String... str) {
		Map<String, String> m = new HashMap<String, String>();
		for (int i = 0; i < str.length; i += 2) {
			m.put(str[i], str[i + 1]);
		}
		String jsonString = new GsonBuilder().disableHtmlEscaping().create().toJson(m);
		return jsonString;

	}
	
	public static String getJsonBody(Object obj) {
		GsonBuilder builder = new GsonBuilder(); 
	    builder.setPrettyPrinting(); 
	      
	    Gson gson = builder.create(); 
		String str = gson.toJson(obj);
		return str;
	}
	
	
	public static String getMemberValueAsString(Response res, String memberName) {
		String memberValue = res.jsonPath().getString(memberName);
		if (memberValue != null) {
			memberValue = memberValue.replaceAll("\"", "");
		}
		return memberValue;
	}
	
	/***
	 * Will return Current Date in the passed format
	 * 
	 * @param date
	 *            - Pass the format of date
	 * @return Returns current date See :{@link java.text.DateFormat#format(Date)}
	 */
	public static String getCurrentDate(String date) {

		DateFormat df = new SimpleDateFormat(date);
		Date d = new Date();
		return df.format(d);
	}
	
	
	public static JsonElement getMemberValue(Response res, String memberXPath) {
		JsonObject jObj = getJsonObject(res);
		return getMemberValue(jObj, memberXPath);
	}
	
	public static JsonElement getObjMemberValue(JsonObject obj, String memberXPath) {
				return getMemberValue(obj, memberXPath);
	}

	public static JsonObject getJsonObject(Response res) {
		JsonElement jElement = new JsonParser().parse(res.asString());
		JsonObject jObject = jElement.getAsJsonObject();
		return jObject;
	}
		
	public static String getMemberValueAsString(JsonObject jObject, String memberName) {
		String memberValue = jObject.get(memberName).getAsString();
		if (memberValue != null)  	{
			memberValue = memberValue.replaceAll("\"", "");
		}
		return memberValue;
	}
	
	public static JsonElement getMemberValue(JsonObject jObj, String memberXPath) {

		if (memberXPath.contains("/")) {
			String[] members = memberXPath.split("/");
			int index = getJsonArrayIndex(members[0]);
			String memberName = members[0].split("\\[")[0];
			if (jObj.getAsJsonObject().get(memberName).isJsonArray()) {
				JsonArray jArray = jObj.getAsJsonObject().get(memberName).getAsJsonArray();
				JsonObject jObjTemp = jArray.get(index).getAsJsonObject();
				return getMemberValue(jObjTemp, memberXPath.replaceFirst("/*[^/]*/", ""));
			} else {
				if (jObj.get(members[0]).isJsonObject())
					return getMemberValue(jObj.get(members[0]).getAsJsonObject(),
							memberXPath.replaceFirst("/*[^/]*/", ""));
			}
		} else {
			if (memberXPath.contains("[")) {
				int index = getJsonArrayIndex(memberXPath);
				memberXPath = memberXPath.split("\\[")[0];
				return jObj.getAsJsonArray(memberXPath).get(index);
			}
			return jObj.get(memberXPath);
		}
		return null;
	}
	
	public static int getJsonArrayIndex(String value) {
		Pattern pattern = Pattern.compile("\\[(.*?)\\]+");
		Matcher matcher = pattern.matcher(value);
		if (matcher.find())
			return Integer.parseInt(matcher.group(1));
		else
			return -1;
	}

}
