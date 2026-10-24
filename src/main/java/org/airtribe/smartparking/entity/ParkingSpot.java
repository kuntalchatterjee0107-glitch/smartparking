package org.airtribe.smartparking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parking_spots", indexes = {
        @Index(name = "idx_spot_status_size", columnList = "status, spotSize")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String spotNumber; // e.g., "F1-S01"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpotSize spotSize;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpotStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id", nullable = false)
    private ParkingFloor floor;

    @Version
    private Integer version; // Prevents lost updates during high concurrency
}