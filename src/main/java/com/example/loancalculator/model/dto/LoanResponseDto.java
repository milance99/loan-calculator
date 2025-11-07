package com.example.loancalculator.model.dto;

import java.time.LocalDateTime;
import java.util.List;

public record LoanResponseDto(
        Long id,
        Double amount,
        Double annualInterestPercent,
        Integer numberOfMonths,
        Double monthlyPayment,
        LocalDateTime createdAt,
        List<InstallmentDto> schedule
) {}
