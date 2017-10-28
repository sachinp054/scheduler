/**
 * 
 */
package com.sacknibbles.sch.scheduler.helper;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sacknibbles.sch.avro.model.SchedulerType;
import com.sacknibbles.sch.factory.HttpJobSchedulerFactory;

/**
 * @author Sachin
 *
 */
@Service
public class SchedulerHelper {

	@Autowired
	private HttpJobSchedulerFactory schFactory;
	
	public boolean ifJobExists(SchedulerType schedulerType, JobKey jobKey) throws SchedulerException{
		return schFactory.getScheduler(schedulerType).checkExists(jobKey);
	}
	
	public String getNextFireTime(SchedulerType schedulerType, JobKey jobKey) throws SchedulerException{
		Scheduler scheduler = schFactory.getScheduler(schedulerType);
		List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
		Date nextFireTime = null;
		if(!triggers.isEmpty()){
			nextFireTime = triggers.get(0).getNextFireTime();
		}else{
			return "NA";
		}
		
		return Objects.nonNull(nextFireTime)?nextFireTime.toString():"NA'";
	}
	
}
