/**
 * 
 */
package com.sacknibbles.sch.builder;

import static com.sacknibbles.sch.constants.Constant.ACK_EMAIL;
import static com.sacknibbles.sch.constants.Constant.ACK_URL;
import static com.sacknibbles.sch.constants.Constant.CLIENT_IDENTIFIER;
import static com.sacknibbles.sch.constants.Constant.JOB_GROUP_NAME;
import static com.sacknibbles.sch.constants.Constant.JOB_KEY;
import static com.sacknibbles.sch.constants.Constant.JOB_NAME;
import static com.sacknibbles.sch.constants.Constant.MANDATORY_DEPENDENCIES;
import static com.sacknibbles.sch.constants.Constant.TARGET_URL;
import static com.sacknibbles.sch.constants.Constant.TARGET_URL_RETRY_LIMIT;
import static com.sacknibbles.sch.constants.Constant.TARGET_URL_RETRY_RATE;
import static org.quartz.JobBuilder.newJob;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;

import com.sacknibbles.sch.avro.model.JobRequestRecord;

/**
 * @author Sachin
 *
 */
public class JobDetailBuilder {

	private JobDetailBuilder(){}
	
	public static JobDetail buildJobDetail(JobRequestRecord jobRequestRecord, Class<? extends Job> httpJobType){
		JobKey jobKey = new JobKey(jobRequestRecord.getJobGroupName(), jobRequestRecord.getJobName());
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put(JOB_GROUP_NAME, jobRequestRecord.getJobGroupName());
		jobDataMap.put(JOB_NAME, jobRequestRecord.getJobName());
		jobDataMap.put(JOB_KEY,jobKey );
		jobDataMap.put(CLIENT_IDENTIFIER, jobRequestRecord.getClientIdentity());
		jobDataMap.put(TARGET_URL, jobRequestRecord.getTargetUrl());
		jobDataMap.put(ACK_URL,jobRequestRecord.getAckUrl());
		jobDataMap.put(ACK_EMAIL, jobRequestRecord.getAckEmailAddress());
		jobDataMap.put(TARGET_URL_RETRY_LIMIT, jobRequestRecord.getTargetUrlRetryLimit());
		jobDataMap.put(TARGET_URL_RETRY_RATE, jobRequestRecord.getTargetUrlRetryRate());
		jobDataMap.put(MANDATORY_DEPENDENCIES, jobRequestRecord.getMandatoryDependencies());
		return newJob(httpJobType).withIdentity(jobKey).usingJobData(jobDataMap).build() ;
	}
}
