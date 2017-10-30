/**
 * 
 */
package com.sacknibbles.sch.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sachin
 *
 */
@RestController
public class TestController {

	@PostMapping(value="/target",consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HttpStatus> target(@RequestBody String jobRequestPayload) {	
		System.out.println(jobRequestPayload);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@PostMapping(value="/test",consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HttpStatus> test(@RequestBody String jobRequestPayload) {	
		System.out.println(jobRequestPayload);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
