/**
 * 
 */
package com.sacknibbles.sch.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import com.sacknibbles.sch.constants.DaoName;
import com.sacknibbles.sch.dao.Dao;
import com.sacknibbles.sch.entity.DependentJob;
import com.sacknibbles.sch.entity.JobDependencyVO;
import com.sacknibbles.sch.scheduler.exception.UnSupportedOperationException;

/**
 * @author Sachin
 *
 */
@Service
public class JobDependencyDao implements Dao<JobDependencyVO> {


	private final String INSERT_QUERY = "INSERT INTO JOB_DEPENDENCY (JOB_ID,DEPENDENT_JOB_ID,IS_MANDATORY)"
			+ " VALUES (?,?,?)";
	private final String FETCH_ALL_QUERY = "SELECT JOB_ID,DEPENDENT_JOB_ID,IS_MANDATORY FROM JOB_DEPENDENCY";

	private final String FETCH_BY_ID_QUERY = FETCH_ALL_QUERY + " WHERE JOB_ID = ?'";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public JobDependencyVO insert(JobDependencyVO t) throws Exception {
		Objects.requireNonNull(t);
		List<DependentJob> dependentOn = t.getDependentOn();
		if (Objects.nonNull(dependentOn)) {
			batchInsert(t, dependentOn);
		} else {
			jdbcTemplate.update(INSERT_QUERY, t.getJobId(), null, null);
		}
		return t;
	}

	@Override
	public JobDependencyVO update(JobDependencyVO t) throws Exception {
		throw new UnSupportedOperationException("Update on dependency is not permitted!!");
	}

	@Override
	public List<JobDependencyVO> fetchAll() throws Exception {
		List<JobDependencyVoTemp> result = jdbcTemplate.query(FETCH_ALL_QUERY, new JobDependencyRowExtractor());	
		return createJobDependencyVO(result);
	}

	@Override
	public JobDependencyVO fetchById(String id) throws Exception {
		List<JobDependencyVoTemp> result = jdbcTemplate.query(FETCH_BY_ID_QUERY, new Object[]{id},  new JobDependencyRowExtractor());	
		return createJobDependencyVO(result).get(0);
	}

	@Override
	public DaoName getDaoName() {
		return DaoName.JOB_DEPENDENCY;
	}


	/**
	 * @param result
	 * @return
	 */
	private List<JobDependencyVO> createJobDependencyVO(List<JobDependencyVoTemp> result) {
		List<JobDependencyVO> jobDependencyVoList = new ArrayList<>();
		if (!result.isEmpty()) {
			Map<String, List<JobDependencyVoTemp>> map = result.stream()
					.collect(Collectors.groupingBy(JobDependencyVoTemp::getJobId));
			
			map.forEach((k,v)->{
				JobDependencyVO jobDependencyVO = new JobDependencyVO();
				jobDependencyVO.setJobId(k);
				v.forEach(value->{
					DependentJob dependentJob = new DependentJob();
					dependentJob.setJobId(value.getDependentJobId());
					dependentJob.setManadatoryDependency(value.isManDatory());
				});
				jobDependencyVoList.add(jobDependencyVO);
			});
		}
		return jobDependencyVoList;
	}

	/**
	 * @param t
	 * @param dependentOn
	 */
	private void batchInsert(JobDependencyVO t, List<DependentJob> dependentOn) {
		jdbcTemplate.batchUpdate(INSERT_QUERY, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				DependentJob dependentJob = dependentOn.get(i);
				ps.setString(1, t.getJobId());
				ps.setString(2, dependentJob.getJobId());
				ps.setBoolean(3, dependentJob.isManadatoryDependency());
			}

			@Override
			public int getBatchSize() {
				return dependentOn.size();
			}
		});
	}

	/**
	 * @author Sachin
	 *
	 */
	private final class JobDependencyVoTemp {
		private String jobId;
		private String dependentJobId;
		private boolean isManDatory;

		/**
		 * @param jobId
		 * @param dependentJobId
		 * @param isManDatory
		 */
		public JobDependencyVoTemp(String jobId, String dependentJobId, boolean isManDatory) {
			super();
			this.jobId = jobId;
			this.dependentJobId = dependentJobId;
			this.isManDatory = isManDatory;
		}

		/**
		 * @return the jobId
		 */
		public String getJobId() {
			return jobId;
		}

		/**
		 * @return the dependentJobId
		 */
		public String getDependentJobId() {
			return dependentJobId;
		}

		/**
		 * @return the isManDatory
		 */
		public boolean isManDatory() {
			return isManDatory;
		}

	}

	private final class JobDependencyRowExtractor implements ResultSetExtractor<List<JobDependencyVoTemp>> {
		List<JobDependencyVoTemp> list = new ArrayList<>();

		@Override
		public List<JobDependencyVoTemp> extractData(ResultSet rs) throws SQLException, DataAccessException {
			list.add(new JobDependencyVoTemp(rs.getString(1), rs.getString(2), rs.getBoolean(3)));
			return list;
		}

	}


}
