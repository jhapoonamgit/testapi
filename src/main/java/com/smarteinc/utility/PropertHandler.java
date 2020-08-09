package com.smarteinc.utility;

import java.io.InputStream;
import java.util.Properties;

public class PropertHandler {
	private Properties properties = new Properties();

	public void PropertyHandler() {

	}
	
	public void PropertyHandler(Properties prop) {
		this.properties = prop;
	}

	public void PropertyHandler(InputStream is) {
		try {
			this.properties.load(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected Properties getProperties() {
		return properties;
	}
	public void addProperties(InputStream ins) {
		try {
			Properties tempProperties = new Properties();
			tempProperties.load(ins);
			addProperties(tempProperties);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addProperties(Properties ins) {
		try {
			ins.forEach((key, value) -> {
				if (!properties.containsKey(String.valueOf(key))) {
					properties.put(key, value);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getConfigValue(String key) {
		
		for (Object k : properties.keySet()) {
			String propertyKey = String.valueOf(k).trim();
			if (propertyKey.equalsIgnoreCase(key)) {
				return properties.getProperty(propertyKey);
			}
		}
		return "";
	}


}
