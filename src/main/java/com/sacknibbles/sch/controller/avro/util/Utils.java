/**
 * 
 */
package com.sacknibbles.sch.controller.avro.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sacknibbles.sch.avro.model.SchedulerResponse;

/**
 * @author Sachin
 *
 */

public final class Utils {

	private static Logger logger = LoggerFactory.getLogger(Utils.class);
	private final static Map<String, JobKey> jobKeyMap = new ConcurrentHashMap<>();

	private static final SpecificDatumWriter<SchedulerResponse> writer = new SpecificDatumWriter<>(
			SchedulerResponse.class);

	public static String getId() {
		return UUID.randomUUID().toString();
	}

	public static <T extends SpecificRecordBase> String convertToAvroJSONString(T t) {
		String payload;
		
		try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			JsonEncoder encoder = EncoderFactory.get().jsonEncoder(t.getSchema(), out);
			writer.write((SchedulerResponse) t, encoder);
			encoder.flush();
			payload = out.toString();
			out.flush();
		} catch (IOException e) {
			logger.error("Exception occured!", e);
			// If AVRO serialization fails then use default toString
			payload = t.toString();
		}

		return payload;
	}

	public static JobKey getJobKey(String jobId, String jobGroupName, String jobName) {
		String k1 = jobId + "#" + jobName + "#" + jobName;
		if (Objects.isNull(jobKeyMap.get(k1))) {
			jobKeyMap.put(k1, new JobKey(jobId + "#" + jobName, jobName));
		}
		return jobKeyMap.get(k1);
	}

}
