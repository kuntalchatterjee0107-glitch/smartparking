package org.airtribe.smartparking.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "parking_tickets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ParkingTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ticketNumber;

    // Normalized relationship to Vehicle
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "spot_id", nullable = false)
    @JsonIgnoreProperties("floor") // Prevents infinite JSON recursion
    private ParkingSpot spot;

    @Column(nullable = false)
    private LocalDateTime entryTime;

    private LocalDateTime exitTime;

    private BigDecimal fee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;
}