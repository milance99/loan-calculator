# Loan Calculator - Sequence Diagram

```mermaid
sequenceDiagram
    participant U as User/Client
    participant C as LoanController
    participant S as LoanService
    participant Calc as LoanCalculationService
    participant LM as LoanMapper
    participant IM as InstallmentMapper
    participant R as LoanRepository
    participant DB as Database

    rect rgb(240, 248, 255)
        Note over U,DB: Calculate Loan Request Flow
        U->>C: POST /api/loans<br/>LoanRequestDto
        activate C

        C->>S: calculateAndSaveLoan(request)
        activate S

        rect rgb(255, 250, 240)
            Note over Calc,S: Monthly Payment Calculation
            S->>Calc: calculateMonthlyPayment(amount, interest, months)
            activate Calc
            Calc-->>S: monthlyPayment: BigDecimal
            deactivate Calc
        end

        rect rgb(255, 248, 240)
            Note over Calc,S: Amortization Schedule Generation
            S->>Calc: generateAmortizationSchedule(amount, interest, months, payment)
            activate Calc
            Calc-->>S: schedule: List<InstallmentDto>
            deactivate Calc
        end

        rect rgb(248, 255, 248)
            Note over LM,IM: Entity Creation and Mapping
            S->>LM: toEntity(request)
            activate LM
            LM-->>S: loan: Loan
            deactivate LM

            loop For each installment in schedule
                S->>IM: toEntity(installmentDto, loan)
                activate IM
                IM-->>S: installment: Installment
                deactivate IM
            end
        end

        rect rgb(255, 248, 248)
            Note over R,DB: Database Persistence
            S->>R: save(loan)
            activate R

            R->>DB: INSERT INTO loans
            R->>DB: INSERT INTO installments
            DB-->>R: loan saved

            R-->>S: savedLoan: Loan
            deactivate R
        end

        rect rgb(248, 255, 252)
            Note over LM,S: Response Mapping
            S->>LM: toResponseDto(savedLoan)
            activate LM
            LM-->>S: responseDto: LoanResponseDto
            deactivate LM
        end

        S-->>C: responseDto
        deactivate S

        C-->>U: HTTP 201 Created<br/>LoanResponseDto
        deactivate C
    end
```

## Flow Explanation

1. **User Request**: Client sends POST request with loan parameters
2. **Monthly Payment**: Calculate using standard loan formula
3. **Amortization Schedule**: Generate month-by-month breakdown
4. **Entity Mapping**: Convert DTOs to JPA entities
5. **Database Save**: Persist loan and installments
6. **Response Mapping**: Convert entities back to DTOs
7. **HTTP Response**: Return calculated results

## Color Coding

- **🔵 Blue**: Main request flow
- **🟠 Orange**: Payment calculations
- **🟡 Yellow**: Schedule generation
- **🟢 Green**: Data mapping
- **🔴 Red**: Database operations
- **🟣 Purple**: Response preparation
