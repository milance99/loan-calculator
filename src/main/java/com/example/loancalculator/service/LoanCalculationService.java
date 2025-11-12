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

    public BigDecimal calculateMonthlyPayment(BigDecimal loanAmount, BigDecimal annualInterestPercent, int numberOfMonths) {

        BigDecimal monthlyInterestRate = annualInterestPercent
                .divide(BigDecimal.valueOf(100), INTEREST_SCALE, ROUNDING)
                .divide(BigDecimal.valueOf(12), INTEREST_SCALE, ROUNDING);

        if (monthlyInterestRate.compareTo(BigDecimal.ZERO) == 0) {
            return loanAmount.divide(BigDecimal.valueOf(numberOfMonths), CURRENCY_SCALE, ROUNDING);
        }

        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyInterestRate);
        BigDecimal rateCompounded = onePlusRate.pow(numberOfMonths);

        BigDecimal numerator = loanAmount.multiply(monthlyInterestRate).multiply(rateCompounded);
        BigDecimal denominator = rateCompounded.subtract(BigDecimal.ONE);

        return numerator.divide(denominator, INTEREST_SCALE, ROUNDING)
                .setScale(CURRENCY_SCALE, ROUNDING);
    }

    public List<InstallmentDto> generateAmortizationSchedule(BigDecimal loanAmount, BigDecimal annualInterestPercent,
                                                             int numberOfMonths, BigDecimal monthlyPayment) {

        BigDecimal monthlyInterestRate = annualInterestPercent
                .divide(BigDecimal.valueOf(100), INTEREST_SCALE, ROUNDING)
                .divide(BigDecimal.valueOf(12), INTEREST_SCALE, ROUNDING);

        List<InstallmentDto> amortizationSchedule = new ArrayList<>(numberOfMonths);
        BigDecimal outstandingBalance = loanAmount;

        for (int monthNumber = 1; monthNumber <= numberOfMonths; monthNumber++) {
            BigDecimal interestForMonth = outstandingBalance.multiply(monthlyInterestRate)
                    .setScale(INTEREST_SCALE, ROUNDING);

            BigDecimal principalForMonth = monthlyPayment.subtract(interestForMonth)
                    .setScale(INTEREST_SCALE, ROUNDING);

            if (principalForMonth.compareTo(outstandingBalance) > 0) {
                principalForMonth = outstandingBalance;
            }

            BigDecimal totalPaymentForMonth = principalForMonth.add(interestForMonth);

            BigDecimal paymentRounded = totalPaymentForMonth.setScale(CURRENCY_SCALE, ROUNDING);
            BigDecimal principalRounded = principalForMonth.setScale(CURRENCY_SCALE, ROUNDING);
            BigDecimal interestRounded = interestForMonth.setScale(CURRENCY_SCALE, ROUNDING);

            outstandingBalance = outstandingBalance.subtract(principalForMonth);
            if (outstandingBalance.compareTo(BigDecimal.ZERO) < 0) {
                outstandingBalance = BigDecimal.ZERO;
            }

            BigDecimal balanceRounded = outstandingBalance.setScale(CURRENCY_SCALE, ROUNDING);

            if (monthNumber == numberOfMonths) {
                balanceRounded = BigDecimal.ZERO.setScale(CURRENCY_SCALE, ROUNDING);
            }

            amortizationSchedule.add(new InstallmentDto(
                    monthNumber,
                    paymentRounded,
                    principalRounded,
                    interestRounded,
                    balanceRounded
            ));
        }

        return amortizationSchedule;
    }
}
