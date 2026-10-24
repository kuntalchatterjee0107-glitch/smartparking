package org.airtribe.smartparking.service.Strategy;


import org.airtribe.smartparking.entity.VehicleType;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class HourlyFeeCalculationStrategy implements FeeCalculationStrategy {

    private static final Map<VehicleType, BigDecimal> HOURLY_RATES = Map.of(
            VehicleType.MOTORCYCLE, new BigDecimal("20.00"),
            VehicleType.CAR,        new BigDecimal("50.00"),
            VehicleType.BUS,        new BigDecimal("100.00")
    );

    @Override
    public BigDecimal calculateFee(LocalDateTime entryTime, LocalDateTime exitTime, VehicleType vehicleType) {
        long minutes = Duration.between(entryTime, exitTime).toMinutes();
        // Minimum 1 hour charge; round up partial hours (ceiling division)
        long billableHours = Math.max(1, (minutes + 59) / 60);

        BigDecimal rate = HOURLY_RATES.getOrDefault(vehicleType, new BigDecimal("50.00"));
        return rate.multiply(BigDecimal.valueOf(billableHours));
    }
}