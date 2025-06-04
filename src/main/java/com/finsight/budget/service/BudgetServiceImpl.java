package com.finsight.budget.service;

import com.finsight.budget.dto.BudgetRequest;
import com.finsight.budget.dto.BudgetResponse;
import com.finsight.budget.model.Budget;
import com.finsight.budget.repository.BudgetRepository;
import com.finsight.exception.ResourceNotFoundException;
import com.finsight.expense.model.ExpenseCategory;
import com.finsight.user.model.User;
import com.finsight.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BudgetResponse createBudget(Long userId, BudgetRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (budgetRepository.existsByUserIdAndCategoryAndMonthYear(userId, request.getCategory(), request.getMonthYear())) {
            throw new IllegalStateException("Budget already exists for this category and month");
        }

        Budget budget = new Budget();
        budget.setUser(user);
        mapRequestToBudget(request, budget);

        return mapBudgetToResponse(budgetRepository.save(budget));
    }

    @Override
    @Transactional
    public BudgetResponse updateBudget(Long userId, Long budgetId, BudgetRequest request) {
        Budget budget = budgetRepository.findById(budgetId)
            .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", budgetId));

        if (!budget.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Budget not found for user");
        }

        mapRequestToBudget(request, budget);
        updateThresholdStatus(budget);
        
        return mapBudgetToResponse(budgetRepository.save(budget));
    }

    @Override
    @Transactional
    public void deleteBudget(Long userId, Long budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
            .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", budgetId));

        if (!budget.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Budget not found for user");
        }

        budgetRepository.delete(budget);
    }

    @Override
    public BudgetResponse getBudget(Long userId, Long budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
            .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", budgetId));

        if (!budget.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Budget not found for user");
        }

        return mapBudgetToResponse(budget);
    }

    @Override
    public List<BudgetResponse> getUserBudgets(Long userId) {
        return budgetRepository.findByUserId(userId).stream()
            .map(this::mapBudgetToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<BudgetResponse> getUserBudgetsByMonth(Long userId, YearMonth monthYear) {
        return budgetRepository.findByUserIdAndMonthYear(userId, monthYear).stream()
            .map(this::mapBudgetToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public BudgetResponse getBudgetByCategory(Long userId, ExpenseCategory category, YearMonth monthYear) {
        Budget budget = budgetRepository.findByUserIdAndCategoryAndMonthYear(userId, category, monthYear)
            .orElseThrow(() -> new ResourceNotFoundException("Budget not found for category and month"));

        return mapBudgetToResponse(budget);
    }

    @Override
    @Transactional
    public BudgetResponse updateExpenseAmount(Long userId, ExpenseCategory category, YearMonth monthYear, BigDecimal amount) {
        Budget budget = budgetRepository.findByUserIdAndCategoryAndMonthYear(userId, category, monthYear)
            .orElseThrow(() -> new ResourceNotFoundException("Budget not found for category and month"));

        budget.setCurrentSpent(budget.getCurrentSpent().add(amount));
        updateThresholdStatus(budget);

        return mapBudgetToResponse(budgetRepository.save(budget));
    }

    @Override
    public boolean isThresholdReached(Long userId, ExpenseCategory category, YearMonth monthYear) {
        return budgetRepository.findByUserIdAndCategoryAndMonthYear(userId, category, monthYear)
            .map(Budget::isThresholdReached)
            .orElse(false);
    }

    private void mapRequestToBudget(BudgetRequest request, Budget budget) {
        budget.setCategory(request.getCategory());
        budget.setMonthYear(request.getMonthYear());
        budget.setMonthlyLimit(request.getMonthlyLimit());
        if (request.getAlertThreshold() != null) {
            budget.setAlertThreshold(request.getAlertThreshold());
        }
        updateThresholdStatus(budget);
    }

    private void updateThresholdStatus(Budget budget) {
        if (budget.getCurrentSpent() != null && budget.getMonthlyLimit() != null) {
            double percentage = budget.getCurrentSpent()
                .multiply(BigDecimal.valueOf(100))
                .divide(budget.getMonthlyLimit(), 2, RoundingMode.HALF_UP)
                .doubleValue();
            budget.setThresholdReached(percentage >= budget.getAlertThreshold());
        }
    }

    private BudgetResponse mapBudgetToResponse(Budget budget) {
        BudgetResponse response = new BudgetResponse();
        response.setId(budget.getId());
        response.setCategory(budget.getCategory());
        response.setMonthYear(budget.getMonthYear());
        response.setMonthlyLimit(budget.getMonthlyLimit());
        response.setCurrentSpent(budget.getCurrentSpent());
        response.setAlertThreshold(budget.getAlertThreshold());
        response.setThresholdReached(budget.isThresholdReached());
        
        // Calculate spent percentage
        if (budget.getCurrentSpent() != null && budget.getMonthlyLimit() != null) {
            double percentage = budget.getCurrentSpent()
                .multiply(BigDecimal.valueOf(100))
                .divide(budget.getMonthlyLimit(), 2, RoundingMode.HALF_UP)
                .doubleValue();
            response.setSpentPercentage(percentage);
        }
        
        response.setCreatedAt(budget.getCreatedAt());
        response.setUpdatedAt(budget.getUpdatedAt());
        return response;
    }
} 