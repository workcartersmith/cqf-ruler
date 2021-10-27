package org.opencds.cqf.common.evaluation.entity;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.opencds.cqf.common.evaluation.model.MeasureEvalJobBatchJson;
import org.opencds.cqf.common.evaluation.model.MeasureEvalJobBatchStatusEnum;

@Entity
@Table(name = "CQL_MSR_EVAL_JOB_BATCH", indexes = {
	@Index(name = "IDX_MSR_EVAL_JOB_BATCH_JOBID", columnList = "JOB_PID")
})
public class MeasureEvalJobBatchEntity implements Serializable {

	public static final int MAX_DESCRIPTION_LENGTH = 500;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_MSR_EVAL_JOB_BATCH_PID")
	@SequenceGenerator(name = "SEQ_MSR_EVAL_JOB_BATCH_PID", sequenceName = "SEQ_MSR_EVAL_JOB_BATCH_PID")
	@Column(name = "PID")
	private Long myId;

	@ManyToOne
	@JoinColumn(name = "JOB_PID", referencedColumnName = "PID", nullable = false, foreignKey = @ForeignKey(name = "FK_MSR_EVAL_JOB_BATCH_JOB"))
	private MeasureEvalJobEntity myJob;

    @Version
	@Column(name = "OPTLOCK", nullable = false)
	private int myVersion;

    @Enumerated(EnumType.STRING)
	@Column(name = "BATCH_STATUS", length = 10, nullable = false)
	private MeasureEvalJobBatchStatusEnum myStatus;

	@Lob
	@Column(name = "SUBJECT_CONTENTS", nullable = false)
	private byte[] mySubjectContents;

    @Lob
	@Column(name = "RESULT_CONTENTS", nullable = true)
	private byte[] myResultContents;


	public MeasureEvalJobEntity getJob() {
		return myJob;
	}

	public void setJob(MeasureEvalJobEntity theJob) {
		myJob = theJob;
	}

    public MeasureEvalJobBatchStatusEnum getStatus() {
        return myStatus;
    }

    public void setStatus(MeasureEvalJobBatchStatusEnum theStatus) {
        myStatus = theStatus;
    }

    /**
     * @return a JSON array of subjectIds
     */
    public String getSubjectContents() {
		return new String(mySubjectContents, StandardCharsets.UTF_8);
	}

    /**
     * @param theSubjectContents a JSON array of subjectIds
     */
	public void setSubjectContents(String theSubjectContents) {
		mySubjectContents = theSubjectContents.getBytes(StandardCharsets.UTF_8);
	}

    /**
     * @return the MeasureReport generated for this batch
     */
    public String getResultContents() {
		return new String(myResultContents, StandardCharsets.UTF_8);
	}

    /**
     * @param theResultContents the MeasureReport generated for this batch
     */
	public void setResultContents(String theResultContents) {
		myResultContents = theResultContents.getBytes(StandardCharsets.UTF_8);
	}

    public MeasureEvalJobBatchJson toJson() {
        return new MeasureEvalJobBatchJson()
            .setJobId(this.getJob().getJobId())
            .setStatus(this.getStatus())
            .setSubjectContents(this.getSubjectContents())
            .setResultContents(this.getResultContents());
    }
}