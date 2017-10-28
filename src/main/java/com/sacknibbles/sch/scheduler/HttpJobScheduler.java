/**
 * 
 */
package com.sacknibbles.sch.scheduler;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sacknibbles.sch.avro.model.JobRequestRecord;
import com.sacknibbles.sch.avro.model.JobStatus;
import com.sacknibbles.sch.avro.model.SchedulerResponse;
import com.sacknibbles.sch.avro.model.SchedulerResponse.Builder;
import com.sacknibbles.sch.avro.model.SchedulerType;
import com.sacknibbles.sch.builder.JobDetailBuilder;
import com.sacknibbles.sch.builder.TriggerBuilder;
import com.sacknibbles.sch.controller.avro.util.Utils;
import com.sacknibbles.sch.factory.HttpJobSchedulerFactory;
import com.sacknibbles.sch.job.HttpJob;
import com.sacknibbles.sch.request.validator.JobRequestValidator;
import com.sacknibbles.sch.scheduler.exception.HttpJobSchedulerException;
import com.sacknibbles.sch.scheduler.helper.SchedulerHelper;

/**
 * @author Sachin
 *
 */
@Service
public class HttpJobScheduler {

	private Logger logger = LoggerFactory.getLogger(HttpJobScheduler.class);
	@Autowired
	private HttpJobSchedulerFactory schFactory;
	@Autowired
	private JobRequestValidator requestValidator;
	@Autowired
	private SchedulerHelper schedulerHelper;

	public SchedulerResponse scheduleJob(String jobRequestPayload) {

		Builder respbuilder = SchedulerResponse.newBuilder();
		try {
			JobRequestRecord jobRequest = requestValidator.jobRequestValidator(jobRequestPayload, respbuilder);
			if (respbuilder.hasException()) {
				return respbuilder.build();
			}
			
			JobKey jobKey = Utils.getJobKey(jobRequest.getJobId(),jobRequest.getJobGroupName(),jobRequest.getJobName());
			respbuilder.setJobId(jobRequest.getJobId()).setJobGroupName(jobRequest.getJobGroupName()).setJobName(jobRequest.getJobName())
					.setJobKey(jobKey.toString());
			SchedulerType schedulerType = jobRequest.getSchedulerType();
			Scheduler scheduler = schFactory.getScheduler(schedulerType);
			
			if (Objects.nonNull(scheduler)) {
				if (schedulerHelper.ifJobExists(schedulerType, jobKey)) {
					handleAlreadyScheduledJob(respbuilder, jobKey, scheduler);
				} else {
					handleNewJobScheduling(respbuilder, jobRequest, jobKey, schedulerType, scheduler);
				}
			} else {
				String message = " scheduler is not running. Checkout the application startup log for more information.";
				respbuilder.setException(schFactory.getException(jobRequest.getSchedulerType()).toString())
						.setJobStatus(JobStatus.FAILED).setResponseMessage(message);
				logger.warn("{} {}", jobRequest.getSchedulerType(), message);
			}
		} catch (SchedulerException schExp) {
			respbuilder.setException(schExp.toString()).setJobStatus(JobStatus.FAILED)
					.setResponseMessage(schExp.getLocalizedMessage());
			logger.error("Exception occured while scheduling job ::[{}]:: cause::{}",
					jobRequestPayload.substring(0, 100));
		}
		return respbuilder.build();
	}

	/**
	 * @param respbuilder
	 * @param jobRequest
	 * @param jobKey
	 * @param schedulerType
	 * @param scheduler
	 * @throws SchedulerException
	 */
	private void handleNewJobScheduling(Builder respbuilder, JobRequestRecord jobRequest, JobKey jobKey,
			SchedulerType schedulerType, Scheduler scheduler) throws SchedulerException {
		Set<Trigger> triggers = TriggerBuilder.buidlTriggers(jobRequest, respbuilder);
		if (respbuilder.hasException()) {
			return;
		}
		JobDetail jobDetail = JobDetailBuilder.buildJobDetail(jobRequest, HttpJob.class);
		if (triggers.isEmpty()) {
			scheduler.addJob(jobDetail, false);
		} else {
			scheduler.scheduleJob(jobDetail, triggers, false);
		}
		String nextFireTime = schedulerHelper.getNextFireTime(schedulerType, jobKey);
		if ("NA".equals(nextFireTime)) {
			respbuilder.setJobStatus(JobStatus.ADDED_NOTSCHEDULED).setResponseMessage(
					"Job added to scheduler but not scheduled. There must be be some problem with trigger details provided."
							+ "Use endpoint to update job trigger!!");
		} else {
			respbuilder.setJobStatus(JobStatus.SCHEDULED)
					.setResponseMessage("Jobscheduled at ::" + schedulerHelper.getNextFireTime(schedulerType, jobKey));
		}
	}

	/**
	 * @param respbuilder
	 * @param jobKey
	 * @param scheduler
	 * @throws SchedulerException
	 */
	private void handleAlreadyScheduledJob(Builder respbuilder, JobKey jobKey, Scheduler scheduler)
			throws SchedulerException {
		List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
		String message = "Job alreadey scheduled for jobKey:: " + jobKey;
		respbuilder.setJobStatus(JobStatus.FAILED)
				.setResponseMessage(message + " at ::" + triggers.get(0).getNextFireTime())
				.setException(new HttpJobSchedulerException(message).toString());
	}
}
