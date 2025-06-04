package com.finsight.income.repository;

import com.finsight.income.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findByUserId(Long userId);
    
    List<Income> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    
    List<Income> findByUserIdAndType(Long userId, Income.IncomeType type);

    @Query("SELECT i.type, COUNT(i), SUM(i.amount), AVG(i.amount) " +
           "FROM Income i WHERE i.user.id = :userId " +
           "AND i.date BETWEEN :startDate AND :endDate " +
           "GROUP BY i.type")
    List<Object[]> getIncomeStatsByType(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT FUNCTION('date_trunc', 'month', i.date) as month, SUM(i.amount) " +
           "FROM Income i WHERE i.user.id = :userId " +
           "AND i.date >= :startDate AND i.date <= :endDate " +
           "GROUP BY FUNCTION('date_trunc', 'month', i.date) " +
           "ORDER BY month")
    List<Object[]> findMonthlyIncomeTrend(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT i FROM Income i WHERE i.user.id = :userId " +
           "AND i.recurring = true " +
           "AND i.date <= :date " +
           "ORDER BY i.amount DESC")
    List<Income> findRecurringIncome(
        @Param("userId") Long userId,
        @Param("date") LocalDate date
    );

    @Query("SELECT SUM(i.amount) FROM Income i " +
           "WHERE i.user.id = :userId " +
           "AND EXTRACT(YEAR FROM i.date) = :year " +
           "AND EXTRACT(MONTH FROM i.date) = :month")
    BigDecimal getTotalIncomeForMonth(
        @Param("userId") Long userId,
        @Param("year") int year,
        @Param("month") int month
    );

    @Query("SELECT i.type, SUM(i.amount) FROM Income i " +
           "WHERE i.user.id = :userId " +
           "AND i.date BETWEEN :startDate AND :endDate " +
           "GROUP BY i.type ORDER BY SUM(i.amount) DESC")
    List<Object[]> getTopIncomeSources(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT AVG(monthlyTotal) FROM (" +
           "SELECT EXTRACT(YEAR FROM i.date) as year, " +
           "EXTRACT(MONTH FROM i.date) as month, " +
           "SUM(i.amount) as monthlyTotal " +
           "FROM Income i WHERE i.user.id = :userId " +
           "AND i.date >= :startDate " +
           "GROUP BY EXTRACT(YEAR FROM i.date), EXTRACT(MONTH FROM i.date)) as monthly")
    BigDecimal getAverageMonthlyIncome(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate
    );

    @Query("SELECT i FROM Income i WHERE i.user.id = :userId " +
           "AND i.date BETWEEN :startDate AND :endDate " +
           "AND i.amount >= :minAmount " +
           "ORDER BY i.amount DESC")
    List<Income> findLargeIncomeTransactions(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("minAmount") BigDecimal minAmount
    );
} 