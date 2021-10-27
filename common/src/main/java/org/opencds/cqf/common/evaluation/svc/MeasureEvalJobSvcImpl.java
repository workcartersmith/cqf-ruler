package org.opencds.cqf.common.evaluation.svc;

import ca.uhn.fhir.jpa.api.config.DaoConfig;
import ca.uhn.fhir.jpa.batch.BatchJobsConfig;
import ca.uhn.fhir.jpa.batch.api.IBatchJobSubmitter;
import ca.uhn.fhir.jpa.batch.log.Logs;
import ca.uhn.fhir.jpa.model.sched.HapiJob;
import ca.uhn.fhir.jpa.model.sched.ISchedulerService;
import ca.uhn.fhir.jpa.model.sched.ScheduledJobDefinition;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.util.ValidateUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.opencds.cqf.common.evaluation.dao.IMeasureEvalJobBatchDao;
import org.opencds.cqf.common.evaluation.dao.IMeasureEvalJobDao;
import org.opencds.cqf.common.evaluation.entity.MeasureEvalJobBatchEntity;
import org.opencds.cqf.common.evaluation.entity.MeasureEvalJobEntity;
import org.opencds.cqf.common.evaluation.model.MeasureEvalJobBatchJson;
import org.opencds.cqf.common.evaluation.model.MeasureEvalJobJson;
import org.opencds.cqf.common.evaluation.model.MeasureEvalJobStatusEnum;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class MeasureEvalJobSvcImpl implements IMeasureEvalJobSvc {

	private static final Logger ourLog = LoggerFactory.getLogger(MeasureEvalJobSvcImpl.class);
	private final Semaphore myRunningJobSemaphore = new Semaphore(1);
	@Autowired
	private IMeasureEvalJobDao myJobDao;
	@Autowired
	private IMeasureEvalJobBatchDao myJobBatchDao;
	@Autowired
	private PlatformTransactionManager myTxManager;
	private TransactionTemplate myTxTemplate;
	@Autowired
	private ISchedulerService mySchedulerService;
	@Autowired
	private IBatchJobSubmitter myJobSubmitter;
	@Autowired
	@Qualifier(BatchJobsConfig.BULK_IMPORT_JOB_NAME)
	private org.springframework.batch.core.Job myBulkImportJob;
	@Autowired
	private DaoConfig myDaoConfig;

	@PostConstruct
	public void start() {
		myTxTemplate = new TransactionTemplate(myTxManager);

		// This job should be local so that each node in the cluster can pick up jobs
		ScheduledJobDefinition jobDetail = new ScheduledJobDefinition();
		jobDetail.setId(ActivationJob.class.getName());
		jobDetail.setJobClass(ActivationJob.class);
		mySchedulerService.scheduleLocalJob(2 * DateUtils.MILLIS_PER_SECOND, jobDetail);
	}

	@Override
	@Transactional
	public String createNewMeasureEvalJob(MeasureEvalJobJson theJobDescription, List<MeasureEvalJobBatchJson> theBatchDefinitions) {
		ValidateUtil.isNotNullOrThrowUnprocessableEntity(theJobDescription, "Job must not be null");
		ValidateUtil.isNotNullOrThrowUnprocessableEntity(theBatchDefinitions, "The batches must not be null");

		String jobId = UUID.randomUUID().toString();

		ourLog.info("Creating new Measure Eval Job with {} batches, assigning job ID: {}", theBatchDefinitions.size(), jobId);

		// BulkImportJobEntity job = new BulkImportJobEntity();
		// job.setJobId(jobId);
		// job.setFileCount(theInitialFiles.size());
		// job.setStatus(BulkImportJobStatusEnum.STAGING);
		// job.setJobDescription(theJobDescription.getJobDescription());
		// job.setBatchSize(theJobDescription.getBatchSize());
		// job.setRowProcessingMode(theJobDescription.getProcessingMode());
		// job = myJobDao.save(job);

		// int nextSequence = 0;
		// addFilesToJob(theInitialFiles, job, nextSequence);

		return jobId;
	}

	@Override
	@Transactional
	public void markJobAsReadyForActivation(String theJobId) {
		// ourLog.info("Activating bulk import job {}", theJobId);

		// BulkImportJobEntity job = findJobByJobId(theJobId);
		// ValidateUtil.isTrueOrThrowInvalidRequest(job.getStatus() == BulkImportJobStatusEnum.STAGING, "Bulk import job %s can not be activated in status: %s", theJobId, job.getStatus());

		// job.setStatus(BulkImportJobStatusEnum.READY);
		// myJobDao.save(job);
	}

		/**
	 * To be called by the job scheduler
	 */
	@Transactional(value = Transactional.TxType.NEVER)
	@Override
	public boolean activateNextReadyBatch() {
		if (!myRunningJobSemaphore.tryAcquire()) {
			Logs.getBatchTroubleshootingLog().trace("Already have a running measure eval job batch, not going to check for more");
			return false;
		}

		try {
			return doActivateNextReadyBatch();
		} finally {
			myRunningJobSemaphore.release();
		}
	}

	private boolean doActivateNextReadyBatch() {
		return false;
	}


	/**
	 * To be called by the job scheduler
	 */
	@Transactional(value = Transactional.TxType.NEVER)
	@Override
	public boolean activateNextReadyJob() {
		if (!myRunningJobSemaphore.tryAcquire()) {
			Logs.getBatchTroubleshootingLog().trace("Already have a running measure eval job, not going to check for more");
			return false;
		}

		try {
			return doActivateNextReadyJob();
		} finally {
			myRunningJobSemaphore.release();
		}
	}

	private boolean doActivateNextReadyJob() {
		return false;
		// Optional<BulkImportJobEntity> jobToProcessOpt = Objects.requireNonNull(myTxTemplate.execute(t -> {
		// 	Pageable page = PageRequest.of(0, 1);
		// 	Slice<BulkImportJobEntity> submittedJobs = myJobDao.findByStatus(page, BulkImportJobStatusEnum.READY);
		// 	if (submittedJobs.isEmpty()) {
		// 		return Optional.empty();
		// 	}
		// 	return Optional.of(submittedJobs.getContent().get(0));
		// }));

		// if (!jobToProcessOpt.isPresent()) {
		// 	return false;
		// }

		// BulkImportJobEntity bulkImportJobEntity = jobToProcessOpt.get();

		// String jobUuid = bulkImportJobEntity.getJobId();
		// try {
		// 	processJob(bulkImportJobEntity);
		// } catch (Exception e) {
		// 	ourLog.error("Failure while preparing bulk export extract", e);
		// 	myTxTemplate.execute(t -> {
		// 		Optional<BulkImportJobEntity> submittedJobs = myJobDao.findByJobId(jobUuid);
		// 		if (submittedJobs.isPresent()) {
		// 			BulkImportJobEntity jobEntity = submittedJobs.get();
		// 			jobEntity.setStatus(BulkImportJobStatusEnum.ERROR);
		// 			jobEntity.setStatusMessage(e.getMessage());
		// 			myJobDao.save(jobEntity);
		// 		}
		// 		return false;
		// 	});
		// }

		// return true;
	}

	// @Transactional
	// public void setJobToStatus(String theJobId, MeasureEvalJobStatusEnum theStatus, String theStatusMessage) {
	// 	MeasureEvalJobEntity job = findJobByJobId(theJobId);
	// 	job.setStatus(theStatus);
	// 	job.setStatusMessage(theStatusMessage);
	// 	myJobDao.save(job);
	// }

	// @Transactional
	// public void setBatchToStatus(String theBatchId, MeasureEvalJobStatusEnum theStatus) {
	// 	BulkImportJobEntity job = findJobByJobId(theJobId);
	// 	job.setStatus(theStatus);
	// 	job.setStatusMessage(theStatusMessage);
	// 	myJobDao.save(job);
	// }

	// @Override
	// @Transactional
	// public MeasureEvalJobJson fetchJob(String theJobId) {
	// 	MeasureEvalJobEntity job = findJobByJobId(theJobId);
	// 	return job.toJson();
	// }

	// @Transactional
	// @Override
	// public MeasureEvalJobBatchJson fetchBatch(String theBatchId) {
	// 	MeasureEvalJobBatchEntity batch = findBatchByBatchId(theBatchId);
	// 	return batch.toJson();
	// }

	// @Transactional
	// @Override
	// public String getFileDescription(String theJobId, int theFileIndex) {
	// 	BulkImportJobEntity job = findJobByJobId(theJobId);

	// 	return myJobFileDao.findFileDescriptionForJob(job, theFileIndex).orElse("");
	// }

	// @Override
	// @Transactional
	// public void deleteJobFiles(String theJobId) {
	// 	BulkImportJobEntity job = findJobByJobId(theJobId);
	// 	List<Long> files = myJobFileDao.findAllIdsForJob(theJobId);
	// 	for (Long next : files) {
	// 		myJobFileDao.deleteById(next);
	// 	}
	// 	myJobDao.delete(job);
	// }

	// private void processJob(BulkImportJobEntity theBulkExportJobEntity) throws JobParametersInvalidException {
	// 	String jobId = theBulkExportJobEntity.getJobId();
	// 	int batchSize = theBulkExportJobEntity.getBatchSize();
	// 	ValidateUtil.isTrueOrThrowInvalidRequest(batchSize > 0, "Batch size must be positive");

	// 	JobParametersBuilder parameters = new JobParametersBuilder()
	// 		.addString(BulkExportJobConfig.JOB_UUID_PARAMETER, jobId)
	// 		.addLong(BulkImportJobConfig.JOB_PARAM_COMMIT_INTERVAL, (long) batchSize);

	// 	if (isNotBlank(theBulkExportJobEntity.getJobDescription())) {
	// 		parameters.addString(BulkExportJobConfig.JOB_DESCRIPTION, theBulkExportJobEntity.getJobDescription());
	// 	}

	// 	ourLog.info("Submitting bulk import job {} to job scheduler", jobId);

	// 	myJobSubmitter.runJob(myBulkImportJob, parameters.toJobParameters());
	// }

	public static class ActivationJob implements HapiJob {
		@Autowired
		private IMeasureEvalJobSvc myTarget;

		@Override
		public void execute(JobExecutionContext theContext) {
			Boolean result = myTarget.activateNextReadyJob();
			if (!result) {
				myTarget.activateNextReadyBatch();
			}
		}
	}

	@Override
	public MeasureEvalJobJson fetchJob(String theJobId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MeasureEvalJobBatchJson fetchBatch(String theBatchId) {
		// TODO Auto-generated method stub
		return null;
	}


}