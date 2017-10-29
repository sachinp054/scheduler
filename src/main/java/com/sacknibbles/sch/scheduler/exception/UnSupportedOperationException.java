/**
 * 
 */
package com.sacknibbles.sch.scheduler.exception;

/**
 * @author Sachin
 *
 */
public class UnSupportedOperationException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	UnSupportedOperationException(){
		super();
	}
	public UnSupportedOperationException(String message){
		super(message);
	}
}
