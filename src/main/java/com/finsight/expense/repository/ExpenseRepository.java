package com.finsight.expense.repository;

import com.finsight.expense.model.Expense;
import com.finsight.expense.model.ExpenseCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    Page<Expense> findByUserId(Long userId, Pageable pageable);
    
    List<Expense> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    
    List<Expense> findByUserIdAndCategory(Long userId, ExpenseCategory category);
    
    List<Expense> findByUserIdAndDateBetweenAndCategory(
        Long userId, 
        LocalDate startDate, 
        LocalDate endDate, 
        ExpenseCategory category
    );

    @Query("SELECT e.category, SUM(e.amount) FROM Expense e " +
           "WHERE e.user.id = :userId AND e.date BETWEEN :startDate AND :endDate " +
           "GROUP BY e.category")
    List<Object[]> findExpenseSummaryByCategory(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT FUNCTION('date_trunc', 'month', e.date) as month, SUM(e.amount) " +
           "FROM Expense e WHERE e.user.id = :userId " +
           "AND e.date >= :startDate AND e.date <= :endDate " +
           "GROUP BY FUNCTION('date_trunc', 'month', e.date) " +
           "ORDER BY month")
    List<Object[]> findMonthlyExpenseTrend(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT e.category, COUNT(e), SUM(e.amount), AVG(e.amount) " +
           "FROM Expense e WHERE e.user.id = :userId " +
           "AND e.date BETWEEN :startDate AND :endDate " +
           "GROUP BY e.category")
    List<Object[]> getExpenseStatsByCategory(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT SUM(e.amount) FROM Expense e " +
           "WHERE e.user.id = :userId AND e.category = :category " +
           "AND EXTRACT(YEAR FROM e.date) = :year AND EXTRACT(MONTH FROM e.date) = :month")
    BigDecimal getTotalExpenseForCategoryAndMonth(
        @Param("userId") Long userId,
        @Param("category") ExpenseCategory category,
        @Param("year") int year,
        @Param("month") int month
    );

    @Query("SELECT COUNT(e) > 0 FROM Expense e " +
           "WHERE e.user.id = :userId AND e.category = :category " +
           "AND e.date BETWEEN :startDate AND :endDate " +
           "AND e.amount > :amount")
    boolean existsLargeExpenseInPeriod(
        @Param("userId") Long userId,
        @Param("category") ExpenseCategory category,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("amount") BigDecimal amount
    );

    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId " +
           "AND e.date BETWEEN :startDate AND :endDate " +
           "ORDER BY e.amount DESC")
    List<Expense> findTopExpenses(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        Pageable pageable
    );
} 