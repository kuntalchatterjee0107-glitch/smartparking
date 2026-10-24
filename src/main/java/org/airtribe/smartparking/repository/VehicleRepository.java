package org.airtribe.smartparking.repository;


import org.airtribe.smartparking.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByLicensePlate(String licensePlate);
}