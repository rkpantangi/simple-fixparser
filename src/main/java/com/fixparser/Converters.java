package com.fixparser;



import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class Converters {

	private static final Logger logger = Logger.getLogger(Converters.class);
	
	/**
	 * @param str
	 * @return
	 */
	public static Integer getInt(String str) {
		if (StringUtils.isBlank(str)) return null;
		Integer data = null;
		try {
			data = Integer.parseInt(str);
		} catch (Exception ex) {
			logger.error("Error parsing integer - " + str);
		}
		return data;
	}
}
