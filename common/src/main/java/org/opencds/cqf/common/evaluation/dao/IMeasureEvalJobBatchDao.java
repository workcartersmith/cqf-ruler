package org.opencds.cqf.common.evaluation.dao;

import java.util.Optional;

import org.opencds.cqf.common.evaluation.entity.MeasureEvalJobBatchEntity;
import org.opencds.cqf.common.evaluation.model.MeasureEvalJobBatchStatusEnum;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IMeasureEvalJobBatchDao extends JpaRepository<MeasureEvalJobBatchEntity, Long> {

	@Query("SELECT j FROM MeasureEvalJobBatchEntity j WHERE j.myJob.myJobId = :jobid")
	Optional<MeasureEvalJobBatchEntity> findByJobId(@Param("jobid") String theUuid);

	@Query("SELECT j FROM MeasureEvalJobBatchEntity j WHERE j.myStatus = :status")
	Slice<MeasureEvalJobBatchEntity> findByStatus(Pageable thePage, @Param("status") MeasureEvalJobBatchStatusEnum theStatus);
}