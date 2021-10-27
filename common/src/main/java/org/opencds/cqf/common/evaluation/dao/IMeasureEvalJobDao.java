package org.opencds.cqf.common.evaluation.dao;

import java.util.Optional;

import org.opencds.cqf.common.evaluation.entity.MeasureEvalJobEntity;
import org.opencds.cqf.common.evaluation.model.MeasureEvalJobStatusEnum;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IMeasureEvalJobDao extends JpaRepository<MeasureEvalJobEntity, Long> {

	@Query("SELECT j FROM MeasureEvalJobEntity j WHERE j.myJobId = :jobid")
	Optional<MeasureEvalJobEntity> findByJobId(@Param("jobid") String theUuid);

	@Query("SELECT j FROM MeasureEvalJobEntity j WHERE j.myStatus = :status")
	Slice<MeasureEvalJobEntity> findByStatus(Pageable thePage, @Param("status") MeasureEvalJobStatusEnum theStatus);
}