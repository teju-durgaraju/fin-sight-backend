package com.finsight.expense.dto;

import com.finsight.expense.model.ExpenseCategory;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ExpenseResponse {
    private Long id;
    private String name;
    private BigDecimal amount;
    private ExpenseCategory category;
    private LocalDate date;
    private boolean isRecurring;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 