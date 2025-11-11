package com.example.loancalculator;

import com.example.loancalculator.repostiory.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles(profiles = {"test"})
@TestPropertySource(locations = "classpath:application-test.properties")
public class AbstractLoanCalculatorTest {

    @Autowired
    private LoanRepository loanRepository;

    @BeforeEach
    void setup() {
        clearDatabase();
    }

    protected void clearDatabase() {
        loanRepository.deleteAll();
        loanRepository.flush();
    }
}
