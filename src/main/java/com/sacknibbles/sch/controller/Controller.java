/**
 * 
 */
package com.sacknibbles.sch.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sacknibbles.sch.controller.avro.util.Utils;
import com.sacknibbles.sch.scheduler.HttpJobScheduler;

/**
 * @author Sachin
 *
 */
@RestController
public class Controller {

	@Autowired
	private HttpJobScheduler httpJobScheduler;
	
	@PostMapping(value="/job",consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> createJob(@RequestBody String jobRequestPayload) throws IOException{	
		return Utils.generateResponseEntity(httpJobScheduler.scheduleJob(jobRequestPayload));
	}
}
