/**
 * 
 */
package com.sacknibbles.sch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sacknibbles.sch.scheduler.JobRequestHandler;

/**
 * @author Sachin
 *
 */
@RestController
public class JobController {

	@Autowired
	private JobRequestHandler requestHandler;
	
	@PostMapping(value="/job",consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> createJob(@RequestBody String jobRequestPayload){	
		return requestHandler.scheduleJob(jobRequestPayload);
	}
}
