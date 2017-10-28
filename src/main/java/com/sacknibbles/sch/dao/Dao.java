/**
 * 
 */
package com.sacknibbles.sch.dao;

import java.util.List;

import com.sacknibbles.sch.constants.DaoName;

/**
 * @author Sachin
 *
 */
public interface Dao<T> {

	public  T insert(T t) throws Exception;
	public  T update(T t) throws Exception;
	public   List<T> fetchAll() throws Exception;
	public  T fetchById(String id) throws Exception;
	public  DaoName getDaoName();
	
}
