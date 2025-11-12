package com.example.loancalculator.provider;

import com.example.loancalculator.model.dto.InstallmentDto;
import com.example.loancalculator.model.dto.LoanResponseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class LoanResponseDtoProvider {

    public static LoanResponseDto validResponse() {
        return new LoanResponseDto(
            "test-id-123",
            BigDecimal.valueOf(10000),
            BigDecimal.valueOf(5.0),
            12,
            BigDecimal.valueOf(856.07),
            LocalDateTime.now(),
            List.of(
                new InstallmentDto(1, BigDecimal.valueOf(856.07), BigDecimal.valueOf(814.40), BigDecimal.valueOf(41.67), BigDecimal.valueOf(9185.60)),
                new InstallmentDto(2, BigDecimal.valueOf(856.07), BigDecimal.valueOf(817.80), BigDecimal.valueOf(38.27), BigDecimal.valueOf(8367.80))
            )
        );
    }
}
