/**
 * 
 */
package com.sacknibbles.sch.scheduler.exception;

/**
 * @author Sachin
 *
 */
public class HttpJobSchedulerException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HttpJobSchedulerException(String message){
		super(message);
	}

	/**
	 * 
	 */
	public HttpJobSchedulerException() {
	}

	/**
	 * @param cause
	 */
	public HttpJobSchedulerException(Throwable cause) {
		super(cause);
	}
}
