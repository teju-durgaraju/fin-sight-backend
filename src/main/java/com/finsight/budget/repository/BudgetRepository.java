package com.finsight.budget.repository;

import com.finsight.budget.model.Budget;
import com.finsight.expense.model.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUserId(Long userId);
    
    List<Budget> findByUserIdAndMonthYear(Long userId, YearMonth monthYear);
    
    Optional<Budget> findByUserIdAndCategoryAndMonthYear(Long userId, ExpenseCategory category, YearMonth monthYear);
    
    boolean existsByUserIdAndCategoryAndMonthYear(Long userId, ExpenseCategory category, YearMonth monthYear);
} 