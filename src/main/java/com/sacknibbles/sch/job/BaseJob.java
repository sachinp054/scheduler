/**
 * 
 */
package com.sacknibbles.sch.job;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sacknibbles.sch.ack.handler.AckHandler;
import com.sacknibbles.sch.avro.model.JobStatus;
import com.sacknibbles.sch.avro.model.SchedulerResponse;
import com.sacknibbles.sch.avro.model.SchedulerResponse.Builder;
import com.sacknibbles.sch.avro.model.URLRecord;
import com.sacknibbles.sch.constants.Constant;
import com.sacknibbles.sch.controller.avro.util.Utils;
import com.sacknibbles.sch.entity.JobStatusVO;
import com.sacknibbles.sch.scheduler.HttpJobScheduler;
import com.sacknibbles.sch.scheduler.exception.HttpJobSchedulerDaoException;
import com.sacknibbles.sch.scheduler.exception.PendingMandatoryJobDependencyException;
import com.sacknibbles.sch.scheduler.helper.SchedulerHelper;

/**
 * @author Sachin
 *
 */
public abstract class BaseJob implements Job {

	private Logger logger = LoggerFactory.getLogger(HttpJobScheduler.class);

	@Override
	public final void execute(JobExecutionContext context) throws JobExecutionException {
		Builder result = SchedulerResponse.newBuilder();
		try {
			JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
			String jobId = jobDataMap.getString(Constant.JOB_ID);
			logger.info("Job [JobId::{}] triggered.", jobId);
			String jobName = jobDataMap.getString(Constant.JOB_NAME);
			String jobGroupName = jobDataMap.getString(Constant.JOB_GROUP_NAME);
			JobKey jobKey = Utils.getJobKey(jobId, jobGroupName, jobName);
			synchronized (jobKey) {
				result.setJobId(jobId).setJobName(jobName).setJobGroupName(jobGroupName)
						.setJobKey(Utils.getJobKey(jobId, jobGroupName, jobName).toString());
				preProcess(context);
				result = executeJob(context, result);
			}
		} catch (Exception e) {
			handleException(result, e);
		} finally {
			postProcess(result, context);
		}

	}

	/**
	 * @param result
	 * @param context
	 * @return
	 */
	protected abstract Builder executeJob(JobExecutionContext context, Builder result);

	/**
	 * 
	 * @param context
	 */
	protected void preProcessJob(JobExecutionContext context) {
	}

	/**
	 * 
	 * @param result
	 * @param context
	 */
	protected void postProcessJob(Builder result, JobExecutionContext context) {
	}

	/**
	 * @param result
	 * @param context
	 * 
	 */
	private final void handleException(Builder result, Exception e) {
		if (e instanceof PendingMandatoryJobDependencyException) {
			result.setJobStatus(JobStatus.WAITING_FOR_MANDATORY_DEPENDENCIES);
		} else {
			result.setJobStatus(JobStatus.FAILED);
		}
		result.setResponseMessage(e.getLocalizedMessage());
		logger.error("Exception occured while executing job", e);
	}

	/**
	 * @param context
	 * @throws Exception
	 */
	private void postProcess(Builder result, JobExecutionContext context) {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		SchedulerHelper schedulerHelper = (SchedulerHelper) jobDataMap.get(Constant.SCHEDULER_HELPER);
		String jobId = jobDataMap.getString(Constant.JOB_ID);
		URLRecord urlRecord = (URLRecord) jobDataMap.get(Constant.ACK_URL);
		String ackEmail = jobDataMap.getString(Constant.ACK_EMAIL);
		try {
			postProcessJob(result, context);
			updateJobStatus(schedulerHelper, jobId, result.getJobStatus(), result.getResponseMessage());
		} catch (Exception e) {
			handleException(result, e);
		}
		sendEmailNotification(ackEmail, result);
		sendAcknowledgement(urlRecord, result);
	}

	/**
	 * @param urlRecord
	 * @param result
	 */
	private void sendAcknowledgement(URLRecord urlRecord, Builder result) {
		AckHandler.sendAcknowledgement(urlRecord, result.build());
	}

	/**
	 * @param ackEmail
	 * @param result
	 * 
	 */
	private void sendEmailNotification(String ackEmail, Builder result) {
		AckHandler.sendAcknowledgement(ackEmail, result.build());
	}

	/**
	 * @param result
	 * @param jobId
	 * @throws HttpJobSchedulerDaoException
	 * @throws Exception
	 */
	private void updateJobStatus(SchedulerHelper schedulerHelper, String jobId, JobStatus jobStatus,
			String responseMessage) throws HttpJobSchedulerDaoException {
		JobStatusVO vo = schedulerHelper.getJobStatusVo(jobId);
		vo.setJobStatus(jobStatus);
		vo.setErrorDesc(responseMessage);
		vo.setJobCompletionTime(new Date(System.currentTimeMillis()));
		schedulerHelper.updateJobStatus(vo);
	}

	/**
	 * @param context
	 * @return
	 * @throws HttpJobSchedulerDaoException
	 * @throws PendingMandatoryJobDependencyException
	 * @throws Exception
	 */
	private void preProcess(JobExecutionContext context)
			throws HttpJobSchedulerDaoException, PendingMandatoryJobDependencyException {

		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		SchedulerHelper schedulerHelper = (SchedulerHelper) jobDataMap.get(Constant.SCHEDULER_HELPER);
		String jobId = jobDataMap.getString(Constant.JOB_ID);
		JobStatusVO jobStatus = schedulerHelper.getJobStatusVo(jobId);
		@SuppressWarnings("unchecked")
		List<String> manDependencies = (List<String>) jobDataMap.get(Constant.MANDATORY_DEPENDENCIES);
		if (Objects.nonNull(manDependencies)) {
			List<JobStatusVO> statusVOs = schedulerHelper.getJobStatusByIds(manDependencies);
			List<JobStatusVO> pendingJobs = statusVOs.stream()
					.filter(v -> !v.getJobStatus().equals(JobStatus.COMPLETED)).collect(Collectors.toList());
			if (!pendingJobs.isEmpty()) {
				throw new PendingMandatoryJobDependencyException("Could not execute job{" + jobId
						+ "}, mandatory job dependencies {" + pendingJobs + "} are not completed yet");
			}
		}
		jobStatus.setJobStatus(JobStatus.STARTED);
		jobStatus.setJobStartTime(new Date(System.currentTimeMillis()));
		schedulerHelper.updateJobStatus(jobStatus);
		preProcessJob(context);
	}

}
