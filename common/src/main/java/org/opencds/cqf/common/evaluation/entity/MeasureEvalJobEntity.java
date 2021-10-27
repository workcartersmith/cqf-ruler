package org.opencds.cqf.common.evaluation.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.opencds.cqf.common.evaluation.model.MeasureEvalJobJson;
import org.opencds.cqf.common.evaluation.model.MeasureEvalJobStatusEnum;

import static org.apache.commons.lang3.StringUtils.left;

@Entity
@Table(name = "CQL_MSR_EVAL_JOB", uniqueConstraints = {
	@UniqueConstraint(name = "IDX_MSR_EVAL_JOB_ID", columnNames = "JOB_ID")
})
public class MeasureEvalJobEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_MSR_EVAL_JOB_PID")
	@SequenceGenerator(name = "SEQ_MSR_EVAL_JOB_PID", sequenceName = "SEQ_MSR_EVAL_JOB_PID")
	@Column(name = "PID")
	private Long myId;

	@Version
	@Column(name = "OPTLOCK", nullable = false)
	private int myVersion;

	@Column(name = "JOB_ID", length = 36, nullable = false, updatable = false)
	private String myJobId;

    @Column(name = "SUBJECT_COUNT", nullable = false)
	private int mySubjectCount;

    @Column(name = "BATCH_COUNT", nullable = false)
	private int myBatchCount;

    @Column(name = "BATCH_SIZE", nullable = false)
	private int myBatchSize;

	@Enumerated(EnumType.STRING)
	@Column(name = "JOB_STATUS", length = 10, nullable = false)
	private MeasureEvalJobStatusEnum myStatus;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "STATUS_TIME", nullable = false)
	private Date myStatusTime;

	@Column(name = "STATUS_MESSAGE", nullable = true, length = 500)
	private String myStatusMessage;

	@Column(name = "MEASURE_ID",  length = 200, nullable = false)
	private String myMeasureId;

	@Column(name = "REPORT_TYPE",  length = 10, nullable = false)
	private String myReportType;

	@Column(name = "SUBJECT",  length = 50, nullable = true)
	private String mySubject;

	@Column(name = "PERIOD_START",  length = 50, nullable = true)
	private String myPeriodStart;

	@Column(name = "PERIOD_END",  length = 50, nullable = true)
	private String myPeriodEnd;

	@Column(name = "MEASURE_REPORT_ID",  length = 36, nullable = false)
	private String myMeasureReportId;

	public String getJobId() {
		return myJobId;
	}

	public void setJobId(String theJobId) {
		myJobId = theJobId;
	}

	public int getSubjectCount() {
		return mySubjectCount;
	}

	public void setSubjectCount(int theSubjectCount) {
		mySubjectCount = theSubjectCount;
	}

	public int getBatchCount() {
		return myBatchCount;
	}

	public void setBatchCount(int theBatchCount) {
		myBatchCount = theBatchCount;
	}

	public int getBatchSize() {
		return myBatchSize;
	}

	public void setBatchSize(int theBatchSize) {
		myBatchSize = theBatchSize;
	}


	public Date getStatusTime() {
		return myStatusTime;
	}

	public void setStatusTime(Date theStatusTime) {
		myStatusTime = theStatusTime;
	}

	public MeasureEvalJobStatusEnum getStatus() {
		return myStatus;
	}

	/**
	 * Sets the status, updates the status time, and clears the status message
	 */
	public void setStatus(MeasureEvalJobStatusEnum theStatus) {
		if (myStatus != theStatus) {
			myStatus = theStatus;
			setStatusTime(new Date());
			setStatusMessage(null);
		}
	}
	public String getStatusMessage() {
		return myStatusMessage;
	}

	public void setStatusMessage(String theStatusMessage) {
		myStatusMessage = left(theStatusMessage, 500);
	}

	public String getMeasureId() {
		return myMeasureId;
	}

	public void setMeasureId(String theMeasureId) {
		this.myMeasureId = theMeasureId;
	}

	public String getReportType() {
		return myReportType;
	}

	public void setReportType(String theReportType) {
		myReportType = theReportType;
	}

	public String getSubject() {
		return mySubject;
	}

	public void setSubject(String theSubject) {
		mySubject = theSubject;
	}

	public String getPeriodStart() {
		return myPeriodStart;
	}

	public void setPeriodStart(String thePeriodStart) {
		myPeriodStart = thePeriodStart;
	}

	public String getPeriodEnd() {
		return myPeriodEnd;
	}

	public void setPeriodEnd(String thePeriodEnd) {
		myPeriodEnd = thePeriodEnd;
	}

	public String getMeasureReportId() {
		return myMeasureReportId;
	}

	public void setMeasureReportId(String theMeasureReportId) {
		myMeasureReportId = theMeasureReportId;
	}

	public MeasureEvalJobJson toJson() {
		return new MeasureEvalJobJson()
		.setJobId(this.getJobId())
		.setStatus(this.getStatus())
		.setStatusMessage(this.getStatusMessage())
		.setStatusTime(this.getStatusTime())
		.setSubjectCount(this.getSubjectCount())
		.setBatchCount(this.getBatchCount())
		.setBatchSize(this.getBatchSize())
		.setMeasureId(this.getMeasureId())
		.setSubject(this.getSubject())
		.setReportType(this.getReportType())
		.setPeriodStart(this.getPeriodStart())
		.setPeriodEnd(this.getPeriodEnd())
		.setMeasureReportId(this.getMeasureReportId());
	}
}
