package com.example.loancalculator.mapper;

import com.example.loancalculator.model.Loan;
import com.example.loancalculator.model.dto.InstallmentDto;
import com.example.loancalculator.model.dto.LoanRequestDto;
import com.example.loancalculator.model.dto.LoanResponseDto;

import java.util.List;


public class LoanMapper {

    public static Loan toEntity(LoanRequestDto request) {
        Loan loan = new Loan();
        loan.setAmount(request.amount());
        loan.setAnnualInterestPercent(request.annualInterestPercent());
        loan.setNumberOfMonths(request.numberOfMonths());
        return loan;
    }

    public static LoanResponseDto toResponseDto(Loan loan) {
        if (loan == null) {
            return null;
        }

        List<InstallmentDto> installmentDtos = loan.getInstallments() != null
            ? loan.getInstallments().stream()
                .map(InstallmentMapper::toDto)
                .toList()
            : List.of();

        return new LoanResponseDto(
            loan.getId(),
            loan.getAmount(),
            loan.getAnnualInterestPercent(),
            loan.getNumberOfMonths(),
            loan.getMonthlyPayment(),
            loan.getCreatedAt(),
            installmentDtos
        );
    }
}
