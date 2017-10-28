/**
 * 
 */
package com.sacknibbles.sch.entity;

import java.util.Date;

import com.sacknibbles.sch.avro.model.JobStatus;

/**
 * @author Sachin
 *
 */
public class JobStatusVO {
	
	private String jobId;
	private JobStatus jobStatus;
	private Date jobStartTime;
	private Date jobCompletionTime;
	private String errorDesc;
	
	/**
	 * @return the jobId
	 */
	public String getJobId() {
		return jobId;
	}
	/**
	 * @param jobId the jobId to set
	 */
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	/**
	 * @return the jobStatus
	 */
	public JobStatus getJobStatus() {
		return jobStatus;
	}
	/**
	 * @param jobStatus the jobStatus to set
	 */
	public void setJobStatus(JobStatus jobStatus) {
		this.jobStatus = jobStatus;
	}
	/**
	 * @return the errorDesc
	 */
	public String getErrorDesc() {
		return errorDesc;
	}
	/**
	 * @param errorDesc the errorDesc to set
	 */
	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}
	/**
	 * @return the jobStartTime
	 */
	public Date getJobStartTime() {
		return jobStartTime;
	}
	/**
	 * @param jobStartTime the jobStartTime to set
	 */
	public void setJobStartTime(Date jobStartTime) {
		this.jobStartTime = jobStartTime;
	}
	/**
	 * @return the jobCompletionTime
	 */
	public Date getJobCompletionTime() {
		return jobCompletionTime;
	}
	/**
	 * @param jobCompletionTime the jobCompletionTime to set
	 */
	public void setJobCompletionTime(Date jobCompletionTime) {
		this.jobCompletionTime = jobCompletionTime;
	}
		
}
