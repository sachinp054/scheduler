/**
 * 
 */
package com.sacknibbles.sch.scheduler.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sacknibbles.sch.avro.model.JobRequestRecord;
import com.sacknibbles.sch.avro.model.JobStatus;
import com.sacknibbles.sch.avro.model.SchedulerType;
import com.sacknibbles.sch.dao.impl.JobDefinitionDao;
import com.sacknibbles.sch.dao.impl.JobDependencyDao;
import com.sacknibbles.sch.dao.impl.JobStatusDao;
import com.sacknibbles.sch.entity.DependentJob;
import com.sacknibbles.sch.entity.JobDefinitionVO;
import com.sacknibbles.sch.entity.JobDependencyVO;
import com.sacknibbles.sch.entity.JobStatusVO;
import com.sacknibbles.sch.factory.HttpJobSchedulerFactory;
import com.sacknibbles.sch.scheduler.exception.HttpJobSchedulerDaoException;

/**
 * @author Sachin
 *
 */
@Service
public class SchedulerHelper {

	@Autowired
	private HttpJobSchedulerFactory schFactory;
	@Autowired
	private JobDefinitionDao jobDefinitionDao;
	@Autowired
	private JobDependencyDao jobDependencyDao;
	@Autowired
	private JobStatusDao jobStatusDao;

	public boolean ifJobExists(SchedulerType schedulerType, JobKey jobKey) throws SchedulerException {
		Optional<Scheduler> scheduler = schFactory.getScheduler(schedulerType);
		if (scheduler.isPresent()) {
			return scheduler.get().checkExists(jobKey);
		}
		throw new SchedulerException("No Scheduler configured for type: " + schedulerType);
	}

	public String getNextFireTime(SchedulerType schedulerType, JobKey jobKey) throws SchedulerException {
		Optional<Scheduler> scheduler = schFactory.getScheduler(schedulerType);
		if (scheduler.isPresent()) {
			List<? extends Trigger> triggers = scheduler.get().getTriggersOfJob(jobKey);
			Date nextFireTime = null;
			if (!triggers.isEmpty()) {
				nextFireTime = triggers.get(0).getNextFireTime();
				return Objects.nonNull(nextFireTime) ? nextFireTime.toString() : "NA'";
			} else {
				return "NA";
			}
		}
		throw new SchedulerException("No Scheduler configured for type: " + schedulerType);
	}

	public void createJobDefinition(String jobId, String jobName, String jobGroupName, String payload) throws HttpJobSchedulerDaoException
			{
		JobDefinitionVO vo = new JobDefinitionVO();
		vo.setJobId(jobId);
		vo.setJobName(jobName);
		vo.setJobGroupName(jobGroupName);
		vo.setJobRequestPayload(payload);
		vo.setInstTs(new Date());
		jobDefinitionDao.insert(vo);
	}

	public void createJobDependency(JobRequestRecord jobRequest) throws HttpJobSchedulerDaoException {
		JobDependencyVO vo = new JobDependencyVO();
		List<String> mandatoryDependencies = Objects.nonNull(jobRequest.getMandatoryDependencies())
				? jobRequest.getMandatoryDependencies() : Collections.emptyList();
		List<DependentJob> list = new ArrayList<>();
		if (Objects.nonNull(jobRequest.getJobDependencies())) {
			for(String d: jobRequest.getJobDependencies()){
				DependentJob dJob = new DependentJob();
				dJob.setJobId(d);
				if(mandatoryDependencies.contains(d)){
					dJob.setManadatoryDependency(true);
				}else{
					dJob.setManadatoryDependency(false);
				}
				list.add(dJob);
			}
			vo.setDependentOn(list);
		}
		vo.setJobId(jobRequest.getJobId());
		jobDependencyDao.insert(vo);
	}

	public JobDependencyVO getJobDependencies(String jobId) throws HttpJobSchedulerDaoException{
		return jobDependencyDao.fetchById(jobId);	
	}
	
	public List<JobStatusVO> getJobStatusByIds(List<String> ids) throws HttpJobSchedulerDaoException{
		List<JobStatusVO> vos = jobStatusDao.fetchByJobIds(ids);
		return Objects.nonNull(vos)?vos:Collections.emptyList();
	}
	
	public void createJobStatusEntry(String jobId,JobStatus jobStatus, String errDesc) throws HttpJobSchedulerDaoException {
		JobStatusVO vo = new JobStatusVO();
		vo.setJobId(jobId);
		vo.setJobStatus(jobStatus);
		vo.setErrorDesc(errDesc);
		jobStatusDao.insert(vo);
	}
	
	public void updateJobStatus(JobStatusVO vo) throws HttpJobSchedulerDaoException{
		jobStatusDao.update(vo);
	}
	
	public JobStatusVO getJobStatusVo(String jobId) throws HttpJobSchedulerDaoException{
		return jobStatusDao.fetchById(jobId);
	}
	
}
