/**
 * 
 */
package com.sacknibbles.sch.factory;

import java.util.EnumMap;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sacknibbles.sch.avro.model.SchedulerType;

/**
 * @author Sachin
 *
 */
@Component
public class HttpJobSchedulerFactory {

	private Logger logger = LoggerFactory.getLogger(HttpJobSchedulerFactory.class);
	EnumMap<SchedulerType, Scheduler> schedulerMap = new EnumMap<>(SchedulerType.class);
	EnumMap<SchedulerType, Exception> exceptionMap = new EnumMap<>(SchedulerType.class);
	private final String ENV = System.getProperty("spring.profiles.active").toLowerCase();

	@PostConstruct
	void init() {
		schedulerMap.put(SchedulerType.INMEMORY_BASED, initialiseInmemoryScheduler());
		schedulerMap.put(SchedulerType.JDBC_BASED, initialiseJdbcBasedScheduler());
	}

	private Scheduler initialiseJdbcBasedScheduler() {
		Scheduler schd = null;
		
		try {
			SchedulerFactory schdFact = new StdSchedulerFactory("/quartz/" + ENV + "/quartz.properties");
			schd = schdFact.getScheduler();
			schd.start();
		} catch (SchedulerException e) {
			exceptionMap.put(SchedulerType.JDBC_BASED, e);
			logger.error("Exception occured while initializing JDBC based scheduler. Requests with scheduler type as 'JDBC_BASED' will not be serveed",e);		
		}
		return schd;
	}

	private Scheduler initialiseInmemoryScheduler() {
		Scheduler sched = null;
		try {
			sched = StdSchedulerFactory.getDefaultScheduler();
			sched.start();
		} catch (SchedulerException e) {
			exceptionMap.put(SchedulerType.INMEMORY_BASED, e);
			logger.error("Exception occured while initializing INMEMORY based scheduler. Requests with scheduler type as 'INMEMORY_BASED' will not be serveed",e);
		}
		return sched;
	}

	public Optional<Scheduler> getScheduler(SchedulerType schedulerType) {
		return Optional.ofNullable(schedulerMap.get(schedulerType));
	}
	
	public Exception getException(SchedulerType schedulerType){
		return exceptionMap.get(schedulerType);
	}
}
