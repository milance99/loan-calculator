package com.example.loancalculator.service;

import com.example.loancalculator.model.dto.InstallmentDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Loan Calculation Service Tests")
class LoanCalculationServiceTest {

    private LoanCalculationService calculationService;

    @BeforeEach
    void setUp() {
        calculationService = new LoanCalculationService();
    }

    @Nested
    @DisplayName("Monthly Payment Calculations")
    class MonthlyPaymentTests {

        @Test
        @DisplayName("Should calculate monthly payment for standard loan")
        void shouldCalculateMonthlyPaymentForStandardLoan() {
            BigDecimal amount = BigDecimal.valueOf(10000);
            BigDecimal annualInterestPercent = BigDecimal.valueOf(5.0);
            int numberOfMonths = 12;

            BigDecimal monthlyPayment = calculationService.calculateMonthlyPayment(amount, annualInterestPercent, numberOfMonths);

            assertThat(monthlyPayment).isEqualTo(BigDecimal.valueOf(856.07));
        }

        @Test
        @DisplayName("Should handle zero interest rate (simple division)")
        void shouldHandleZeroInterestRate() {
            BigDecimal amount = BigDecimal.valueOf(12000);
            BigDecimal annualInterestPercent = BigDecimal.ZERO;
            int numberOfMonths = 12;

            BigDecimal monthlyPayment = calculationService.calculateMonthlyPayment(amount, annualInterestPercent, numberOfMonths);

            assertThat(monthlyPayment).isEqualTo(new BigDecimal("1000.00"));
        }

        @Test
        @DisplayName("Should calculate high interest loan correctly")
        void shouldCalculateHighInterestLoanCorrectly() {
            BigDecimal amount = BigDecimal.valueOf(5000);
            BigDecimal annualInterestPercent = BigDecimal.valueOf(15.0);
            int numberOfMonths = 24;

            BigDecimal monthlyPayment = calculationService.calculateMonthlyPayment(amount, annualInterestPercent, numberOfMonths);

            assertThat(monthlyPayment).isPositive();
            assertThat(monthlyPayment).isGreaterThan(BigDecimal.valueOf(200));
            assertThat(monthlyPayment).isLessThan(BigDecimal.valueOf(300));
        }

        @Test
        @DisplayName("Should maintain precision with decimal interest rates")
        void shouldMaintainPrecisionWithDecimalInterestRates() {
            BigDecimal amount = BigDecimal.valueOf(8000);
            BigDecimal annualInterestPercent = BigDecimal.valueOf(7.25);
            int numberOfMonths = 36;

            BigDecimal monthlyPayment = calculationService.calculateMonthlyPayment(amount, annualInterestPercent, numberOfMonths);

            assertThat(monthlyPayment.scale()).isEqualTo(2);
            assertThat(monthlyPayment).isGreaterThan(BigDecimal.valueOf(200));
            assertThat(monthlyPayment).isLessThan(BigDecimal.valueOf(300));
        }
    }

    @Nested
    @DisplayName("Amortization Schedule Generation")
    class AmortizationScheduleTests {

        @Test
        @DisplayName("Should generate complete amortization schedule")
        void shouldGenerateCompleteAmortizationSchedule() {
            BigDecimal amount = BigDecimal.valueOf(10000);
            BigDecimal annualInterestPercent = BigDecimal.valueOf(5.0);
            int numberOfMonths = 12;
            BigDecimal monthlyPayment = calculationService.calculateMonthlyPayment(amount, annualInterestPercent, numberOfMonths);

            List<InstallmentDto> schedule = calculationService.generateAmortizationSchedule(
                amount, annualInterestPercent, numberOfMonths, monthlyPayment);

            assertThat(schedule).hasSize(12);

            InstallmentDto firstInstallment = schedule.getFirst();
            assertThat(firstInstallment.month()).isEqualTo(1);
            assertThat(firstInstallment.payment()).isEqualTo(BigDecimal.valueOf(856.07));
            assertThat(firstInstallment.interest()).isEqualTo(BigDecimal.valueOf(41.67));
            assertThat(firstInstallment.remainingBalance()).isEqualTo(new BigDecimal("9185.60"));

            InstallmentDto lastInstallment = schedule.get(11);
            assertThat(lastInstallment.month()).isEqualTo(12);
            assertThat(lastInstallment.remainingBalance()).isEqualTo(new BigDecimal("0.00"));
        }

        @Test
        @DisplayName("Should handle single month loan")
        void shouldHandleSingleMonthLoan() {
            BigDecimal amount = BigDecimal.valueOf(1000);
            BigDecimal annualInterestPercent = BigDecimal.valueOf(5.0);
            int numberOfMonths = 1;
            BigDecimal monthlyPayment = calculationService.calculateMonthlyPayment(amount, annualInterestPercent, numberOfMonths);

            List<InstallmentDto> schedule = calculationService.generateAmortizationSchedule(
                amount, annualInterestPercent, numberOfMonths, monthlyPayment);

            assertThat(schedule).hasSize(1);

            InstallmentDto installment = schedule.getFirst();
            assertThat(installment.month()).isEqualTo(1);
            assertThat(installment.remainingBalance()).isEqualTo(new BigDecimal("0.00"));
            assertThat(installment.payment()).isEqualTo(installment.principal().add(installment.interest()));
        }

