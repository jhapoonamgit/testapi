package com.smarteinc.utility;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class csvSSLRead {
	private static Logger logger = Logger.getLogger(csvSSLRead.class);
	static InputStream input;

	public static void readRecordsFromTestCRMFile(Map<String, String> hm, String OutputFields) throws IOException {
		String[] headerlist = OutputFields.split(",");
		File[] files = new File(hm.get("inputpath")).listFiles();
		for (File file : files) {
			logger.info("Reading file :: " + file.getPath());
			List<JSONObject> outputlst = new ArrayList<JSONObject>();
			try {
				String filePath = file.getPath();
				if (filePath != null) {
					JSONArray jsonObject = new JSONArray();
					BufferedReader fileReader = new BufferedReader(
							new InputStreamReader(new FileInputStream(filePath), "UTF8"));
					CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
					List<String> headers = csvParser.getHeaderNames();

					for (CSVRecord csvRecord : csvParser) {
						JSONObject record = new JSONObject();
						Map<String, String> document = new HashMap<>();
						for (String header : headers) {
							if (header != null && !header.trim().isEmpty()) {
								record.put(header, csvRecord.get(header));
								document.put(header, csvRecord.get(header));
							}
						}
						jsonObject.add(record);
					}
					List<Future<JSONObject>> resultList = new ArrayList<Future<JSONObject>>();
					// ExecutorService executor = Executors.newFixedThreadPool(threads);
					ExecutorService executor = Executors.newFixedThreadPool(Integer.parseInt(hm.get("threads")));
					logger.info("Thread started :: " + file.getPath());
					for (int j = 0; j < jsonObject.size(); j++) {
						JSONObject vo = (JSONObject) jsonObject.get(j);
						callAPISSL worker = new callAPISSL(vo, OutputFields, hm.get("apiKey"), hm.get("url"),
								hm.get("client-id"), hm.get("input-value"), hm.get("ev-value"), hm.get("scope-value"),
								hm.get("output-value"));
						Future<JSONObject> submit = executor.submit(worker);
						resultList.add(submit);
					}

					for (Future<JSONObject> collatedResult : resultList) {
						JSONObject output = collatedResult.get();
						if (output != null) {
							outputlst.add(output);
						}
					}
					logger.info("Thread complete:: " + file.getPath());
					executor.shutdown();
					// exportFile(outputlst, file, outputpath, headerlist);
					exportFile(outputlst, file, hm.get("outputpath"), headerlist);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			logger.info("Reading file complete:: " + file.getPath());
		}

	}

	private static void exportFile(List<JSONObject> outputlst2, File file, String outputpath, String[] headerlist)
			throws IOException {
		String outputFile = outputpath + File.separator + file.getName();
		CSVPrinter csvFilePrinter = null;
		CSVFormat csvFileFormat = CSVFormat.EXCEL.withHeader(headerlist);
		FileWriter fileWriter = new FileWriter(outputFile);
		csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
		for (JSONObject rec : outputlst2) {
			if (rec != null) {
				List<Object> recs = new ArrayList<Object>();
				for (String header : headerlist) {
					Object val = rec.get(header);
					if (val == null) {
						val = "";
					}
					recs.add(val);
				}
				csvFilePrinter.printRecord(recs);
			}
		}

		fileWriter.flush();
		fileWriter.close();
		csvFilePrinter.close();

	}

}
