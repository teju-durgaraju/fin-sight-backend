package com.finsight.budget.service;

import com.finsight.budget.dto.BudgetRequest;
import com.finsight.budget.dto.BudgetResponse;
import com.finsight.expense.model.ExpenseCategory;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public interface BudgetService {
    BudgetResponse createBudget(Long userId, BudgetRequest request);
    
    BudgetResponse updateBudget(Long userId, Long budgetId, BudgetRequest request);
    
    void deleteBudget(Long userId, Long budgetId);
    
    BudgetResponse getBudget(Long userId, Long budgetId);
    
    List<BudgetResponse> getUserBudgets(Long userId);
    
    List<BudgetResponse> getUserBudgetsByMonth(Long userId, YearMonth monthYear);
    
    BudgetResponse getBudgetByCategory(Long userId, ExpenseCategory category, YearMonth monthYear);
    
    BudgetResponse updateExpenseAmount(Long userId, ExpenseCategory category, YearMonth monthYear, BigDecimal amount);
    
    boolean isThresholdReached(Long userId, ExpenseCategory category, YearMonth monthYear);
} 