package org.airtribe.smartparking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicles", indexes = {
        @Index(name = "idx_vehicle_plate", columnList = "licensePlate", unique = true)
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType vehicleType;

    private String ownerName;      // Optional metadata for registered users
    private String contactNumber;  // Optional metadata for SMS alerts
}