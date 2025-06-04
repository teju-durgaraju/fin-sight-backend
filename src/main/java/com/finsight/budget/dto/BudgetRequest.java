package com.finsight.budget.dto;

import com.finsight.expense.model.ExpenseCategory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;

@Data
public class BudgetRequest {
    
    @NotNull(message = "Category is required")
    private ExpenseCategory category;
    
    @NotNull(message = "Month and year are required")
    private YearMonth monthYear;
    
    @NotNull(message = "Monthly limit is required")
    @Positive(message = "Monthly limit must be positive")
    private BigDecimal monthlyLimit;
    
    private Integer alertThreshold = 80;
} 