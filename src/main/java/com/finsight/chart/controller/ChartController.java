package com.finsight.chart.controller;

import com.finsight.chart.dto.ChartDataResponse.*;
import com.finsight.chart.service.ChartService;
import com.finsight.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/charts")
@RequiredArgsConstructor
@Tag(name = "Financial Charts", description = "APIs for financial analytics and charts")
public class ChartController {

    private final ChartService chartService;

    @GetMapping("/expense-breakdown")
    @Operation(summary = "Get expense breakdown by category for pie chart")
    public ResponseEntity<List<ExpenseBreakdown>> getExpenseBreakdown(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return ResponseEntity.ok(chartService.getExpenseBreakdown(userDetails.getId(), month));
    }

    @GetMapping("/monthly-spending")
    @Operation(summary = "Get monthly spending trend")
    public ResponseEntity<List<MonthlySpending>> getMonthlySpending(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "6") int months) {
        return ResponseEntity.ok(chartService.getMonthlySpendingTrend(userDetails.getId(), months));
    }

    @GetMapping("/net-worth")
    @Operation(summary = "Get net worth over time")
    public ResponseEntity<List<NetWorthData>> getNetWorth(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(chartService.getNetWorthOverTime(userDetails.getId()));
    }

    @GetMapping("/savings-vs-budget")
    @Operation(summary = "Get savings vs budget comparison")
    public ResponseEntity<List<SavingsBudgetComparison>> getSavingsVsBudget(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return ResponseEntity.ok(chartService.getSavingsVsBudget(userDetails.getId(), month));
    }

    @GetMapping("/income-vs-expense")
    @Operation(summary = "Get income vs expense comparison")
    public ResponseEntity<List<IncomeExpenseComparison>> getIncomeVsExpense(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "6") int months) {
        return ResponseEntity.ok(chartService.getIncomeVsExpense(userDetails.getId(), months));
    }
} 