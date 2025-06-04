package com.finsight.chart.repository;

import com.finsight.chart.model.Chart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Repository
public interface ChartRepository extends JpaRepository<Chart, Long> {
    @Query(nativeQuery = true,
           value = "SELECT e.category, SUM(e.amount) as total " +
                  "FROM expenses e " +
                  "WHERE e.user_id = :userId " +
                  "AND e.date BETWEEN :startDate AND :endDate " +
                  "GROUP BY e.category " +
                  "ORDER BY total DESC")
    List<Object[]> getExpenseBreakdown(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query(nativeQuery = true,
           value = "WITH monthly_data AS ( " +
                  "    SELECT DATE_TRUNC('month', i.date) as month, " +
                  "           SUM(i.amount) as income, " +
                  "           COALESCE(e.expenses, 0) as expenses " +
                  "    FROM incomes i " +
                  "    LEFT JOIN ( " +
                  "        SELECT DATE_TRUNC('month', date) as month, " +
                  "               SUM(amount) as expenses " +
                  "        FROM expenses " +
                  "        WHERE user_id = :userId " +
                  "        AND date BETWEEN :startDate AND :endDate " +
                  "        GROUP BY DATE_TRUNC('month', date) " +
                  "    ) e ON DATE_TRUNC('month', i.date) = e.month " +
                  "    WHERE i.user_id = :userId " +
                  "    AND i.date BETWEEN :startDate AND :endDate " +
                  "    GROUP BY DATE_TRUNC('month', i.date), e.expenses " +
                  "    ORDER BY month ASC " +
                  ") " +
                  "SELECT month, " +
                  "       SUM(income) OVER (ORDER BY month) as assets, " +
                  "       SUM(expenses) OVER (ORDER BY month) as liabilities " +
                  "FROM monthly_data")
    List<Object[]> getNetWorthData(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query(nativeQuery = true,
           value = "SELECT b.category, " +
                  "       COALESCE(e.spent_amount, 0) as spent, " +
                  "       b.monthly_limit, " +
                  "       CASE " +
                  "           WHEN b.monthly_limit > 0 THEN " +
                  "               ROUND((COALESCE(e.spent_amount, 0) * 100.0 / b.monthly_limit), 2) " +
                  "           ELSE 0 " +
                  "       END as utilization " +
                  "FROM budgets b " +
                  "LEFT JOIN ( " +
                  "    SELECT category, SUM(amount) as spent_amount " +
                  "    FROM expenses " +
                  "    WHERE user_id = :userId " +
                  "    AND date BETWEEN :startDate AND :endDate " +
                  "    GROUP BY category " +
                  ") e ON b.category = e.category " +
                  "WHERE b.user_id = :userId " +
                  "AND b.month_year = :monthYear")
    List<Object[]> getSavingsVsBudget(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("monthYear") YearMonth monthYear
    );

    @Query(nativeQuery = true,
           value = "WITH monthly_data AS ( " +
                  "    SELECT DATE_TRUNC('month', dates.month) as month, " +
                  "           COALESCE(i.income, 0) as income, " +
                  "           COALESCE(e.expenses, 0) as expenses " +
                  "    FROM ( " +
                  "        SELECT DISTINCT DATE_TRUNC('month', date) as month " +
                  "        FROM ( " +
                  "            SELECT date FROM incomes WHERE user_id = :userId AND date BETWEEN :startDate AND :endDate " +
                  "            UNION " +
                  "            SELECT date FROM expenses WHERE user_id = :userId AND date BETWEEN :startDate AND :endDate " +
                  "        ) all_dates " +
                  "    ) dates " +
                  "    LEFT JOIN ( " +
                  "        SELECT DATE_TRUNC('month', date) as month, SUM(amount) as income " +
                  "        FROM incomes " +
                  "        WHERE user_id = :userId AND date BETWEEN :startDate AND :endDate " +
                  "        GROUP BY DATE_TRUNC('month', date) " +
                  "    ) i ON dates.month = i.month " +
                  "    LEFT JOIN ( " +
                  "        SELECT DATE_TRUNC('month', date) as month, SUM(amount) as expenses " +
                  "        FROM expenses " +
                  "        WHERE user_id = :userId AND date BETWEEN :startDate AND :endDate " +
                  "        GROUP BY DATE_TRUNC('month', date) " +
                  "    ) e ON dates.month = e.month " +
                  "    ORDER BY month ASC " +
                  ") " +
                  "SELECT month, " +
                  "       income, " +
                  "       expenses, " +
                  "       income - expenses as net_savings, " +
                  "       CASE " +
                  "           WHEN income > 0 THEN ROUND(((income - expenses) * 100.0 / income), 2) " +
                  "           ELSE 0 " +
                  "       END as savings_rate " +
                  "FROM monthly_data")
    List<Object[]> getIncomeVsExpense(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
} 