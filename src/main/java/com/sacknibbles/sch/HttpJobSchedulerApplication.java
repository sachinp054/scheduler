package com.sacknibbles.sch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.sacknibbles.sch")
public class HttpJobSchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(HttpJobSchedulerApplication.class, args);
	}
}
