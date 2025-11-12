package com.example.loancalculator.service;

import com.example.loancalculator.AbstractLoanCalculatorTest;
import com.example.loancalculator.model.dto.LoanRequestDto;
import com.example.loancalculator.model.dto.LoanResponseDto;
import com.example.loancalculator.provider.LoanRequestDtoProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Loan Service Integration Tests")
class LoanServiceTest extends AbstractLoanCalculatorTest {

    @Autowired
    private LoanService loanService;

    @Test
    @DisplayName("Should calculate and save loan successfully - Happy Path")
    void shouldCalculateAndSaveLoanSuccessfully() {
        LoanRequestDto request = LoanRequestDtoProvider.validRequest(
            BigDecimal.valueOf(10000),
            BigDecimal.valueOf(5.0),
            12
        );

        LoanResponseDto response = loanService.calculateAndSaveLoan(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isNotNull();
        assertThat(response.amount()).isEqualTo(request.amount());
        assertThat(response.annualInterestPercent()).isEqualTo(request.annualInterestPercent());
        assertThat(response.numberOfMonths()).isEqualTo(request.numberOfMonths());
        assertThat(response.monthlyPayment()).isNotNull().isPositive();
        assertThat(response.schedule()).isNotNull().hasSize(12);

        assertThat(response.monthlyPayment()).isEqualTo(BigDecimal.valueOf(856.07));

        var firstInstallment = response.schedule().getFirst();
        assertThat(firstInstallment.month()).isEqualTo(1);
        assertThat(firstInstallment.payment()).isEqualTo(BigDecimal.valueOf(856.07));
        assertThat(firstInstallment.principal()).isEqualTo(new BigDecimal("814.40"));
        assertThat(firstInstallment.interest()).isEqualTo(BigDecimal.valueOf(41.67));
        assertThat(firstInstallment.remainingBalance()).isEqualTo(new BigDecimal("9185.60"));

        var lastInstallment = response.schedule().get(11);
        assertThat(lastInstallment.month()).isEqualTo(12);
        assertThat(lastInstallment.remainingBalance()).isEqualTo(new BigDecimal("0.00"));
    }

    @Test
    @DisplayName("Should handle zero interest loan correctly")
    void shouldHandleZeroInterestLoanCorrectly() {
        LoanRequestDto request = LoanRequestDtoProvider.validRequest(
            BigDecimal.valueOf(6000),
            BigDecimal.ZERO,
            6
        );

        LoanResponseDto response = loanService.calculateAndSaveLoan(request);
        assertThat(response.monthlyPayment()).isEqualTo(new BigDecimal("1000.00"));

        response.schedule().forEach(installment -> {
            assertThat(installment.interest()).isEqualTo(new BigDecimal("0.00"));
            assertThat(installment.principal()).isEqualTo(new BigDecimal("1000.00"));
        });
    }

    @Test
    @DisplayName("Should persist loan data to database")
    void shouldPersistLoanDataToDatabase() {
        LoanRequestDto request = LoanRequestDtoProvider.validRequest();

        LoanResponseDto response = loanService.calculateAndSaveLoan(request);

        assertThat(response.id()).isNotNull();
        assertThat(response.amount()).isEqualTo(BigDecimal.valueOf(10000));
        assertThat(response.annualInterestPercent()).isEqualTo(BigDecimal.valueOf(5.0));
        assertThat(response.numberOfMonths()).isEqualTo(12);
        assertThat(response.schedule()).hasSize(12);
    }

    @Test
    @DisplayName("Should generate complete amortization schedule")
    void shouldGenerateCompleteAmortizationSchedule() {
        LoanRequestDto request = LoanRequestDtoProvider.validRequest(
            BigDecimal.valueOf(5000),
            BigDecimal.valueOf(6.0),
            10
        );

        LoanResponseDto response = loanService.calculateAndSaveLoan(request);

        assertThat(response.schedule()).hasSize(10);
        BigDecimal previousBalance = response.amount();
        for (var installment : response.schedule()) {
            assertThat(installment.month()).isBetween(1, 10);
            assertThat(installment.payment()).isEqualTo(response.monthlyPayment());
            assertThat(installment.remainingBalance()).isLessThanOrEqualTo(previousBalance);
            assertThat(installment.remainingBalance()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
            assertThat(installment.principal().add(installment.interest())).isEqualTo(installment.payment());
            previousBalance = installment.remainingBalance();
        }

        var lastInstallment = response.schedule().get(9);
        assertThat(lastInstallment.remainingBalance()).isEqualTo(new BigDecimal("0.00"));
    }
}
