/**
 * 
 */
package com.sacknibbles.sch.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.sacknibbles.sch.avro.model.JobStatus;
import com.sacknibbles.sch.constants.DaoName;
import com.sacknibbles.sch.dao.Dao;
import com.sacknibbles.sch.entity.JobStatusVO;
import com.sacknibbles.sch.scheduler.exception.HttpJobSchedulerDaoException;

/**
 * @author Sachin
 *
 */
@Service
public class JobStatusDao implements Dao<JobStatusVO> {

	
	/**
	 * @author Sachin
	 *
	 */
	private final class JobStatusResultExtractor implements ResultSetExtractor<List<JobStatusVO>> {
		@Override
		public List<JobStatusVO> extractData(ResultSet rs) throws SQLException, DataAccessException {
			List<JobStatusVO> vos = new ArrayList<>();
			while(rs.next()){
				JobStatusVO vo = new JobStatusVO();
				vo.setJobId(rs.getString("jobId"));
				vo.setJobStartTime(rs.getDate("jobStartTime"));
				vo.setJobStatus(JobStatus.valueOf(rs.getString("jobStatus")));
				vo.setJobCompletionTime(rs.getDate("jobCompletionTime"));
				vo.setErrorDesc(rs.getString("errorDesc"));
				vos.add(vo);
			}					
			return vos;
		}
	}

	private final String INSERT_QUERY = "INSERT INTO JOB_STATUS (JOB_ID,JOB_STATUS,JOB_START_TIME,JOB_COMPLETION_TIME,ERROR_DESC)"
			+ " VALUES(?,?,?,?,?)";
	private final String UPDATE_QUERY = "UPDATE JOB_STATUS SET JOB_STATUS=?, JOB_COMPLETION_TIME=?,"
			+ "JOB_START_TIME=?,ERROR_DESC=? WHERE JOB_ID=?";
	private final static String FETCH_ALL_QUERY = " SELECT JOB_ID as jobId,JOB_STATUS as jobStatus,JOB_START_TIME as jobStartTime,"
			+ "JOB_COMPLETION_TIME as jobCompletionTime,ERROR_DESC as errorDesc FROM" + " JOB_STATUS ";
	private final String FETCH_BY_ID_QUERY = FETCH_ALL_QUERY + " WHERE JOB_ID =?";
	private static final String FETCH_BY_ID_QUERY_AND_NAME = FETCH_ALL_QUERY + " WHERE JOB_ID IN (:jobIds)";
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private NamedParameterJdbcTemplate namedJdbcParameterTemplate;

	@Override
	public JobStatusVO insert(JobStatusVO t) throws HttpJobSchedulerDaoException {
		try {
			Objects.requireNonNull(t);
			jdbcTemplate.update(INSERT_QUERY, t.getJobId(), t.getJobStatus().name(), t.getJobStartTime(),
					t.getJobCompletionTime(), t.getErrorDesc());
			return t;
		} catch (Exception e) {
			throw new HttpJobSchedulerDaoException(e);
		}
	}

	@Override
	public JobStatusVO update(JobStatusVO t) throws HttpJobSchedulerDaoException {
		try {
			jdbcTemplate.update(UPDATE_QUERY, t.getJobStatus().name(), t.getJobStartTime(), t.getJobCompletionTime(),
					t.getErrorDesc(), t.getJobId());
			return t;
		} catch (Exception e) {
			throw new HttpJobSchedulerDaoException(e);
		}
	}

	@Override
	public List<JobStatusVO> fetchAll() throws HttpJobSchedulerDaoException {
		try {
			return jdbcTemplate.query(FETCH_ALL_QUERY, new JobStatusResultExtractor());
		} catch (Exception e) {
			throw new HttpJobSchedulerDaoException(e);
		}
	}

	@Override
	public JobStatusVO fetchById(String id) throws HttpJobSchedulerDaoException {
		try {
			return jdbcTemplate.queryForObject(FETCH_BY_ID_QUERY, new Object[] { id },
					new BeanPropertyRowMapper<JobStatusVO>(JobStatusVO.class));
		} catch (Exception e) {
			throw new HttpJobSchedulerDaoException(e);
		}
	}
	
	
	public List<JobStatusVO> fetchByJobIds(List<String> ids) throws HttpJobSchedulerDaoException {
		try {
			Map<String,Object> paramMap = new HashMap<>();
			paramMap.put("jobIds", ids);
			return namedJdbcParameterTemplate.query(FETCH_BY_ID_QUERY_AND_NAME, paramMap,new JobStatusResultExtractor());
			
		} catch (Exception e) {
			throw new HttpJobSchedulerDaoException(e);
		}
	}

	@Override
	public DaoName getDaoName() {
		return DaoName.JOB_STATUS;
	}

}
