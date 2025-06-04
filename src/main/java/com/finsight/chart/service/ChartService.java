package com.finsight.chart.service;

import com.finsight.chart.dto.ChartDataResponse.*;
import java.time.YearMonth;
import java.util.List;

public interface ChartService {
    List<ExpenseBreakdown> getExpenseBreakdown(Long userId, YearMonth month);
    
    List<MonthlySpending> getMonthlySpendingTrend(Long userId, int months);
    
    List<NetWorthData> getNetWorthOverTime(Long userId);
    
    List<SavingsBudgetComparison> getSavingsVsBudget(Long userId, YearMonth month);
    
    List<IncomeExpenseComparison> getIncomeVsExpense(Long userId, int months);
} 