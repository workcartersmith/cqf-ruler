package org.opencds.cqf.common.evaluation.svc;

import java.util.List;

import org.opencds.cqf.common.evaluation.model.MeasureEvalJobBatchJson;
import org.opencds.cqf.common.evaluation.model.MeasureEvalJobJson;

public interface IMeasureEvalJobSvc {
    String createNewMeasureEvalJob(MeasureEvalJobJson thejobDefinition, List<MeasureEvalJobBatchJson> theBatchDefinitions);

    void markJobAsReadyForActivation(String theJobId);

    boolean activateNextReadyBatch();

    boolean activateNextReadyJob();

    MeasureEvalJobJson fetchJob(String theJobId);

    MeasureEvalJobBatchJson fetchBatch(String theBatchId);
}
