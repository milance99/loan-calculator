package com.example.loancalculator.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Data Transfer Object for Installment Details")
public record InstallmentDto(
        @Schema(description = "Month number of the installment", example = "1")
        Integer month,
        @Schema(description = "Total payment amount for the installment", example = "856.07")
        BigDecimal payment,
        @Schema(description = "Principal portion of the installment", example = "814.07")
        BigDecimal principal,
        @Schema(description = "Interest portion of the installment", example = "42.00")
        BigDecimal interest,
        @Schema(description = "Remaining balance after the installment", example = "9185.93")
        BigDecimal remainingBalance
) {}
