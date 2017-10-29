/**
 * 
 */
package com.sacknibbles.sch.builder;

import static org.quartz.CronExpression.isValidExpression;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.quartz.Trigger;

import com.sacknibbles.sch.avro.model.JobRequestRecord;
import com.sacknibbles.sch.avro.model.SchedulerResponse.Builder;
import com.sacknibbles.sch.avro.model.TriggerRecord;
import com.sacknibbles.sch.controller.avro.util.HttpJobDateParser;
import com.sacknibbles.sch.controller.avro.util.Utils;
import com.sacknibbles.sch.scheduler.exception.InvalidCronExpression;
/**
 * @author Sachin
 *
 */
public class TriggerBuilder {

	private TriggerBuilder(){}
	
	public static Set<Trigger> buidlTriggers(JobRequestRecord jobResuestRecord, Builder respBuilder){
		TriggerRecord trigger = jobResuestRecord.getTrigger();
		String cron = trigger.getCron();
		String schedule = trigger.getSchedule();
		Trigger t = null;
		if(!isNullOrEmpty(cron)){
			if(isValidExpression(cron)){
				t = newTrigger().withIdentity(getTriggerKey()).withSchedule(cronSchedule(cron))
						.usingJobData("cron",cron).build();
			}else{
				respBuilder.setException(new InvalidCronExpression().toString())
				.setResponseMessage("Cron expression ["+cron+"] is invalid!!");
			}
		}else if(!isNullOrEmpty(schedule)){
			Date triggerTime = null;
			try{
				triggerTime = HttpJobDateParser.getDateTime(schedule);
				t = newTrigger().withIdentity(getTriggerKey()).startAt(triggerTime)
						.usingJobData("schedule",schedule).build();
			}catch(Exception e){
				respBuilder.setException(e.toString())
				.setResponseMessage("Could not parse trigger schedule. Schedule detail should either be in LocalDateTime.toString() format or "
						+ " dd/MM/yyyy HH24:mm format");
			}
		}
		return new HashSet<>(Arrays.asList(t));
	}

	/**
	 * @param jobKey
	 * @return
	 */
	private static String getTriggerKey() {
		return Utils.getId();
	}

	/**
	 * @param cron
	 * @return
	 */
	private static boolean isNullOrEmpty(String str) {
		return str==null|| str.isEmpty();
	}
	
}
