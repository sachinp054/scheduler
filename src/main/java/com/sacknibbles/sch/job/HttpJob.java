/**
 * 
 */
package com.sacknibbles.sch.job;

import org.quartz.JobExecutionContext;

import com.sacknibbles.sch.avro.model.SchedulerResponse;

/**
 * @author Sachin
 *
 */
public class HttpJob extends BaseJob{

	
	@Override
	protected SchedulerResponse executeJob(SchedulerResponse result, JobExecutionContext context) {
		return null;
	}

}
