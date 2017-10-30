/**
 * 
 */
package com.sacknibbles.sch.scheduler.exception;

/**
 * @author Sachin
 *
 */
public class DuplicateJobKeyException extends HttpJobSchedulerException{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public DuplicateJobKeyException(String message){
			super(message);
		}

		/**
		 * 
		 */
		public DuplicateJobKeyException() {
		}

		/**
		 * @param cause
		 */
		public DuplicateJobKeyException(Throwable cause) {
			super(cause);
		}
}
