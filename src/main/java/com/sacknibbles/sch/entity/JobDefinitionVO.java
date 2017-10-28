/**
 * 
 */
package com.sacknibbles.sch.entity;

import java.util.Date;

/**
 * @author Sachin
 *
 */
public class JobDefinitionVO {

	private String jobId;
	private String jobName;
	private String jobGroupName;
	private String jobRequestPayload;
	private Date instTs;
	
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
	 * @return the jobName
	 */
	public String getJobName() {
		return jobName;
	}
	/**
	 * @param jobName the jobName to set
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	/**
	 * @return the jobGroupName
	 */
	public String getJobGroupName() {
		return jobGroupName;
	}
	/**
	 * @param jobGroupName the jobGroupName to set
	 */
	public void setJobGroupName(String jobGroupName) {
		this.jobGroupName = jobGroupName;
	}
	/**
	 * @return the jobRequestPayload
	 */
	public String getJobRequestPayload() {
		return jobRequestPayload;
	}
	/**
	 * @param jobRequestPayload the jobRequestPayload to set
	 */
	public void setJobRequestPayload(String jobRequestPayload) {
		this.jobRequestPayload = jobRequestPayload;
	}
	/**
	 * @return the instTs
	 */
	public Date getInstTs() {
		return instTs;
	}
	/**
	 * @param instTs the instTs to set
	 */
	public void setInstTs(Date instTs) {
		this.instTs = instTs;
	}
		
}
