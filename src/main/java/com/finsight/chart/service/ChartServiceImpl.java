package com.finsight.chart.service;

import com.finsight.chart.dto.ChartDataResponse.*;
import com.finsight.expense.repository.ExpenseRepository;
import com.finsight.budget.repository.BudgetRepository;
import com.finsight.income.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChartServiceImpl implements ChartService {

    @PersistenceContext
    private EntityManager entityManager;

    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;
    private final IncomeRepository incomeRepository;

    @Override
    public List<ExpenseBreakdown> getExpenseBreakdown(Long userId, YearMonth month) {
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();

        String query = """
            SELECT e.category, SUM(e.amount) as total
            FROM expenses e
            WHERE e.user_id = :userId
            AND e.date BETWEEN :startDate AND :endDate
            GROUP BY e.category
            ORDER BY total DESC
            """;

        @SuppressWarnings("unchecked")
        List<Object[]> results = entityManager.createNativeQuery(query)
            .setParameter("userId", userId)
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
            .getResultList();

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<ExpenseBreakdown> breakdown = new ArrayList<>();

        for (Object[] row : results) {
            BigDecimal amount = (BigDecimal) row[1];
            totalAmount = totalAmount.add(amount);
        }

        for (Object[] row : results) {
            String category = (String) row[0];
            BigDecimal amount = (BigDecimal) row[1];
            double percentage = totalAmount.compareTo(BigDecimal.ZERO) > 0 
                ? amount.multiply(BigDecimal.valueOf(100))
                    .divide(totalAmount, 2, RoundingMode.HALF_UP)
                    .doubleValue()
                : 0.0;

            breakdown.add(ExpenseBreakdown.builder()
                .category(Enum.valueOf(com.finsight.expense.model.ExpenseCategory.class, category))
                .totalAmount(amount)
                .percentage(percentage)
                .build());
        }

        return breakdown;
    }

    @Override
    public List<MonthlySpending> getMonthlySpendingTrend(Long userId, int months) {
        LocalDate startDate = LocalDate.now().minusMonths(months - 1).withDayOfMonth(1);
        LocalDate endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        String query = """
            SELECT DATE_TRUNC('month', e.date) as month,
                   SUM(e.amount) as total
            FROM expenses e
            WHERE e.user_id = :userId
            AND e.date BETWEEN :startDate AND :endDate
            GROUP BY DATE_TRUNC('month', e.date)
            ORDER BY month ASC
            """;

        @SuppressWarnings("unchecked")
        List<Object[]> results = entityManager.createNativeQuery(query)
            .setParameter("userId", userId)
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
            .getResultList();

        List<MonthlySpending> trend = new ArrayList<>();
        BigDecimal previousAmount = null;

        for (Object[] row : results) {
            java.sql.Date date = (java.sql.Date) row[0];
            BigDecimal amount = (BigDecimal) row[1];
            
            Double change = null;
            if (previousAmount != null && previousAmount.compareTo(BigDecimal.ZERO) > 0) {
                change = amount.subtract(previousAmount)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(previousAmount, 2, RoundingMode.HALF_UP)
                    .doubleValue();
            }

            trend.add(MonthlySpending.builder()
                .month(YearMonth.from(date.toLocalDate()))
                .totalSpent(amount)
                .changeFromPreviousMonth(change)
                .build());

            previousAmount = amount;
        }

        return trend;
    }

    @Override
    public List<NetWorthData> getNetWorthOverTime(Long userId) {
        LocalDate startDate = LocalDate.now().minusYears(1).withDayOfMonth(1);
        LocalDate endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        String query = """
            WITH monthly_data AS (
                SELECT DATE_TRUNC('month', i.date) as month,
                       SUM(i.amount) as income,
                       COALESCE(e.expenses, 0) as expenses
                FROM incomes i
                LEFT JOIN (
                    SELECT DATE_TRUNC('month', date) as month,
                           SUM(amount) as expenses
                    FROM expenses
                    WHERE user_id = :userId
                    AND date BETWEEN :startDate AND :endDate
                    GROUP BY DATE_TRUNC('month', date)
                ) e ON DATE_TRUNC('month', i.date) = e.month
                WHERE i.user_id = :userId
                AND i.date BETWEEN :startDate AND :endDate
                GROUP BY DATE_TRUNC('month', i.date), e.expenses
                ORDER BY month ASC
            )
            SELECT month,
                   SUM(income) OVER (ORDER BY month) as assets,
                   SUM(expenses) OVER (ORDER BY month) as liabilities
            FROM monthly_data
            """;

        @SuppressWarnings("unchecked")
        List<Object[]> results = entityManager.createNativeQuery(query)
            .setParameter("userId", userId)
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
            .getResultList();

        List<NetWorthData> netWorthList = new ArrayList<>();
        BigDecimal previousNetWorth = null;

        for (Object[] row : results) {
            java.sql.Date date = (java.sql.Date) row[0];
            BigDecimal assets = (BigDecimal) row[1];
            BigDecimal liabilities = (BigDecimal) row[2];
            BigDecimal netWorth = assets.subtract(liabilities);

            Double monthlyChange = null;
            if (previousNetWorth != null && previousNetWorth.compareTo(BigDecimal.ZERO) != 0) {
                monthlyChange = netWorth.subtract(previousNetWorth)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(previousNetWorth.abs(), 2, RoundingMode.HALF_UP)
                    .doubleValue();
            }

            netWorthList.add(NetWorthData.builder()
                .month(YearMonth.from(date.toLocalDate()))
                .assetsTotal(assets)
                .liabilitiesTotal(liabilities)
                .netWorth(netWorth)
                .monthlyChange(monthlyChange)
                .build());

            previousNetWorth = netWorth;
        }

        return netWorthList;
    }

    @Override
    public List<SavingsBudgetComparison> getSavingsVsBudget(Long userId, YearMonth month) {
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();

        String query = """
            SELECT b.category,
                   COALESCE(e.spent_amount, 0) as spent,
                   b.monthly_limit,
                   CASE
                       WHEN b.monthly_limit > 0 THEN
                           ROUND((COALESCE(e.spent_amount, 0) * 100.0 / b.monthly_limit), 2)
                       ELSE 0
                   END as utilization
            FROM budgets b
            LEFT JOIN (
                SELECT category, SUM(amount) as spent_amount
                FROM expenses
                WHERE user_id = :userId
                AND date BETWEEN :startDate AND :endDate
                GROUP BY category
            ) e ON b.category = e.category
            WHERE b.user_id = :userId
            AND b.month_year = :month
            """;

        @SuppressWarnings("unchecked")
        List<Object[]> results = entityManager.createNativeQuery(query)
            .setParameter("userId", userId)
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
            .setParameter("month", month)
            .getResultList();

        return results.stream().map(row -> {
            String category = (String) row[0];
            BigDecimal spent = (BigDecimal) row[1];
            BigDecimal limit = (BigDecimal) row[2];
            Double utilization = ((Number) row[3]).doubleValue();

            return SavingsBudgetComparison.builder()
                .category(Enum.valueOf(com.finsight.expense.model.ExpenseCategory.class, category))
                .savedAmount(spent)
                .budgetLimit(limit)
                .utilizationPercentage(utilization)
                .build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<IncomeExpenseComparison> getIncomeVsExpense(Long userId, int months) {
        LocalDate startDate = LocalDate.now().minusMonths(months - 1).withDayOfMonth(1);
        LocalDate endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        String query = """
            WITH monthly_data AS (
                SELECT DATE_TRUNC('month', dates.month) as month,
                       COALESCE(i.income, 0) as income,
                       COALESCE(e.expenses, 0) as expenses
                FROM (
                    SELECT DISTINCT DATE_TRUNC('month', date) as month
                    FROM (
                        SELECT date FROM incomes WHERE user_id = :userId AND date BETWEEN :startDate AND :endDate
                        UNION
                        SELECT date FROM expenses WHERE user_id = :userId AND date BETWEEN :startDate AND :endDate
                    ) all_dates
                ) dates
                LEFT JOIN (
                    SELECT DATE_TRUNC('month', date) as month, SUM(amount) as income
                    FROM incomes
                    WHERE user_id = :userId AND date BETWEEN :startDate AND :endDate
                    GROUP BY DATE_TRUNC('month', date)
                ) i ON dates.month = i.month
                LEFT JOIN (
                    SELECT DATE_TRUNC('month', date) as month, SUM(amount) as expenses
                    FROM expenses
                    WHERE user_id = :userId AND date BETWEEN :startDate AND :endDate
                    GROUP BY DATE_TRUNC('month', date)
                ) e ON dates.month = e.month
                ORDER BY month ASC
            )
            SELECT month,
                   income,
                   expenses,
                   income - expenses as net_savings,
                   CASE
                       WHEN income > 0 THEN ROUND(((income - expenses) * 100.0 / income), 2)
                       ELSE 0
                   END as savings_rate
            FROM monthly_data
            """;

        @SuppressWarnings("unchecked")
        List<Object[]> results = entityManager.createNativeQuery(query)
            .setParameter("userId", userId)
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
            .getResultList();

        return results.stream().map(row -> {
            java.sql.Date date = (java.sql.Date) row[0];
            BigDecimal income = (BigDecimal) row[1];
            BigDecimal expense = (BigDecimal) row[2];
            BigDecimal netSavings = (BigDecimal) row[3];
            Double savingsRate = ((Number) row[4]).doubleValue();

            return IncomeExpenseComparison.builder()
                .month(YearMonth.from(date.toLocalDate()))
                .totalIncome(income)
                .totalExpense(expense)
                .netSavings(netSavings)
                .savingsRate(savingsRate)
                .build();
        }).collect(Collectors.toList());
    }
} 