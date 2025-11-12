package com.example.loancalculator.controller;

import com.example.loancalculator.AbstractLoanCalculatorWebMvcTest;
import com.example.loancalculator.provider.LoanRequestDtoProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class LoanCalculatorControllerTest extends AbstractLoanCalculatorWebMvcTest {

    @Test
    @DisplayName("Calculate loan happy path returns 201 with valid response")
    void calculateLoan_happyPath_returnSuccess() throws Exception {
        var request = LoanRequestDtoProvider.validRequest();

        mvc.perform(post(LOAN_API_BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.amount", is(request.amount().intValue())))
                .andExpect(jsonPath("$.annualInterestPercent", closeTo(request.annualInterestPercent().doubleValue(), 0.01)))
                .andExpect(jsonPath("$.numberOfMonths", is(request.numberOfMonths())))
                .andExpect(jsonPath("$.monthlyPayment").exists())
                .andExpect(jsonPath("$.schedule").isArray())
                .andExpect(jsonPath("$.schedule", hasSize(request.numberOfMonths())))
                .andExpect(jsonPath("$.schedule[0].month", is(1)))
                .andExpect(jsonPath("$.schedule[0].payment").exists())
                .andExpect(jsonPath("$.schedule[0].principal").exists())
                .andExpect(jsonPath("$.schedule[0].interest").exists())
                .andExpect(jsonPath("$.schedule[0].remainingBalance").exists())
                .andExpect(jsonPath("$.schedule[11].month", is(12)))
                .andExpect(jsonPath("$.schedule[11].payment").exists())
                .andExpect(jsonPath("$.schedule[11].principal").exists())
                .andExpect(jsonPath("$.schedule[11].interest").exists())
                .andExpect(jsonPath("$.schedule[11].remainingBalance", closeTo(0.0, 0.01)));
    }
}
