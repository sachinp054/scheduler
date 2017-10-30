/**
 * 
 */
package com.sacknibbles.sch.dao;

import java.util.List;

import com.sacknibbles.sch.constants.DaoName;
import com.sacknibbles.sch.scheduler.exception.HttpJobSchedulerDaoException;

/**
 * @author Sachin
 *
 */
public interface Dao<T> {

	public  T insert(T t) throws HttpJobSchedulerDaoException;
	public  T update(T t) throws HttpJobSchedulerDaoException;
	public   List<T> fetchAll() throws HttpJobSchedulerDaoException;
	public  T fetchById(String id) throws HttpJobSchedulerDaoException;
	public  DaoName getDaoName();
	
}
