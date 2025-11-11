package com.example.loancalculator.mapper;

import com.example.loancalculator.model.Installment;
import com.example.loancalculator.model.Loan;
import com.example.loancalculator.model.dto.InstallmentDto;

public class InstallmentMapper {

    public static InstallmentDto toDto(Installment installment) {
        return new InstallmentDto(
                installment.getMonth(),
                installment.getPayment(),
                installment.getPrincipal(),
                installment.getInterest(),
                installment.getRemainingBalance()
        );
    }

    public static Installment toEntity(InstallmentDto dto, Loan loan) {
        Installment installment = new Installment();
        installment.setMonth(dto.month());
        installment.setPayment(dto.payment());
        installment.setPrincipal(dto.principal());
        installment.setInterest(dto.interest());
        installment.setRemainingBalance(dto.remainingBalance());
        installment.setLoan(loan);
        return installment;
    }
}