        @Test
        @DisplayName("Should handle zero interest amortization schedule")
        void shouldHandleZeroInterestAmortizationSchedule() {
            BigDecimal amount = BigDecimal.valueOf(6000);
            BigDecimal annualInterestPercent = BigDecimal.ZERO;
            int numberOfMonths = 6;
            BigDecimal monthlyPayment = calculationService.calculateMonthlyPayment(amount, annualInterestPercent, numberOfMonths);

            List<InstallmentDto> schedule = calculationService.generateAmortizationSchedule(
                amount, annualInterestPercent, numberOfMonths, monthlyPayment);

            assertThat(schedule).hasSize(6);

            for (InstallmentDto installment : schedule) {
                assertThat(installment.interest()).isEqualTo(new BigDecimal("0.00"));
                assertThat(installment.principal()).isEqualTo(new BigDecimal("1000.00")); // 6000/6
            }

            InstallmentDto lastInstallment = schedule.get(5);
            assertThat(lastInstallment.remainingBalance()).isEqualTo(new BigDecimal("0.00"));
        }

        @Test
        @DisplayName("Should maintain balance consistency across installments")
        void shouldMaintainBalanceConsistencyAcrossInstallments() {
            BigDecimal amount = BigDecimal.valueOf(10000);
            BigDecimal annualInterestPercent = BigDecimal.valueOf(5.0);
            int numberOfMonths = 12;
            BigDecimal monthlyPayment = calculationService.calculateMonthlyPayment(amount, annualInterestPercent, numberOfMonths);

            List<InstallmentDto> schedule = calculationService.generateAmortizationSchedule(
                amount, annualInterestPercent, numberOfMonths, monthlyPayment);

            BigDecimal previousBalance = amount;
            for (InstallmentDto installment : schedule) {
                assertThat(installment.remainingBalance()).isLessThanOrEqualTo(previousBalance);
                assertThat(installment.remainingBalance()).isGreaterThanOrEqualTo(new BigDecimal("0.00"));
                previousBalance = installment.remainingBalance();
            }

            InstallmentDto lastInstallment = schedule.getLast();
            assertThat(lastInstallment.remainingBalance()).isEqualTo(new BigDecimal("0.00"));
        }

        @Test
        @DisplayName("Should round all monetary values to 2 decimal places")
        void shouldRoundAllMonetaryValuesToTwoDecimalPlaces() {
            BigDecimal amount = BigDecimal.valueOf(12345.67);
            BigDecimal annualInterestPercent = BigDecimal.valueOf(6.75);
            int numberOfMonths = 24;
            BigDecimal monthlyPayment = calculationService.calculateMonthlyPayment(amount, annualInterestPercent, numberOfMonths);

            List<InstallmentDto> schedule = calculationService.generateAmortizationSchedule(
                amount, annualInterestPercent, numberOfMonths, monthlyPayment);

            for (InstallmentDto installment : schedule) {
                assertThat(installment.payment().scale()).isEqualTo(2);
                assertThat(installment.principal().scale()).isEqualTo(2);
                assertThat(installment.interest().scale()).isEqualTo(2);
                assertThat(installment.remainingBalance().scale()).isEqualTo(2);
            }
        }
    }

    @Nested
    @DisplayName("Mathematical Precision and Edge Cases")
    class PrecisionAndEdgeCasesTests {

        @Test
        @DisplayName("Should handle very small amounts")
        void shouldHandleVerySmallAmounts() {
            BigDecimal amount = BigDecimal.valueOf(0.01);
            BigDecimal annualInterestPercent = BigDecimal.valueOf(5.0);
            int numberOfMonths = 1;

            BigDecimal monthlyPayment = calculationService.calculateMonthlyPayment(amount, annualInterestPercent, numberOfMonths);

            assertThat(monthlyPayment).isNotNull();
            assertThat(monthlyPayment).isPositive();
        }

        @Test
        @DisplayName("Should handle very long loan terms")
        void shouldHandleVeryLongLoanTerms() {
            BigDecimal amount = BigDecimal.valueOf(100000);
            BigDecimal annualInterestPercent = BigDecimal.valueOf(4.5);
            int numberOfMonths = 600;

            BigDecimal monthlyPayment = calculationService.calculateMonthlyPayment(amount, annualInterestPercent, numberOfMonths);

            assertThat(monthlyPayment).isNotNull();
            assertThat(monthlyPayment).isPositive();
            assertThat(monthlyPayment).isLessThan(amount);
        }
    }
}
