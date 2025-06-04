package com.finsight.expense.controller;

import com.finsight.expense.dto.ExpenseRequest;
import com.finsight.expense.dto.ExpenseResponse;
import com.finsight.expense.model.ExpenseCategory;
import com.finsight.expense.service.ExpenseService;
import com.finsight.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
@Tag(name = "Expense Management", description = "APIs for managing expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    @Operation(summary = "Create a new expense")
    public ResponseEntity<ExpenseResponse> createExpense(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(expenseService.createExpense(userDetails.getId(), request));
    }

    @PutMapping("/{expenseId}")
    @Operation(summary = "Update an existing expense")
    public ResponseEntity<ExpenseResponse> updateExpense(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long expenseId,
            @Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(expenseService.updateExpense(userDetails.getId(), expenseId, request));
    }

    @DeleteMapping("/{expenseId}")
    @Operation(summary = "Delete an expense")
    public ResponseEntity<Void> deleteExpense(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long expenseId) {
        expenseService.deleteExpense(userDetails.getId(), expenseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{expenseId}")
    @Operation(summary = "Get expense by ID")
    public ResponseEntity<ExpenseResponse> getExpense(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long expenseId) {
        return ResponseEntity.ok(expenseService.getExpense(userDetails.getId(), expenseId));
    }

    @GetMapping
    @Operation(summary = "Get all expenses for the current user")
    public ResponseEntity<Page<ExpenseResponse>> getAllExpenses(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Pageable pageable) {
        return ResponseEntity.ok(expenseService.getUserExpenses(userDetails.getId(), pageable));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get expenses within a date range")
    public ResponseEntity<List<ExpenseResponse>> getExpensesByDateRange(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(expenseService.getExpensesByDateRange(userDetails.getId(), startDate, endDate));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get expenses by category")
    public ResponseEntity<List<ExpenseResponse>> getExpensesByCategory(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable ExpenseCategory category) {
        return ResponseEntity.ok(expenseService.getExpensesByCategory(userDetails.getId(), category));
    }

    @GetMapping("/date-range-category")
    @Operation(summary = "Get expenses by date range and category")
    public ResponseEntity<List<ExpenseResponse>> getExpensesByDateRangeAndCategory(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam ExpenseCategory category) {
        return ResponseEntity.ok(expenseService.getExpensesByDateRangeAndCategory(
            userDetails.getId(), startDate, endDate, category));
    }
} 