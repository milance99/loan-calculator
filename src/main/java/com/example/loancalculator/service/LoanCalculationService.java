package com.example.loancalculator.service;

import com.example.loancalculator.model.dto.InstallmentDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanCalculationService {

    private static final int CURRENCY_SCALE = 2;
    private static final int INTEREST_SCALE = 12;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    public BigDecimal calculateMonthlyPayment(BigDecimal amount, BigDecimal annualInterestPercent, int numberOfMonths) {

        BigDecimal monthlyInterestRate = annualInterestPercent
                .divide(BigDecimal.valueOf(100), INTEREST_SCALE, ROUNDING)
                .divide(BigDecimal.valueOf(12), INTEREST_SCALE, ROUNDING);

        if (monthlyInterestRate.compareTo(BigDecimal.ZERO) == 0) {
            return amount.divide(BigDecimal.valueOf(numberOfMonths), CURRENCY_SCALE, ROUNDING);
        }

        BigDecimal onePlusI = BigDecimal.ONE.add(monthlyInterestRate);
        BigDecimal onePlusIPowerN = onePlusI.pow(numberOfMonths);

        BigDecimal numerator = amount.multiply(monthlyInterestRate).multiply(onePlusIPowerN);
        BigDecimal denominator = onePlusIPowerN.subtract(BigDecimal.ONE);

        return numerator.divide(denominator, INTEREST_SCALE, ROUNDING)
                .setScale(CURRENCY_SCALE, ROUNDING);
    }

    public List<InstallmentDto> generateAmortizationSchedule(BigDecimal amount, BigDecimal annualInterestPercent,
                                                             int numberOfMonths, BigDecimal monthlyPayment) {

        BigDecimal monthlyInterestRate = annualInterestPercent
                .divide(BigDecimal.valueOf(100), INTEREST_SCALE, ROUNDING)
                .divide(BigDecimal.valueOf(12), INTEREST_SCALE, ROUNDING);

        List<InstallmentDto> schedule = new ArrayList<>(numberOfMonths);
        BigDecimal remainingBalance = amount;

        for (int month = 1; month <= numberOfMonths; month++) {
            BigDecimal interest = remainingBalance.multiply(monthlyInterestRate).setScale(INTEREST_SCALE, ROUNDING);
            BigDecimal principal = monthlyPayment.subtract(interest).setScale(INTEREST_SCALE, ROUNDING);

            if (principal.compareTo(remainingBalance) > 0) {
                principal = remainingBalance;
            }

            BigDecimal actualPayment = principal.add(interest);

            BigDecimal dtoPayment = actualPayment.setScale(CURRENCY_SCALE, ROUNDING);
            BigDecimal dtoPrincipal = principal.setScale(CURRENCY_SCALE, ROUNDING);
            BigDecimal dtoInterest = interest.setScale(CURRENCY_SCALE, ROUNDING);

            remainingBalance = remainingBalance.subtract(principal);
            if (remainingBalance.compareTo(BigDecimal.ZERO) < 0) {
                remainingBalance = BigDecimal.ZERO;
            }

            BigDecimal dtoRemaining = remainingBalance.setScale(CURRENCY_SCALE, ROUNDING);

            if (month == numberOfMonths) {
                dtoRemaining = BigDecimal.ZERO.setScale(CURRENCY_SCALE, ROUNDING);
            }

            schedule.add(new InstallmentDto(
                    month,
                    dtoPayment,
                    dtoPrincipal,
                    dtoInterest,
                    dtoRemaining
            ));
        }

        return schedule;
    }
}
