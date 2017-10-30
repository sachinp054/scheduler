/**
 * 
 */
package com.sacknibbles.sch.scheduler.exception;

/**
 * @author Sachin
 *
 */
public class HttpJobSchedulerDaoException extends HttpJobSchedulerException{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public HttpJobSchedulerDaoException(String message){
			super(message);
		}

		/**
		 * 
		 */
		public HttpJobSchedulerDaoException() {
		}

		/**
		 * @param cause
		 */
		public HttpJobSchedulerDaoException(Throwable cause) {
			super(cause);
		}
}
