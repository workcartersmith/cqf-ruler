package org.opencds.cqf.qdm.fivepoint4.repository;

import org.opencds.cqf.qdm.fivepoint4.model.PositiveDeviceOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.Optional;

@Repository
public interface PositiveDeviceOrderRepository extends JpaRepository<PositiveDeviceOrder, String>
{
    @Nonnull
    Optional<PositiveDeviceOrder> findBySystemId(@Nonnull String id);
}