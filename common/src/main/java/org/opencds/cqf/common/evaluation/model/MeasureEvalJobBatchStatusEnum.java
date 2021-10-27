package org.opencds.cqf.common.evaluation.model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum MeasureEvalJobBatchStatusEnum {
	STAGING,
	READY,
	RUNNING,
	COMPLETE,
	ERROR
}