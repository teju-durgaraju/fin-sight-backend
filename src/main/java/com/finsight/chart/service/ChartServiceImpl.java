package com.finsight.chart.service;

import com.finsight.chart.dto.ChartDataResponse.*;
import com.finsight.chart.repository.ChartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final ChartRepository chartRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseBreakdown> getExpenseBreakdown(Long userId, YearMonth month) {
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();

        List<Object[]> results = chartRepository.getExpenseBreakdown(userId, startDate, endDate);
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
    @Transactional(readOnly = true)
    public List<MonthlySpending> getMonthlySpendingTrend(Long userId, int months) {
        LocalDate startDate = LocalDate.now().minusMonths(months - 1).withDayOfMonth(1);
        LocalDate endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        List<Object[]> results = chartRepository.getExpenseBreakdown(userId, startDate, endDate);
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
    @Transactional(readOnly = true)
    public List<NetWorthData> getNetWorthOverTime(Long userId) {
        LocalDate startDate = LocalDate.now().minusYears(1).withDayOfMonth(1);
        LocalDate endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        List<Object[]> results = chartRepository.getNetWorthData(userId, startDate, endDate);
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
    @Transactional(readOnly = true)
    public List<SavingsBudgetComparison> getSavingsVsBudget(Long userId, YearMonth month) {
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();

        List<Object[]> results = chartRepository.getSavingsVsBudget(userId, startDate, endDate, month);

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
    @Transactional(readOnly = true)
    public List<IncomeExpenseComparison> getIncomeVsExpense(Long userId, int months) {
        LocalDate startDate = LocalDate.now().minusMonths(months - 1).withDayOfMonth(1);
        LocalDate endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        List<Object[]> results = chartRepository.getIncomeVsExpense(userId, startDate, endDate);

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