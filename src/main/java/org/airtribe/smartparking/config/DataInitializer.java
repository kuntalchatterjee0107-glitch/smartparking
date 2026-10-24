package org.airtribe.smartparking.config;


import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.airtribe.smartparking.entity.ParkingFloor;
import org.airtribe.smartparking.entity.ParkingSpot;
import org.airtribe.smartparking.entity.SpotSize;
import org.airtribe.smartparking.entity.SpotStatus;
import org.airtribe.smartparking.repository.ParkingSpotRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final EntityManager entityManager;
    private final ParkingSpotRepository spotRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (spotRepository.count() > 0) return;

        for (int f = 1; f <= 2; f++) {
            ParkingFloor floor = ParkingFloor.builder().floorNumber(f).build();
            entityManager.persist(floor);

            spotRepository.save(ParkingSpot.builder()
                    .spotNumber("F" + f + "-S01").spotSize(SpotSize.SMALL)
                    .status(SpotStatus.AVAILABLE).floor(floor).build());
            spotRepository.save(ParkingSpot.builder()
                    .spotNumber("F" + f + "-M01").spotSize(SpotSize.MEDIUM)
                    .status(SpotStatus.AVAILABLE).floor(floor).build());
            spotRepository.save(ParkingSpot.builder()
                    .spotNumber("F" + f + "-L01").spotSize(SpotSize.LARGE)
                    .status(SpotStatus.AVAILABLE).floor(floor).build());
        }
    }
}