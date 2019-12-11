/**
 * 
 */
package com.sacknibbles.sch.scheduler.exception;

/**
 * @author Sachin
 *
 */
public class PendingMandatoryJobDependencyException extends HttpJobSchedulerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param string
	 */
	public PendingMandatoryJobDependencyException(String msg) {
		super(msg);
	}

}
