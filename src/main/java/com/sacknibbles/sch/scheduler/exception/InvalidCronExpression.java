/**
 * 
 */
package com.sacknibbles.sch.scheduler.exception;

/**
 * @author Sachin
 *
 */
public class InvalidCronExpression extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidCronExpression(){
		super();
	}
	public InvalidCronExpression(String message){
		super(message);
	}
}
