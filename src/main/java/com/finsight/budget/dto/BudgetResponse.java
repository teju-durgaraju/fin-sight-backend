package com.finsight.budget.dto;

import com.finsight.expense.model.ExpenseCategory;
import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.LocalDateTime;

@Data
public class BudgetResponse {
    private Long id;
    private ExpenseCategory category;
    private YearMonth monthYear;
    private BigDecimal monthlyLimit;
    private BigDecimal currentSpent;
    private Integer alertThreshold;
    private boolean isThresholdReached;
    private double spentPercentage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 