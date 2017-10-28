/**
 * 
 */
package com.sacknibbles.sch.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.sacknibbles.sch.avro.model.SchedulerResponse;

/**
 * @author Sachin
 *
 */
public abstract class BaseJob implements Job{

	@Override
	public final void execute(JobExecutionContext context) throws JobExecutionException {
		SchedulerResponse result = preProcess(context);
		result = executeJob(result,context);
		postProcess(result);
	}

	/**
	 * @param result 
	 * @param context
	 * @return
	 */
	protected abstract SchedulerResponse executeJob(SchedulerResponse result, JobExecutionContext context);

	/**
	 * @param context
	 */
	protected SchedulerResponse postProcess(SchedulerResponse jobResult) {
		return null;
	}

	/**
	 * @param context
	 * @return
	 */
	protected SchedulerResponse preProcess(JobExecutionContext context) {
		return null;
	}

}
