/**
 * 
 */
package com.sacknibbles.sch.scheduler;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
import com.sacknibbles.sch.avro.model.SchedulerType;
import com.sacknibbles.sch.builder.JobDetailBuilder;
import com.sacknibbles.sch.builder.TriggerBuilder;
import com.sacknibbles.sch.constants.Constant;
import com.sacknibbles.sch.controller.avro.util.Utils;
import com.sacknibbles.sch.entity.JobStatusVO;
import com.sacknibbles.sch.factory.HttpJobSchedulerFactory;
import com.sacknibbles.sch.job.HttpJob;
import com.sacknibbles.sch.request.validator.JobRequestValidator;
import com.sacknibbles.sch.scheduler.exception.BadRequestException;
import com.sacknibbles.sch.scheduler.exception.DuplicateJobKeyException;
import com.sacknibbles.sch.scheduler.exception.HttpJobSchedulerDaoException;
import com.sacknibbles.sch.scheduler.exception.HttpJobSchedulerException;
import com.sacknibbles.sch.scheduler.exception.SchedulerUnavailableException;
import com.sacknibbles.sch.scheduler.helper.SchedulerHelper;

/**
 * @author Sachin
 *
 */
@Service
public class HttpJobScheduler {
	private static final Logger logger = LoggerFactory.getLogger(HttpJobScheduler.class);

	@Autowired
	private HttpJobSchedulerFactory schFactory;
	@Autowired
	private JobRequestValidator requestValidator;
	@Autowired
	private SchedulerHelper schedulerHelper;

	public JobRequestRecord scheduleJob(String jobRequestPayload) throws SchedulerException, HttpJobSchedulerException {
		JobRequestRecord jobRequest = preProcessSchedulingRequest(jobRequestPayload);
		scheduleJobRequest(jobRequest);
		return jobRequest;
	}

	/**
	 * @param jobRequestPayload
	 * @return
	 * @throws HttpJobSchedulerException
	 */
	private void scheduleJobRequest(JobRequestRecord jobRequest) throws HttpJobSchedulerException {
		try {

			JobKey jobKey = Utils.getJobKey(jobRequest.getJobId(), jobRequest.getJobGroupName(),
					jobRequest.getJobName());
			SchedulerType schedulerType = jobRequest.getSchedulerType();
			Optional<Scheduler> scheduler = schFactory.getScheduler(schedulerType);
			if (scheduler.isPresent()) {
				synchronized (jobKey) {
					handleNewJobScheduling(jobRequest, jobKey, schedulerType, scheduler.get());
					updateJobStatus(jobRequest);
				}	
			}
		} catch (Exception e) {
			handleException(jobRequest, e);
		}
	}

	/**
	 * @param jobRequest
	 * @throws HttpJobSchedulerDaoException
	 */
	private void updateJobStatus(JobRequestRecord jobRequest) throws HttpJobSchedulerDaoException {
		JobStatusVO vo = schedulerHelper.getJobStatusVo(jobRequest.getJobId());
		vo.setJobStatus(JobStatus.SCHEDULED);
		schedulerHelper.updateJobStatus(vo);
	}

	/**
	 * @param jobRequest
	 * @param respbuilder
	 * @param schExp
	 * @throws HttpJobSchedulerException
	 */
	private void handleException(JobRequestRecord jobRequest, Exception e) throws HttpJobSchedulerException {
		logger.error("Exception occured while scheduling job ::[JobName-{},JobGroupName-{},JobId-{}]:: cause::{}",
				jobRequest.getJobName(), jobRequest.getJobGroupName(), jobRequest.getJobId(), e);
		throw new HttpJobSchedulerException(
				"Exception occured while scheduling job ::[JobName-{" + jobRequest.getJobName() + "},JobGroupName-{"
						+ jobRequest.getJobGroupName() + "},JobId-{" + jobRequest.getJobId() + "}])");
	}

	/**
	 * @param respbuilder
	 * @return
	 * @throws BadRequestException
	 * @throws SchedulerException
	 * @throws DuplicateJobKeyException
	 * @throws SchedulerUnavailableException
	 * @throws HttpJobSchedulerDaoException
	 * @throws HttpJobSchedulerException
	 * 
	 */
	private JobRequestRecord preProcessSchedulingRequest(String jobRequestPayload) throws HttpJobSchedulerException, SchedulerException {
		JobRequestRecord jobRequest = null;
		jobRequest = requestValidator.validateAndGetJobRequestRecord(jobRequestPayload);
		Optional<Scheduler> scheduler = schFactory.getScheduler(jobRequest.getSchedulerType());
		if (scheduler.isPresent()) {
			JobKey jobKey = Utils.getJobKey(jobRequest.getJobId(), jobRequest.getJobGroupName(),
					jobRequest.getJobName());
			if (schedulerHelper.ifJobExists(jobRequest.getSchedulerType(), jobKey)) {
				handleAlreadyScheduledJob(jobKey, scheduler.get());
				return jobRequest;
			}
		} else {
			throw new SchedulerUnavailableException(jobRequest.getSchedulerType()
					+ " scheduler is not running. Checkout the application startup log for more information.");
		}
		schedulerHelper.createJobDefinition(jobRequest.getJobId(), jobRequest.getJobName(),
				jobRequest.getJobGroupName(), jobRequestPayload);
		schedulerHelper.createJobDependency(jobRequest);
		schedulerHelper.createJobStatusEntry(jobRequest.getJobId(), JobStatus.ADDED, "");
		List<String> mandatoryDependencies = jobRequest.getMandatoryDependencies();
		if(Objects.nonNull(mandatoryDependencies)){
			//Need to improve should be batch execution and few extra things
			mandatoryDependencies.forEach(m->{try {
				schedulerHelper.createJobStatusEntry(m, JobStatus.ADDED, "");
			} catch (Exception e) {
				logger.error("[Mandatory dependendency of::{}] Could not create job status for jobId::{}",m);
			}});
			
		}
		return jobRequest;

	}

	/**
	 * @param respbuilder
	 * @param jobRequest
	 * @param jobKey
	 * @param schedulerType
	 * @param scheduler
	 * @throws SchedulerException
	 * @throws BadRequestException
	 */
	private JobStatus handleNewJobScheduling(JobRequestRecord jobRequest, JobKey jobKey, SchedulerType schedulerType,
			Scheduler scheduler) throws SchedulerException, BadRequestException {
		Set<Trigger> triggers = TriggerBuilder.buidlTriggers(jobRequest);
		JobDetail jobDetail = JobDetailBuilder.buildJobDetail(jobRequest, HttpJob.class);
		jobDetail.getJobDataMap().put(Constant.SCHEDULER_HELPER, schedulerHelper);
		if (triggers.isEmpty()) {
			scheduler.addJob(jobDetail, false);
		} else {
			scheduler.scheduleJob(jobDetail, triggers, false);
		}
		String nextFireTime = schedulerHelper.getNextFireTime(schedulerType, jobKey);
		if ("NA".equals(nextFireTime)) {
			return JobStatus.ADDED_NOTSCHEDULED;
		}
		return JobStatus.SCHEDULED;
	}

	/**
	 * @param respbuilder
	 * @param jobKey
	 * @param scheduler
	 * @throws SchedulerException
	 * @throws DuplicateJobKeyException
	 */
	private void handleAlreadyScheduledJob(JobKey jobKey, Scheduler scheduler)
			throws SchedulerException, DuplicateJobKeyException {
		List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
		throw new DuplicateJobKeyException("Job alreadey scheduled for jobKey at" + triggers.get(0).getNextFireTime());
	}
}
