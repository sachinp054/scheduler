/**
 * 
 */
package com.sacknibbles.sch.job;

import org.quartz.JobExecutionContext;

import com.sacknibbles.sch.avro.model.SchedulerResponse.Builder;

/**
 * @author Sachin
 *
 */
public class HttpJob extends BaseJob{

	/* (non-Javadoc)
	 * @see com.sacknibbles.sch.job.BaseJob#executeJob(org.quartz.JobExecutionContext, com.sacknibbles.sch.avro.model.SchedulerResponse.Builder)
	 */
	@Override
	protected Builder executeJob(JobExecutionContext context, Builder result) {
		return result;
	}

}
