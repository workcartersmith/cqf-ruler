package org.opencds.cqf.common.evaluation.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import ca.uhn.fhir.model.api.IModelJson;

public class MeasureEvalJobJson implements IModelJson {

    @JsonProperty("jobId")
	private String myJobId;

    @JsonProperty("subjectCount")
	private int mySubjectCount;

    @JsonProperty("batchCount")
	private int myBatchCount;

    @JsonProperty("batchSize")
	private int myBatchSize;

    @JsonProperty("status")
	private MeasureEvalJobStatusEnum myStatus;

    @JsonProperty("statusTime")
	private Date myStatusTime;

    @JsonProperty("statusMessage")
	private String myStatusMessage;

    @JsonProperty("messageId")
	private String myMeasureId;

    @JsonProperty("reportType")
	private String myReportType;

    @JsonProperty("subject")
	private String mySubject;

    @JsonProperty("periodStart")
	private String myPeriodStart;

    @JsonProperty("periodEnd")
	private String myPeriodEnd;

    @JsonProperty("measureReportId")
	private String myMeasureReportId;

	public String getJobId() {
		return myJobId;
	}

	public MeasureEvalJobJson setJobId(String theJobId) {
		myJobId = theJobId;
        return this;
	}

	public int getSubjectCount() {
		return mySubjectCount;
	}

	public MeasureEvalJobJson setSubjectCount(int theSubjectCount) {
		mySubjectCount = theSubjectCount;
        return this;
	}

	public int getBatchCount() {
		return myBatchCount;
	}

	public MeasureEvalJobJson setBatchCount(int theBatchCount) {
		myBatchCount = theBatchCount;
        return this;
	}

	public int getBatchSize() {
		return myBatchSize;
	}

	public MeasureEvalJobJson setBatchSize(int theBatchSize) {
		myBatchSize = theBatchSize;
        return this;
	}


	public Date getStatusTime() {
		return myStatusTime;
	}

	public MeasureEvalJobJson setStatusTime(Date theStatusTime) {
		myStatusTime = theStatusTime;
        return this;
	}

	public MeasureEvalJobStatusEnum getStatus() {
		return myStatus;
	}

	public MeasureEvalJobJson setStatus(MeasureEvalJobStatusEnum theStatus) {
        myStatus = theStatus;
        return this;
	}
	public String getStatusMessage() {
		return myStatusMessage;
	}

	public MeasureEvalJobJson setStatusMessage(String theStatusMessage) {
		myStatusMessage = theStatusMessage;
        return this;
	}

	public String getMeasureId() {
		return myMeasureId;
	}

	public MeasureEvalJobJson setMeasureId(String theMeasureId) {
		this.myMeasureId = theMeasureId;
        return this;
	}

	public String getReportType() {
		return myReportType;
	}

	public MeasureEvalJobJson setReportType(String theReportType) {
		myReportType = theReportType;
        return this;
	}

	public String getSubject() {
		return mySubject;
	}

	public MeasureEvalJobJson setSubject(String theSubject) {
		mySubject = theSubject;
        return this;
	}

	public String getPeriodStart() {
		return myPeriodStart;
	}

	public MeasureEvalJobJson setPeriodStart(String thePeriodStart) {
		myPeriodStart = thePeriodStart;
        return this;
	}

	public String getPeriodEnd() {
		return myPeriodEnd;
	}

	public MeasureEvalJobJson setPeriodEnd(String thePeriodEnd) {
		myPeriodEnd = thePeriodEnd;
        return this;
	}

	public String getMeasureReportId() {
		return myMeasureReportId;
	}

	public MeasureEvalJobJson setMeasureReportId(String theMeasureReportId) {
		myMeasureReportId = theMeasureReportId;
        return this;
	}
}
