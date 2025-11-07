package com.example.loancalculator.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Data Transfer Object for Loan Request")
public record LoanRequestDto(
        @Schema(description = "Total loan amount", example = "1000.00")
        @NotNull
        @DecimalMin("0.01")
        Double amount,
        @Schema(description = "Annual interest rate in percent", example = "5.0")
        @NotNull
        @DecimalMin("0.0")
        Double annualInterestPercent,
        @Schema(description = "Number of months for loan repayment", example = "12")
        @NotNull
        @Min(1)
        Integer numberOfMonths
) {}
