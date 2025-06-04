package com.finsight.expense.service;

import com.finsight.expense.dto.ExpenseRequest;
import com.finsight.expense.dto.ExpenseResponse;
import com.finsight.expense.model.ExpenseCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {
    ExpenseResponse createExpense(Long userId, ExpenseRequest request);
    
    ExpenseResponse updateExpense(Long userId, Long expenseId, ExpenseRequest request);
    
    void deleteExpense(Long userId, Long expenseId);
    
    ExpenseResponse getExpense(Long userId, Long expenseId);
    
    Page<ExpenseResponse> getUserExpenses(Long userId, Pageable pageable);
    
    List<ExpenseResponse> getExpensesByDateRange(Long userId, LocalDate startDate, LocalDate endDate);
    
    List<ExpenseResponse> getExpensesByCategory(Long userId, ExpenseCategory category);
    
    List<ExpenseResponse> getExpensesByDateRangeAndCategory(
        Long userId, 
        LocalDate startDate, 
        LocalDate endDate, 
        ExpenseCategory category
    );
} 