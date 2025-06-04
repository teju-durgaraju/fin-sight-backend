package com.finsight.budget.repository;

import com.finsight.budget.model.Budget;
import com.finsight.expense.model.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUserId(Long userId);
    
    List<Budget> findByUserIdAndMonthYear(Long userId, YearMonth monthYear);
    
    Optional<Budget> findByUserIdAndCategoryAndMonthYear(Long userId, ExpenseCategory category, YearMonth monthYear);
    
    boolean existsByUserIdAndCategoryAndMonthYear(Long userId, ExpenseCategory category, YearMonth monthYear);

    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId " +
           "AND b.monthYear >= :startMonth AND b.monthYear <= :endMonth " +
           "ORDER BY b.monthYear")
    List<Budget> findBudgetsInRange(
        @Param("userId") Long userId,
        @Param("startMonth") YearMonth startMonth,
        @Param("endMonth") YearMonth endMonth
    );

    @Query("SELECT b.category, SUM(b.monthlyLimit) as totalLimit, SUM(b.currentSpent) as totalSpent " +
           "FROM Budget b WHERE b.user.id = :userId " +
           "AND b.monthYear = :monthYear GROUP BY b.category")
    List<Object[]> getBudgetSummaryByCategory(
        @Param("userId") Long userId,
        @Param("monthYear") YearMonth monthYear
    );

    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId " +
           "AND b.monthYear = :monthYear AND b.currentSpent >= b.monthlyLimit * 0.9")
    List<Budget> findNearingThresholdBudgets(
        @Param("userId") Long userId,
        @Param("monthYear") YearMonth monthYear
    );

    @Modifying
    @Query("UPDATE Budget b SET b.currentSpent = b.currentSpent + :amount " +
           "WHERE b.user.id = :userId AND b.category = :category AND b.monthYear = :monthYear")
    void updateCurrentSpent(
        @Param("userId") Long userId,
        @Param("category") ExpenseCategory category,
        @Param("monthYear") YearMonth monthYear,
        @Param("amount") BigDecimal amount
    );

    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId " +
           "AND b.monthYear = :monthYear AND b.currentSpent > b.monthlyLimit")
    List<Budget> findOverspentBudgets(
        @Param("userId") Long userId,
        @Param("monthYear") YearMonth monthYear
    );

    @Query("SELECT AVG(b.monthlyLimit) FROM Budget b " +
           "WHERE b.user.id = :userId AND b.category = :category " +
           "AND b.monthYear < :monthYear")
    BigDecimal getAverageHistoricalBudget(
        @Param("userId") Long userId,
        @Param("category") ExpenseCategory category,
        @Param("monthYear") YearMonth monthYear
    );
} 