package org.airtribe.smartparking.repository;


import jakarta.persistence.LockModeType;
import org.airtribe.smartparking.entity.ParkingSpot;
import org.airtribe.smartparking.entity.SpotSize;
import org.airtribe.smartparking.entity.SpotStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM ParkingSpot s JOIN s.floor f " +
            "WHERE s.status = :status AND s.spotSize IN :sizes " +
            "ORDER BY s.spotSize ASC, f.floorNumber ASC, s.spotNumber ASC")
    List<ParkingSpot> findNearestAvailableSpotWithLock(
            @Param("status") SpotStatus status,
            @Param("sizes") List<SpotSize> compatibleSizes,
            Pageable pageable);

    long countByStatusAndSpotSize(SpotStatus status, SpotSize spotSize);
}