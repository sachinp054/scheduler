/**
 * 
 */
package com.sacknibbles.sch.ack.handler;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.sacknibbles.sch.avro.model.SchedulerResponse;
import com.sacknibbles.sch.avro.model.URLRecord;
import com.sacknibbles.sch.controller.avro.util.Utils;

/**
 * This will handle the acknowledgement of job completion once it is completed.
 * Currently, we only send acknowledgement on callback url or on email
 * @author Sachin Pandey
 *
 */
public class AckHandler {

	private static Logger logger = LoggerFactory.getLogger(AckHandler.class);
	private static final RestTemplate restTemplate = new RestTemplate();

	private AckHandler() {
	}

	public static void sendAcknowledgement(URLRecord urlRecord, SchedulerResponse response) {
		if (Objects.nonNull(urlRecord)) {
			String uri = urlRecord.getUri();
			HttpMethod method = HttpMethod.valueOf(urlRecord.getMethod());
			HttpHeaders headers = new HttpHeaders();
			urlRecord.getHeaders().forEach((k, v) -> {
				headers.add(k, v);
			});
			if (urlRecord.getIsBasicAuthEnabled()) {
				String userName = urlRecord.getUserName();
				String password = urlRecord.getPassword();
				String authString = "Basic " + userName + ":" + password;
				headers.add("Authorization", authString);
			}
			String body;
			body = Utils.convertToAvroJSONString(response);
			HttpEntity<String> request = new HttpEntity<>(body, headers);
			ResponseEntity<HttpStatus> ackResponse = restTemplate.exchange(uri, method, request, HttpStatus.class);
			logger.info("Received response[{}] for ack message [{}]", ackResponse.getBody(), urlRecord);
		}
		else
			logger.debug(
					String.format("There is no url to send ackowledgement for jobGroup: %s, jobName: %s, jobId: %s",
							response.getJobGroupName(), response.getJobName(), response.getJobId()));
	}

	public static void sendAcknowledgement(String email, SchedulerResponse response) {
		logger.warn("Could not send email!, currently this feature is unavailable");
	}
}
