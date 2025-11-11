package com.example.loancalculator.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Request object for loan calculation containing amount, interest rate, and repayment term")
public record LoanRequestDto(
        @Schema(description = "Total loan amount to be borrowed", example = "10000.00", minimum = "0.01", maximum = "10000000.00")
        @NotNull
        @DecimalMin("0.01")
        BigDecimal amount,
        @Schema(description = "Annual interest rate in percent (e.g., 5.0 for 5%)", example = "5.0", minimum = "0.0", maximum = "50.0")
        @NotNull
        @DecimalMin("0.0")
        BigDecimal annualInterestPercent,
        @Schema(description = "Number of months for loan repayment", example = "12", minimum = "1", maximum = "600")
        @NotNull
        @Min(1)
        Integer numberOfMonths
) {}
