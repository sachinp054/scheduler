/**
 * 
 */
package com.sacknibbles.sch.scheduler.exception;

/**
 * @author Sachin
 *
 */
public class BadRequestException extends HttpJobSchedulerException{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public BadRequestException(String message){
			super(message);
		}

		/**
		 * 
		 */
		public BadRequestException() {
		}

		/**
		 * @param cause
		 */
		public BadRequestException(Throwable cause) {
			super(cause);
		}
}
