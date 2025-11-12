package com.example.loancalculator.provider;

import com.example.loancalculator.model.dto.LoanRequestDto;

import java.math.BigDecimal;

public class LoanRequestDtoProvider {

    public static LoanRequestDto validRequest() {
        return new LoanRequestDto(
            BigDecimal.valueOf(10000),
            BigDecimal.valueOf(5.0),
            12
        );
    }

    public static LoanRequestDto validRequest(BigDecimal amount, BigDecimal interest, int months) {
        return new LoanRequestDto(amount, interest, months);
    }

    public static LoanRequestDto nullAmount() {
        return new LoanRequestDto(null, BigDecimal.valueOf(5.0), 12);
    }

    public static LoanRequestDto negativeAmount() {
        return new LoanRequestDto(BigDecimal.valueOf(-1000), BigDecimal.valueOf(5.0), 12);
    }

    public static LoanRequestDto zeroAmount() {
        return new LoanRequestDto(BigDecimal.valueOf(0), BigDecimal.valueOf(5.0), 12);
    }

    public static LoanRequestDto tooSmallAmount() {
        return new LoanRequestDto(BigDecimal.valueOf(0.005), BigDecimal.valueOf(5.0), 12);
    }

    public static LoanRequestDto nullInterest() {
        return new LoanRequestDto(BigDecimal.valueOf(10000), null, 12);
    }

    public static LoanRequestDto negativeInterest() {
        return new LoanRequestDto(BigDecimal.valueOf(10000), BigDecimal.valueOf(-5.0), 12);
    }

    public static LoanRequestDto nullMonths() {
        return new LoanRequestDto(BigDecimal.valueOf(10000), BigDecimal.valueOf(5.0), null);
    }

    public static LoanRequestDto zeroMonths() {
        return new LoanRequestDto(BigDecimal.valueOf(10000), BigDecimal.valueOf(5.0), 0);
    }

    public static LoanRequestDto negativeMonths() {
        return new LoanRequestDto(BigDecimal.valueOf(10000), BigDecimal.valueOf(5.0), -1);
    }

    public static Object[][] invalidRequests() {
        return new Object[][]{
            {nullAmount(), "null amount"},
            {negativeAmount(), "negative amount"},
            {zeroAmount(), "zero amount"},
            {tooSmallAmount(), "amount too small"},
            {nullInterest(), "null interest"},
            {negativeInterest(), "negative interest"},
            {nullMonths(), "null months"},
            {zeroMonths(), "zero months"},
            {negativeMonths(), "negative months"}
        };
    }
}
