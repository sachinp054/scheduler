/**
 * 
 */
package com.sacknibbles.sch.scheduler.exception;

/**
 * @author Sachin
 *
 */
public class SchedulerUnavailableException extends HttpJobSchedulerException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SchedulerUnavailableException(String message){
		super(message);
	}

	/**
	 * 
	 */
	public SchedulerUnavailableException() {
	}

	/**
	 * @param cause
	 */
	public SchedulerUnavailableException(Throwable cause) {
		super(cause);
	}
}
