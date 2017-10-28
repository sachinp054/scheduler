/**
 * 
 */
package com.sacknibbles.sch.controller.avro.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author Sachin
 *
 */
public class HttpJobDateParser {

	private static SimpleDateFormat df = new SimpleDateFormat( "dd/MM/yyyy HH24:mm" );
	
	public static Date getDateTime(String dateTimeStr) throws ParseException{
		if(dateTimeStr.contains("T")){
			LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr);
			return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
		}else{
			return df.parse(dateTimeStr);
		}
	}

	
}
