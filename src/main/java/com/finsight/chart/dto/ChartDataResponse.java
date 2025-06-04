package com.finsight.chart.dto;

import com.finsight.expense.model.ExpenseCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;

public class ChartDataResponse {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExpenseBreakdown {
        private ExpenseCategory category;
        private BigDecimal totalAmount;
        private Double percentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlySpending {
        private YearMonth month;
        private BigDecimal totalSpent;
        private Double changeFromPreviousMonth;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NetWorthData {
        private YearMonth month;
        private BigDecimal assetsTotal;
        private BigDecimal liabilitiesTotal;
        private BigDecimal netWorth;
        private Double monthlyChange;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SavingsBudgetComparison {
        private ExpenseCategory category;
        private BigDecimal savedAmount;
        private BigDecimal budgetLimit;
        private Double utilizationPercentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IncomeExpenseComparison {
        private YearMonth month;
        private BigDecimal totalIncome;
        private BigDecimal totalExpense;
        private BigDecimal netSavings;
        private Double savingsRate;
    }
} 