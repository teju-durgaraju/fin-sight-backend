package com.finsight.budget.controller;

import com.finsight.budget.dto.BudgetRequest;
import com.finsight.budget.dto.BudgetResponse;
import com.finsight.budget.service.BudgetService;
import com.finsight.expense.model.ExpenseCategory;
import com.finsight.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@Tag(name = "Budget Management", description = "APIs for managing budgets")
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    @Operation(summary = "Create a new budget")
    public ResponseEntity<BudgetResponse> createBudget(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody BudgetRequest request) {
        return ResponseEntity.ok(budgetService.createBudget(userDetails.getId(), request));
    }

    @PutMapping("/{budgetId}")
    @Operation(summary = "Update an existing budget")
    public ResponseEntity<BudgetResponse> updateBudget(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long budgetId,
            @Valid @RequestBody BudgetRequest request) {
        return ResponseEntity.ok(budgetService.updateBudget(userDetails.getId(), budgetId, request));
    }

    @DeleteMapping("/{budgetId}")
    @Operation(summary = "Delete a budget")
    public ResponseEntity<Void> deleteBudget(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long budgetId) {
        budgetService.deleteBudget(userDetails.getId(), budgetId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{budgetId}")
    @Operation(summary = "Get budget by ID")
    public ResponseEntity<BudgetResponse> getBudget(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long budgetId) {
        return ResponseEntity.ok(budgetService.getBudget(userDetails.getId(), budgetId));
    }

    @GetMapping
    @Operation(summary = "Get all budgets for the current user")
    public ResponseEntity<List<BudgetResponse>> getAllBudgets(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(budgetService.getUserBudgets(userDetails.getId()));
    }

    @GetMapping("/month/{yearMonth}")
    @Operation(summary = "Get budgets for a specific month")
    public ResponseEntity<List<BudgetResponse>> getBudgetsByMonth(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
        return ResponseEntity.ok(budgetService.getUserBudgetsByMonth(userDetails.getId(), yearMonth));
    }

    @GetMapping("/category")
    @Operation(summary = "Get budget by category and month")
    public ResponseEntity<BudgetResponse> getBudgetByCategory(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam ExpenseCategory category,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
        return ResponseEntity.ok(budgetService.getBudgetByCategory(userDetails.getId(), category, yearMonth));
    }

    @GetMapping("/threshold-check")
    @Operation(summary = "Check if budget threshold is reached")
    public ResponseEntity<Boolean> checkThreshold(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam ExpenseCategory category,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
        return ResponseEntity.ok(budgetService.isThresholdReached(userDetails.getId(), category, yearMonth));
    }
} 