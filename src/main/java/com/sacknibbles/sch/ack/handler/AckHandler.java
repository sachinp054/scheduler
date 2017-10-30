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
 * @author Sachin
 *
 */
public class AckHandler {

	private static Logger logger = LoggerFactory.getLogger(AckHandler.class);
	private static final RestTemplate restTemplate = new RestTemplate();

	private AckHandler() {
	}

	public static void sendAcknowledgement(URLRecord urlRecord, SchedulerResponse schedulerResponse) {
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
			body = Utils.convertToAvroPayload(schedulerResponse);
			HttpEntity<String> request = new HttpEntity<>(body, headers);
			ResponseEntity<HttpStatus> response = restTemplate.exchange(uri, method, request, HttpStatus.class);
			logger.info("Received response[{}] for ack message [{}]", response.getBody(), urlRecord);
		}
	}

	public static void sendAcknowledgement(String email, SchedulerResponse schedulerResponse) {
		logger.warn("Could not send email!, currently this feature is unavailable");
	}
}
