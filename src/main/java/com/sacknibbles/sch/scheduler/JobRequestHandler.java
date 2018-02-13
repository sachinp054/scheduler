/**
 * 
 */
package com.sacknibbles.sch.scheduler;

import static com.sacknibbles.sch.controller.avro.util.Utils.convertToAvroJSONString;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sacknibbles.sch.avro.model.JobRequestRecord;
import com.sacknibbles.sch.avro.model.JobStatus;
import com.sacknibbles.sch.avro.model.SchedulerResponse;
import com.sacknibbles.sch.avro.model.SchedulerResponse.Builder;
import com.sacknibbles.sch.controller.avro.util.Utils;
import com.sacknibbles.sch.scheduler.exception.BadRequestException;
import com.sacknibbles.sch.scheduler.exception.DuplicateJobKeyException;
import com.sacknibbles.sch.scheduler.exception.HttpJobSchedulerException;
import com.sacknibbles.sch.scheduler.exception.SchedulerUnavailableException;
import com.sacknibbles.sch.scheduler.helper.SchedulerHelper;

/**
 * @author Sachin
 *
 */
@Service
public class JobRequestHandler {
	private Logger logger = LoggerFactory.getLogger(JobRequestHandler.class);
	
	@Autowired
	private HttpJobScheduler httpJobScheduler;
	@Autowired
	private SchedulerHelper schedulerHelper;

	public ResponseEntity<String> scheduleJob(String jobRequestPayload) {
		Builder response = SchedulerResponse.newBuilder();
		HttpStatus httpStatus = HttpStatus.OK;
		JobRequestRecord request = null;
		try {
			request = httpJobScheduler.scheduleJob(jobRequestPayload);		
			String jobId = request.getJobId();
			String jobName = request.getJobName();
			String jobGroupName = request.getJobGroupName();
			
			String currentJobStatus;
			try {
				currentJobStatus = schedulerHelper.getJobStatusVo(jobId).getJobStatus().name();
			} catch (Exception e) {
				currentJobStatus = "COULD NOT FETCH CURRENT STATUS";
			}
			String nextFireTime = schedulerHelper.getNextFireTime(request.getSchedulerType(),
					Utils.getJobKey(jobId, jobGroupName, jobName));
			response.setJobId(jobId).setJobName(jobName).setJobGroupName(jobGroupName).setJobStatus(JobStatus.SCHEDULED)
					.setResponseMessage("Job scheduled successfully! NextFireTime:: {"+nextFireTime+"}, Current Status::{" +currentJobStatus+"}");
		} catch (BadRequestException e) {
			httpStatus = HttpStatus.BAD_REQUEST;
			response.setResponseMessage("Request validation failed, cause::[" + e.getMessage() + "]")
					.setJobStatus(JobStatus.FAILED);
		} catch (DuplicateJobKeyException e1) {
			httpStatus = HttpStatus.BAD_REQUEST;
			handleException(response, e1);
		} catch (SchedulerUnavailableException e2) {
			httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
			response.setResponseMessage(e2.getLocalizedMessage()).setJobStatus(JobStatus.FAILED);
		} catch (HttpJobSchedulerException e3) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			response.setResponseMessage(e3.getLocalizedMessage()).setJobStatus(JobStatus.FAILED);
		} catch (SchedulerException e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			response.setResponseMessage(e.getLocalizedMessage()).setJobStatus(JobStatus.FAILED);
		}

		return ResponseEntity.status(httpStatus).body(convertToAvroJSONString(response.build()));
	}

	/**
	 * @param response
	 * @param e1
	 */
	private void handleException(Builder response, DuplicateJobKeyException e1) {
		response.setResponseMessage(e1.getLocalizedMessage()).setJobStatus(JobStatus.FAILED);
		try {
			schedulerHelper.createJobStatusEntry(response.getJobId(), response.getJobStatus(),
					response.getResponseMessage());
		} catch (Exception e) {
			logger.error("Exception occured while creating Job status Entry for jobId::{}", response.getJobId(), e);
		}
	}

}
