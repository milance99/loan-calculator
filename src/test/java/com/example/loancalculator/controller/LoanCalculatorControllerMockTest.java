package com.example.loancalculator.controller;

import com.example.loancalculator.AbstractLoanCalculatorWebMockMvcTest;
import com.example.loancalculator.model.dto.LoanRequestDto;
import com.example.loancalculator.provider.LoanRequestDtoProvider;
import com.example.loancalculator.provider.LoanResponseDtoProvider;
import com.example.loancalculator.service.LoanService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoanController.class)
public class LoanCalculatorControllerMockTest extends AbstractLoanCalculatorWebMockMvcTest {

    @MockitoBean
    private LoanService loanService;

    @Captor
    private ArgumentCaptor<LoanRequestDto> loanRequestCaptor;

    @Test
    @DisplayName("Calculate loan happy path should call service with correct request")
    void calculateLoan_happyPath_shouldCallServiceWithCorrectRequest() throws Exception {
        var request = LoanRequestDtoProvider.validRequest();
        var expectedResponse = LoanResponseDtoProvider.validResponse();

        when(loanService.calculateAndSaveLoan(any(LoanRequestDto.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(post(LOAN_API_BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(loanService, times(1)).calculateAndSaveLoan(loanRequestCaptor.capture());

        var capturedRequest = loanRequestCaptor.getValue();
        assert capturedRequest.amount().equals(request.amount());
        assert capturedRequest.annualInterestPercent().equals(request.annualInterestPercent());
        assert capturedRequest.numberOfMonths().equals(request.numberOfMonths());
    }

    @ParameterizedTest
    @MethodSource("invalidRequestProvider")
    @DisplayName("Calculate loan with invalid request should return 400")
    void calculateLoan_invalidRequest_shouldReturnBadRequest(LoanRequestDto invalidRequest) throws Exception {
        mockMvc.perform(post(LOAN_API_BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(loanService, never()).calculateAndSaveLoan(any(LoanRequestDto.class));
    }

    static Object[][] invalidRequestProvider() {
        return LoanRequestDtoProvider.invalidRequests();
    }
}
