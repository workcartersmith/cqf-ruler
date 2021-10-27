package org.opencds.cqf.common.evaluation.model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum MeasureEvalJobStatusEnum {
	STAGING,
	READY,
	RUNNING,
	COMPLETE,
	ERROR
}