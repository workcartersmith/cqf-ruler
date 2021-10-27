package org.opencds.cqf.common.evaluation.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import ca.uhn.fhir.model.api.IModelJson;

public class MeasureEvalJobBatchJson implements IModelJson {

    @JsonProperty("jobId")
	private String myJobId;

    @JsonProperty("status")
	private MeasureEvalJobBatchStatusEnum myStatus;

    @JsonProperty("subjectContents")
	private String mySubjectContents;

	@JsonProperty("resultContents")
	private String myResultContents;

	public String getJobId() {
		return myJobId;
	}

	public MeasureEvalJobBatchJson setJobId(String theJobId) {
		myJobId = theJobId;
        return this;
	}

	public MeasureEvalJobBatchStatusEnum getStatus() {
		return myStatus;
	}

	public MeasureEvalJobBatchJson setStatus(MeasureEvalJobBatchStatusEnum theStatus) {
        myStatus = theStatus;
        return this;
	}

	public String getSubjectContents() {
		return mySubjectContents;
	}

	public MeasureEvalJobBatchJson setSubjectContents(String theSubjectContents) {
		mySubjectContents = theSubjectContents;
		return this;
	}

	public String getResultContents() {
		return myResultContents;
	}

	public MeasureEvalJobBatchJson setResultContents(String theResultContents) {
		myResultContents = theResultContents;
		return this;
	}
}
