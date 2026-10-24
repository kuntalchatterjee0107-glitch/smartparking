package org.airtribe.smartparking.service.Strategy;

import org.airtribe.smartparking.entity.VehicleType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface FeeCalculationStrategy {
    BigDecimal calculateFee(LocalDateTime entryTime, LocalDateTime exitTime, VehicleType vehicleType);
}
