package org.airtribe.smartparking.service.Strategy;


import lombok.RequiredArgsConstructor;
import org.airtribe.smartparking.entity.*;
import org.airtribe.smartparking.exeption.CustomExceptions;
import org.airtribe.smartparking.repository.ParkingSpotRepository;
import org.airtribe.smartparking.repository.ParkingTicketRepository;
import org.airtribe.smartparking.repository.VehicleRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParkingService {

    private final ParkingSpotRepository spotRepository;
    private final ParkingTicketRepository ticketRepository;
    private final FeeCalculationStrategy feeStrategy;
    private final VehicleRepository vehicleRepository;

    // Best-fit compatibility rules
    private List<SpotSize> getCompatibleSizes(VehicleType type) {
        return switch (type) {
            case MOTORCYCLE -> List.of(SpotSize.SMALL, SpotSize.MEDIUM, SpotSize.LARGE);
            case CAR        -> List.of(SpotSize.MEDIUM, SpotSize.LARGE);
            case BUS        -> List.of(SpotSize.LARGE);
        };
    }

    @Transactional
    public ParkingTicket checkIn(String licensePlate, VehicleType vehicleType) {
        // 1. Check if vehicle is already parked inside
        ticketRepository.findByVehicle_LicensePlateAndStatus(licensePlate, TicketStatus.ACTIVE)
                .ifPresent(t -> {
                    throw new CustomExceptions.VehicleAlreadyParkedException("Vehicle " + licensePlate + " is already inside the lot.");
                });

        // 2. Fetch existing Vehicle or register a new one
        Vehicle vehicle = vehicleRepository.findByLicensePlate(licensePlate)
                .orElseGet(() -> vehicleRepository.save(
                        Vehicle.builder()
                                .licensePlate(licensePlate)
                                .vehicleType(vehicleType)
                                .build()
                ));

        // 3. Find and lock the best available spot
        List<SpotSize> allowedSizes = getCompatibleSizes(vehicle.getVehicleType());
        List<ParkingSpot> spots = spotRepository.findNearestAvailableSpotWithLock(
                SpotStatus.AVAILABLE, allowedSizes, PageRequest.of(0, 1));

        if (spots.isEmpty()) {
            throw new CustomExceptions.NoSpotAvailableException("No available parking spot for vehicle type: " + vehicleType);
        }

        ParkingSpot assignedSpot = spots.get(0);
        assignedSpot.setStatus(SpotStatus.OCCUPIED);
        spotRepository.save(assignedSpot);

        // 4. Generate Ticket linked to the Vehicle entity
        ParkingTicket ticket = ParkingTicket.builder()
                .ticketNumber("TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .vehicle(vehicle)
                .spot(assignedSpot)
                .entryTime(LocalDateTime.now())
                .status(TicketStatus.ACTIVE)
                .build();

        return ticketRepository.save(ticket);
    }
    @Transactional
    public ParkingTicket checkOut(String ticketNumber) {
        ParkingTicket ticket = ticketRepository.findByTicketNumberAndStatus(ticketNumber, TicketStatus.ACTIVE)
                .orElseThrow(() -> new CustomExceptions.TicketNotFoundException("Active ticket not found: " + ticketNumber));

        LocalDateTime exitTime = LocalDateTime.now();
        BigDecimal totalFee = feeStrategy.calculateFee(
                ticket.getEntryTime(),
                exitTime,
                ticket.getVehicle().getVehicleType()
        );
        // 1. Update Ticket
        ticket.setExitTime(exitTime);
        ticket.setFee(totalFee);
        ticket.setStatus(TicketStatus.PAID);

        // 2. Free up the Parking Spot in real time
        ParkingSpot spot = ticket.getSpot();
        spot.setStatus(SpotStatus.AVAILABLE);
        spotRepository.save(spot);

        return ticketRepository.save(ticket);
    }

    @Transactional(readOnly = true)
    public Map<SpotSize, Long> getRealTimeAvailability() {
        return Map.of(
                SpotSize.SMALL,  spotRepository.countByStatusAndSpotSize(SpotStatus.AVAILABLE, SpotSize.SMALL),
                SpotSize.MEDIUM, spotRepository.countByStatusAndSpotSize(SpotStatus.AVAILABLE, SpotSize.MEDIUM),
                SpotSize.LARGE,  spotRepository.countByStatusAndSpotSize(SpotStatus.AVAILABLE, SpotSize.LARGE)
        );
    }
}