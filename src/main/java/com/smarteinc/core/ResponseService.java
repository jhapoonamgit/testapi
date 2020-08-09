package com.smarteinc.core;

import static com.jayway.restassured.RestAssured.given;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

public class ResponseService {
	public String baseURL;
	public static Map<String, List<Long>> apiRespTimes = new HashMap<String, List<Long>>();
	public static long respTime;
	//public static Date startTime;
	//public static long endTime;
	public static LocalDateTime startTime;
	public static LocalDateTime endTime;
	
	public enum RequestMethodType {
		POST, GET, DELETE, PUT
	}

	/**
	 * Construct the Response service with the base api url of given app name
	 * configured in the testmode property.<br />
	 * Possible app-name : webapp, wms
	 * 
	 * @see {@link APIUtility#getBaseURL(String)}
	 * @param app
	 */
	public ResponseService(String app) {
		baseURL = "";
	}

	/**
	 * Calls to the requested url with the request specifications using the request
	 * method type
	 * 
	 * @param url        : A fully qualified url where the request specification to
	 *                   be pushed
	 * @param request    : A specification of the request generally contains the
	 *                   json string, content type, headers and query parameters
	 * @param methodType : A method type used to the push the request specification
	 * @return {@link Response}
	 * @see {@link RequestSpecification}
	 * @see {@link RequestMethodType}
	 */
	public Response getResponse(String url, RequestSpecification request, RequestMethodType methodType) {
		switch (methodType) {
		case POST:
			return request.post(url);
		case GET:
			return request.get(url);
		case DELETE:
			return request.delete(url);
		case PUT:
			return request.put(url);
		default:
			return null;
		}
	}

	/**
	 * Construct the request specifications using the given properties and performs
	 * the call to the
	 * {@link #getResponse(String, RequestSpecification, RequestMethodType)}
	 * 
	 * @param url         : URL of the api excluding the base URL
	 * @param jsonBody    : look at {@link RequestSpecification#body(String)}
	 * @param queryParam  : look at
	 *                    {@link RequestSpecification#queryParameters(Map)}
	 * @param contentType : look at {@link RequestSpecification#contentType(String)}
	 * @param headers     : look at {@link RequestSpecification#headers(Map)}
	 * @param methodType  : look at {@link RequestMethodType}
	 * @param toLog       : Default is true and will log the response but if dont
	 *                    want to log a response then pass false
	 * @return {@link Response}
	 */
	public Response getResponse(String url, String jsonBody, Map<String, ? extends Object> queryParam,
			String contentType, Map<String, ? extends Object> headers, RequestMethodType methodType, boolean toLog) {
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		    		
	    Map<String,String> hm = new HashMap<String,String>();
		
		String requestURL = this.baseURL + url;
		RequestSpecification request = given();

		if (jsonBody != null) {
			if (!jsonBody.equals(""))
				request = request.body(jsonBody);
		}

		if (queryParam != null) {
			if (!queryParam.isEmpty()) {
				request = request.queryParams(queryParam);
			}
		}

		request = request.when();

		if (contentType != null) {
			if (!contentType.equals(""))
				request = request.contentType(contentType);
		}

		if (headers != null) {
			if (!headers.isEmpty())
				request = request.headers(headers);
		}

		startTime = LocalDateTime.now();
		//System.out.print("Time1..." + startTime + "--Time1");		
		Response res = getResponse(requestURL, request, methodType);
		
		endTime = LocalDateTime.now();

		//System.out.print("Time2 ---" + endTime);
	
		respTime = res.time();
		
		if (toLog == true) {
			StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

			System.out.println("Response of " + stackTraceElements[3].getMethodName() + " :");
			//System.out.println(res.asString());

		}
//		if (apiRespTimes.containsKey(url)) {
//			apiRespTimes.get(url).add(res.time());
//			respTime = res.time();
//		} else {
//			apiRespTimes.put(url, new ArrayList<Long>());
//			apiRespTimes.get(url).add(res.time());
//			respTime = res.time();
//		}

		return res;
	}

	/**
	 * Construct the request specifications using the given properties and performs
	 * the call to the
	 * {@link #getResponse(String, RequestSpecification, RequestMethodType)}
	 * 
	 * @param url         : URL of the api excluding the base URL
	 * @param jsonBody    : look at {@link RequestSpecification#body(String)}
	 * @param queryParam  : look at
	 *                    {@link RequestSpecification#queryParameters(Map)}
	 * @param contentType : look at {@link RequestSpecification#contentType(String)}
	 * @param headers     : look at {@link RequestSpecification#headers(Map)}
	 * @param methodType  : look at {@link RequestMethodType}
	 * @return {@link Response}
	 */
	public Response getResponse(String url, String jsonBody, Map<String, ? extends Object> queryParam,
			String contentType, Map<String, ? extends Object> headers, RequestMethodType methodType) {
		return getResponse(url, jsonBody, queryParam, contentType, headers, methodType, true);

	}

}
