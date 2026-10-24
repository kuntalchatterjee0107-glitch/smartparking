package org.airtribe.smartparking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "parking_floors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingFloor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer floorNumber;

    @OneToMany(mappedBy = "floor", cascade = CascadeType.ALL)
    private List<ParkingSpot> spots;
}