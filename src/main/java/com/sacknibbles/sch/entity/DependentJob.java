/**
 * 
 */
package com.sacknibbles.sch.entity;

/**
 * @author Sachin
 *
 */

public class DependentJob {

	private String jobId;
	private boolean manadatoryDependency;
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
	 * @return the manadatoryDependency
	 */
	public boolean isManadatoryDependency() {
		return manadatoryDependency;
	}
	/**
	 * @param manadatoryDependency the manadatoryDependency to set
	 */
	public void setManadatoryDependency(boolean manadatoryDependency) {
		this.manadatoryDependency = manadatoryDependency;
	}
	
	
}
