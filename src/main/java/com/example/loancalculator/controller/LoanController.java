package com.example.loancalculator.controller;

import com.example.loancalculator.model.dto.LoanRequestDto;
import com.example.loancalculator.model.dto.LoanResponseDto;
import com.example.loancalculator.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Tag(name = "Loan Calculator", description = "API for calculating loan installment plans")
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    @Operation(
        summary = "Calculate loan installment plan",
        description = "Calculates monthly payments and generates a complete amortization schedule for a loan based on amount, interest rate, and term. The calculation result is persisted to the database."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Loan calculation successful and data persisted",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoanResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input parameters",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<LoanResponseDto> calculateLoan(@Valid @RequestBody LoanRequestDto request) {
        LoanResponseDto response = loanService.calculateAndSaveLoan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
