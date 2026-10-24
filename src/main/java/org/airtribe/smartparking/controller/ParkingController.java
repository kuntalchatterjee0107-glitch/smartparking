package org.airtribe.smartparking.controller;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.airtribe.smartparking.entity.ParkingTicket;
import org.airtribe.smartparking.entity.SpotSize;
import org.airtribe.smartparking.entity.VehicleType;
import org.airtribe.smartparking.service.Strategy.ParkingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/parking")
@RequiredArgsConstructor
public class ParkingController {

    private final ParkingService parkingService;

    public record CheckInRequest(@NotBlank String licensePlate, @NotNull VehicleType vehicleType) {}

    @PostMapping("/check-in")
    public ResponseEntity<ParkingTicket> checkIn(@RequestBody CheckInRequest request) {
        return ResponseEntity.ok(parkingService.checkIn(request.licensePlate(), request.vehicleType()));
    }

    @PostMapping("/check-out/{ticketNumber}")
    public ResponseEntity<ParkingTicket> checkOut(@PathVariable String ticketNumber) {
        return ResponseEntity.ok(parkingService.checkOut(ticketNumber));
    }

    @GetMapping("/availability")
    public ResponseEntity<Map<SpotSize, Long>> getAvailability() {
        return ResponseEntity.ok(parkingService.getRealTimeAvailability());
    }
}