package com.example.loancalculator.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Response containing loan calculation details and amortization schedule")
public record LoanResponseDto(
        @Schema(description = "Unique identifier of the loan calculation", example = "64f8d9e4c5a1b2c3d4e5f678")
        String id,
        @Schema(description = "Total loan amount", example = "10000.00")
        BigDecimal amount,
        @Schema(description = "Annual interest rate in percent", example = "5.0")
        BigDecimal annualInterestPercent,
        @Schema(description = "Number of months for loan repayment", example = "12")
        Integer numberOfMonths,
        @Schema(description = "Calculated monthly payment amount", example = "856.07")
        BigDecimal monthlyPayment,
        @Schema(description = "Timestamp when the loan calculation was created", example = "2023-12-01T10:30:00")
        LocalDateTime createdAt,
        @Schema(description = "Complete amortization schedule with monthly breakdown")
        List<InstallmentDto> schedule
) {}
