package com.example.loancalculator.service;

import com.example.loancalculator.model.Installment;
import com.example.loancalculator.model.Loan;
import com.example.loancalculator.model.dto.InstallmentDto;
import com.example.loancalculator.model.dto.LoanRequestDto;
import com.example.loancalculator.model.dto.LoanResponseDto;
import com.example.loancalculator.provider.InstallmentDtoProvider;
import com.example.loancalculator.provider.LoanRequestDtoProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("Loan Service Unit Tests")
@ExtendWith(MockitoExtension.class)
class LoanServiceMockTest {

    @Mock
    private com.example.loancalculator.repostiory.LoanRepository loanRepository;

    @Mock
    private LoanCalculationService loanCalculationService;

    @InjectMocks
    private LoanService loanService;

    @Test
    @DisplayName("Should orchestrate loan calculation and persistence")
    void shouldOrchestrateLoanCalculationAndPersistence() {
        LoanRequestDto request = LoanRequestDtoProvider.validRequest();
        BigDecimal monthlyPayment = BigDecimal.valueOf(856.07);
        List<InstallmentDto> schedule = InstallmentDtoProvider.sampleSchedule();

        when(loanCalculationService.calculateMonthlyPayment(
            request.amount(), request.annualInterestPercent(), request.numberOfMonths()))
            .thenReturn(monthlyPayment);

        when(loanCalculationService.generateAmortizationSchedule(
            request.amount(), request.annualInterestPercent(), request.numberOfMonths(), monthlyPayment))
            .thenReturn(schedule);

        when(loanRepository.save(any(Loan.class))).thenReturn(createMockLoanWithInstallments());

        LoanResponseDto response = loanService.calculateAndSaveLoan(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isNotNull();
        assertThat(response.schedule()).isNotNull();
        assertThat(response.schedule()).hasSizeGreaterThan(0);
    }

    @ParameterizedTest
    @MethodSource("com.example.loancalculator.provider.LoanRequestDtoProvider#invalidRequests")
    @DisplayName("Should reject invalid loan requests with validation errors")
    void shouldRejectInvalidLoanRequests(LoanRequestDto invalidRequest) {
        assertThatThrownBy(() -> loanService.calculateAndSaveLoan(invalidRequest))
                .isNotNull();
    }

    private Loan createMockLoanWithInstallments() {
        Loan loan = new Loan();
        loan.setId("test-id-123");
        loan.setAmount(BigDecimal.valueOf(10000));
        loan.setAnnualInterestPercent(BigDecimal.valueOf(5.0));
        loan.setNumberOfMonths(12);
        loan.setMonthlyPayment(BigDecimal.valueOf(856.07));


        Installment installment1 = new Installment();
        installment1.setMonth(1);
        installment1.setPayment(BigDecimal.valueOf(856.07));
        installment1.setPrincipal(BigDecimal.valueOf(814.40));
        installment1.setInterest(BigDecimal.valueOf(41.67));
        installment1.setRemainingBalance(BigDecimal.valueOf(9185.60));

        Installment installment2 = new Installment();
        installment2.setMonth(2);
        installment2.setPayment(BigDecimal.valueOf(856.07));
        installment2.setPrincipal(BigDecimal.valueOf(817.80));
        installment2.setInterest(BigDecimal.valueOf(38.27));
        installment2.setRemainingBalance(BigDecimal.valueOf(8367.80));

        loan.setInstallments(List.of(installment1, installment2));

        return loan;
    }
}
