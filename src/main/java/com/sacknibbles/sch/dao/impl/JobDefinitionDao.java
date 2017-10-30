/**
 * 
 */
package com.sacknibbles.sch.dao.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.sacknibbles.sch.constants.DaoName;
import com.sacknibbles.sch.controller.avro.util.Utils;
import com.sacknibbles.sch.dao.Dao;
import com.sacknibbles.sch.entity.JobDefinitionVO;
import com.sacknibbles.sch.scheduler.exception.HttpJobSchedulerDaoException;
import com.sacknibbles.sch.scheduler.exception.UnSupportedDaoOperationException;

/**
 * @author Sachin
 *
 */
@Service
public class JobDefinitionDao implements Dao<JobDefinitionVO> {

	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	JobDefinitionDao(JdbcTemplate jdbcTemplate){
		this.jdbcTemplate = jdbcTemplate;
	}
	private final String INSERT_QUERY = "INSERT INTO JOB_DEFINITION (JOB_ID,JOB_NAME,JOB_GROUP_NAME,JOB_REQUEST_PAYLOAD,INS_TS)"
			+ "VALUES (?,?,?,?,?)";
	private final String FETCH_BY_ID_QUERY = "SELECT JOB_ID,JOB_NAME,JOB_GROUP_NAME,JOB_REQUEST_PAYLOAD,INS_TS FROM JOB_DEFINITION"
			+ "WHERE JOB_ID= ?";
	private final String FETCH_ALL_QUERY = "SELECT JOB_ID,JOB_NAME,JOB_GROUP_NAME,JOB_REQUEST_PAYLOAD,INS_TS FROM JOB_DEFINITION";

	

	@Override
	public DaoName getDaoName() {
		return DaoName.JOB_DEFINITION;
	}

	@Override
	public JobDefinitionVO insert(JobDefinitionVO t) throws HttpJobSchedulerDaoException {	
		try{
			t.setJobId(Utils.getId());
			jdbcTemplate.update(INSERT_QUERY, t.getJobId(), t.getJobName(), t.getJobGroupName(), t.getJobRequestPayload(),
					new Date(System.currentTimeMillis()));
			return t;
		}catch(Exception e){
			throw new HttpJobSchedulerDaoException(e.getLocalizedMessage());
		}
		
	}

	@Override
	public JobDefinitionVO update(JobDefinitionVO t) throws HttpJobSchedulerDaoException {
		throw new UnSupportedDaoOperationException(
				"Job definition update is not permitted. Delete the current job and create a new job");
	}

	@Override
	public List<JobDefinitionVO> fetchAll() throws HttpJobSchedulerDaoException {
		try{
			return jdbcTemplate.queryForList(FETCH_ALL_QUERY, JobDefinitionVO.class);
		}catch(Exception e){
			throw new HttpJobSchedulerDaoException(e.getLocalizedMessage());
		}
	}

	@Override
	public JobDefinitionVO fetchById(String id) throws HttpJobSchedulerDaoException {
		try{
			return jdbcTemplate.queryForObject(FETCH_BY_ID_QUERY, new Object[] { id },
					new BeanPropertyRowMapper<JobDefinitionVO>(JobDefinitionVO.class));
		}catch(Exception e){
			throw new HttpJobSchedulerDaoException(e.getLocalizedMessage());
		}
}

}
