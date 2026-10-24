package org.airtribe.smartparking.repository;


import org.airtribe.smartparking.entity.ParkingTicket;
import org.airtribe.smartparking.entity.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ParkingTicketRepository extends JpaRepository<ParkingTicket, Long> {
    Optional<ParkingTicket> findByTicketNumberAndStatus(String ticketNumber, TicketStatus status);

    // Traverses ParkingTicket -> Vehicle -> licensePlate
    Optional<ParkingTicket> findByVehicle_LicensePlateAndStatus(String licensePlate, TicketStatus status);
}