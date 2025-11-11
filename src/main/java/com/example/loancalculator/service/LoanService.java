package com.example.loancalculator.service;

import com.example.loancalculator.mapper.InstallmentMapper;
import com.example.loancalculator.mapper.LoanMapper;
import com.example.loancalculator.model.Installment;
import com.example.loancalculator.model.Loan;
import com.example.loancalculator.model.dto.InstallmentDto;
import com.example.loancalculator.model.dto.LoanRequestDto;
import com.example.loancalculator.model.dto.LoanResponseDto;
import com.example.loancalculator.repostiory.LoanRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class LoanService {

    private final LoanRepository loanRepository;
    private final LoanCalculationService loanCalculationService;

    @Transactional
    public LoanResponseDto calculateAndSaveLoan(@Valid LoanRequestDto request) {
        log.info("Starting loan calculation for amount: {}, interest: {}%, months: {}",
                request.amount(), request.annualInterestPercent(), request.numberOfMonths());

        BigDecimal monthlyPayment = loanCalculationService.calculateMonthlyPayment(
                request.amount(),
                request.annualInterestPercent(),
                request.numberOfMonths()
        );

        List<InstallmentDto> schedule = loanCalculationService.generateAmortizationSchedule(
                request.amount(),
                request.annualInterestPercent(),
                request.numberOfMonths(),
                monthlyPayment
        );

        Loan loan = LoanMapper.toEntity(request);
        loan.setMonthlyPayment(monthlyPayment);

        List<Installment> installmentEntities = schedule.stream()
                .map(dto -> InstallmentMapper.toEntity(dto, loan))
                .toList();

        loan.setInstallments(installmentEntities);
        Loan savedLoan = loanRepository.save(loan);

        log.info("Loan calculation completed successfully. Generated loan ID: {}", savedLoan.getId());

        return LoanMapper.toResponseDto(savedLoan);
    }
}
