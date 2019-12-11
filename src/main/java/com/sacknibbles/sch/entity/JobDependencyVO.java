/**
 * 
 */
package com.sacknibbles.sch.entity;

import java.util.List;

/**
 * @author Sachin
 *
 */
public class JobDependencyVO {

	private String jobId;
	private List<DependentJob> dependentOn;
	
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
	 * @return the dependentOn
	 */
	public List<DependentJob> getDependentOn() {
		return dependentOn;
	}
	/**
	 * @param dependentOn the dependentOn to set
	 */
	public void setDependentOn(List<DependentJob> dependentOn) {
		this.dependentOn = dependentOn;
	}	
}
