package com.finsight.expense.service;

import com.finsight.budget.service.BudgetService;
import com.finsight.expense.dto.ExpenseRequest;
import com.finsight.expense.dto.ExpenseResponse;
import com.finsight.expense.model.Expense;
import com.finsight.expense.model.ExpenseCategory;
import com.finsight.expense.repository.ExpenseRepository;
import com.finsight.exception.ResourceNotFoundException;
import com.finsight.user.model.User;
import com.finsight.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final BudgetService budgetService;

    @Override
    @Transactional
    public ExpenseResponse createExpense(Long userId, ExpenseRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Expense expense = new Expense();
        mapRequestToExpense(request, expense);
        expense.setUser(user);

        expense = expenseRepository.save(expense);

        // Update budget for this category
        try {
            budgetService.updateExpenseAmount(
                userId,
                expense.getCategory(),
                YearMonth.from(expense.getDate()),
                expense.getAmount()
            );
        } catch (ResourceNotFoundException e) {
            // Budget doesn't exist for this category and month - that's ok
        }

        return mapExpenseToResponse(expense);
    }

    @Override
    @Transactional
    public ExpenseResponse updateExpense(Long userId, Long expenseId, ExpenseRequest request) {
        Expense expense = expenseRepository.findById(expenseId)
            .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseId));

        if (!expense.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Expense not found for user");
        }

        // Calculate the difference in amount for budget update
        var oldAmount = expense.getAmount().negate(); // Negative of old amount
        var oldCategory = expense.getCategory();
        var oldYearMonth = YearMonth.from(expense.getDate());

        mapRequestToExpense(request, expense);
        expense = expenseRepository.save(expense);

        // Update budgets
        try {
            // Reverse the old amount from the old category's budget
            budgetService.updateExpenseAmount(userId, oldCategory, oldYearMonth, oldAmount);
            
            // Add the new amount to the new category's budget
            budgetService.updateExpenseAmount(
                userId,
                expense.getCategory(),
                YearMonth.from(expense.getDate()),
                expense.getAmount()
            );
        } catch (ResourceNotFoundException e) {
            // Budget doesn't exist for this category and month - that's ok
        }

        return mapExpenseToResponse(expense);
    }

    @Override
    @Transactional
    public void deleteExpense(Long userId, Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
            .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseId));

        if (!expense.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Expense not found for user");
        }

        // Update budget with negative amount to reduce the spent amount
        try {
            budgetService.updateExpenseAmount(
                userId,
                expense.getCategory(),
                YearMonth.from(expense.getDate()),
                expense.getAmount().negate()
            );
        } catch (ResourceNotFoundException e) {
            // Budget doesn't exist for this category and month - that's ok
        }

        expenseRepository.delete(expense);
    }

    @Override
    public ExpenseResponse getExpense(Long userId, Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
            .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseId));

        if (!expense.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Expense not found for user");
        }

        return mapExpenseToResponse(expense);
    }

    @Override
    public Page<ExpenseResponse> getUserExpenses(Long userId, Pageable pageable) {
        return expenseRepository.findByUserId(userId, pageable)
            .map(this::mapExpenseToResponse);
    }

    @Override
    public List<ExpenseResponse> getExpensesByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByUserIdAndDateBetween(userId, startDate, endDate)
            .stream()
            .map(this::mapExpenseToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<ExpenseResponse> getExpensesByCategory(Long userId, ExpenseCategory category) {
        return expenseRepository.findByUserIdAndCategory(userId, category)
            .stream()
            .map(this::mapExpenseToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<ExpenseResponse> getExpensesByDateRangeAndCategory(
        Long userId,
        LocalDate startDate,
        LocalDate endDate,
        ExpenseCategory category
    ) {
        return expenseRepository.findByUserIdAndDateBetweenAndCategory(userId, startDate, endDate, category)
            .stream()
            .map(this::mapExpenseToResponse)
            .collect(Collectors.toList());
    }

    private void mapRequestToExpense(ExpenseRequest request, Expense expense) {
        expense.setName(request.getName());
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDate(request.getDate());
        expense.setRecurring(request.isRecurring());
        expense.setDescription(request.getDescription());
    }

    private ExpenseResponse mapExpenseToResponse(Expense expense) {
        ExpenseResponse response = new ExpenseResponse();
        response.setId(expense.getId());
        response.setName(expense.getName());
        response.setAmount(expense.getAmount());
        response.setCategory(expense.getCategory());
        response.setDate(expense.getDate());
        response.setRecurring(expense.isRecurring());
        response.setDescription(expense.getDescription());
        response.setCreatedAt(expense.getCreatedAt());
        response.setUpdatedAt(expense.getUpdatedAt());
        return response;
    }
} 