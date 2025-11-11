package com.example.loancalculator.provider;

import com.example.loancalculator.model.dto.InstallmentDto;

import java.math.BigDecimal;
import java.util.List;

public class InstallmentDtoProvider {

    public static InstallmentDto firstInstallment() {
        return new InstallmentDto(1, BigDecimal.valueOf(856.07), BigDecimal.valueOf(814.40), BigDecimal.valueOf(41.67), BigDecimal.valueOf(9185.60));
    }

    public static InstallmentDto secondInstallment() {
        return new InstallmentDto(2, BigDecimal.valueOf(856.07), BigDecimal.valueOf(817.80), BigDecimal.valueOf(38.27), BigDecimal.valueOf(8367.80));
    }

    public static InstallmentDto lastInstallment() {
        return new InstallmentDto(12, BigDecimal.valueOf(856.07), BigDecimal.valueOf(853.00), BigDecimal.valueOf(3.64), BigDecimal.valueOf(0.00));
    }

    public static List<InstallmentDto> sampleSchedule() {
        return List.of(firstInstallment(), secondInstallment(), lastInstallment());
    }
}
