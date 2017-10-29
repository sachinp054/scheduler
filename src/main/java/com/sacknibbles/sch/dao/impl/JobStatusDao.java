/**
 * 
 */
package com.sacknibbles.sch.dao.impl;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.sacknibbles.sch.constants.DaoName;
import com.sacknibbles.sch.dao.Dao;
import com.sacknibbles.sch.entity.JobStatusVO;

/**
 * @author Sachin
 *
 */
@Service
public class JobStatusDao implements Dao<JobStatusVO>{

	private final String INSERT_QUERY = "INSERT INTO JOB_STATUS (JOB_ID,JOB_STATUS,JOB_START_TIME,JOB_COMPLETION_TIME,ERROR_DESC)"
			+ " VALUES(?,?,?,?,?)";
	private final String UPDATE_QUERY = "UPDATE JOB_STATUS SET JOB_STATUS=?, JOB_COMPLETION_TIME=?,"
			+ "JOB_END_TIME=?,ERROR_DESC=? WHERE JOB_ID=?";
	private final String FETCH_ALL_QUERY = " SELECT JOB_ID,JOB_STATUS,JOB_START_TIME,JOB_COMPLETION_TIME,ERROR_DESC FROM"
			+ " JOB_STATUS ";
	private final String FETCH_BY_ID_QUERY = FETCH_ALL_QUERY+" WHERE JOB_ID =?";
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public JobStatusVO insert(JobStatusVO t) throws Exception {
		Objects.requireNonNull(t);
		jdbcTemplate.update(INSERT_QUERY,t.getJobId(),t.getJobStatus().name(),t.getJobStartTime(),t.getJobCompletionTime(),t.getErrorDesc());
		return t;
	}

	@Override
	public JobStatusVO update(JobStatusVO t) throws Exception {
		jdbcTemplate.update(UPDATE_QUERY,t.getJobStatus().name(),t.getJobStartTime(),t.getJobCompletionTime(),t.getErrorDesc(),t.getJobId());
		return t;
	}

	@Override
	public List<JobStatusVO> fetchAll() throws Exception {
		return jdbcTemplate.queryForList(FETCH_ALL_QUERY,JobStatusVO.class);
	}

	@Override
	public JobStatusVO fetchById(String id) throws Exception {
		return jdbcTemplate.queryForObject(FETCH_BY_ID_QUERY, new Object[]{id}, JobStatusVO.class);
	}

	@Override
	public DaoName getDaoName() {
		return DaoName.JOB_STATUS;
	}

}
