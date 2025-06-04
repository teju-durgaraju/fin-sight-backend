package com.finsight.expense.dto;

import com.finsight.expense.model.ExpenseCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExpenseRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    @NotNull(message = "Category is required")
    private ExpenseCategory category;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
    
    private boolean isRecurring = false;
    
    private String description;
} 