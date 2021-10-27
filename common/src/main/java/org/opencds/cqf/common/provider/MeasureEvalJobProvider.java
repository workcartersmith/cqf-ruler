package org.opencds.cqf.common.provider;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.opencds.cqf.common.evaluation.dao.IMeasureEvalJobBatchDao;
import org.opencds.cqf.common.evaluation.dao.IMeasureEvalJobDao;
import org.opencds.cqf.common.evaluation.entity.MeasureEvalJobEntity;
import org.opencds.cqf.common.evaluation.entity.MeasureEvalJobBatchEntity;
import org.opencds.cqf.common.evaluation.model.MeasureEvalJobBatchJson;
import org.opencds.cqf.common.evaluation.model.MeasureEvalJobJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.server.servlet.ServletRequestDetails;
import ca.uhn.fhir.util.JsonUtil;

@Component
public class MeasureEvalJobProvider {

    @Autowired
    private IMeasureEvalJobDao myMeasureEvalJobDao;

    @Autowired
    private IMeasureEvalJobBatchDao myMeasureEvalJobBatchDao;

    @Autowired
    private FhirContext myFhirContext;

    @Operation(name = "$measure-job-status", manualResponse = true, idempotent = true)
	public void measureJobStatus(ServletRequestDetails theRequestDetails) throws IOException {

		HttpServletResponse response = theRequestDetails.getServletResponse();
		theRequestDetails.getServer().addHeadersToResponse(response);

        List<MeasureEvalJobEntity> measureEvalJobEntities = myMeasureEvalJobDao.findAll();

        response.setStatus(Constants.STATUS_HTTP_200_OK);
		response.setContentType(Constants.CT_JSON);

        List<MeasureEvalJobJson> jsons = measureEvalJobEntities.stream().map(x -> x.toJson()).collect(Collectors.toList());

        JsonUtil.serialize(jsons, response.getWriter());
        response.getWriter().close();
	}

    @Operation(name = "$measure-batch-status", manualResponse = true, idempotent = true)
	public void measureBatchStatus(ServletRequestDetails theRequestDetails) throws IOException {

		HttpServletResponse response = theRequestDetails.getServletResponse();
		theRequestDetails.getServer().addHeadersToResponse(response);

        List<MeasureEvalJobBatchEntity> measureEvalJobBatchEntities = myMeasureEvalJobBatchDao.findAll();

        response.setStatus(Constants.STATUS_HTTP_200_OK);
		response.setContentType(Constants.CT_JSON);

        List<MeasureEvalJobBatchJson> jsons = measureEvalJobBatchEntities.stream().map(x -> x.toJson()).collect(Collectors.toList());

        JsonUtil.serialize(jsons, response.getWriter());
        response.getWriter().close();
	} 
}
