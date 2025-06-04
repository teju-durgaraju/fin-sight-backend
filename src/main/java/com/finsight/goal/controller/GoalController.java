package com.finsight.goal.controller;

import com.finsight.goal.dto.GoalRequest;
import com.finsight.goal.dto.GoalResponse;
import com.finsight.goal.dto.WeeklyInsightsResponse;
import com.finsight.goal.model.GoalStatus;
import com.finsight.goal.service.GoalService;
import com.finsight.security.UserDetailsImpl;
import com.finsight.security.annotation.PremiumFeature;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@Tag(name = "Financial Goals", description = "APIs for managing financial goals")
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    @Operation(summary = "Create a new financial goal")
    public ResponseEntity<GoalResponse> createGoal(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody GoalRequest request) {
        return ResponseEntity.ok(goalService.createGoal(userDetails.getId(), request));
    }

    @PutMapping("/{goalId}")
    @Operation(summary = "Update an existing goal")
    public ResponseEntity<GoalResponse> updateGoal(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long goalId,
            @Valid @RequestBody GoalRequest request) {
        return ResponseEntity.ok(goalService.updateGoal(userDetails.getId(), goalId, request));
    }

    @DeleteMapping("/{goalId}")
    @Operation(summary = "Delete a goal")
    public ResponseEntity<Void> deleteGoal(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long goalId) {
        goalService.deleteGoal(userDetails.getId(), goalId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{goalId}")
    @Operation(summary = "Get goal by ID")
    public ResponseEntity<GoalResponse> getGoal(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long goalId) {
        return ResponseEntity.ok(goalService.getGoal(userDetails.getId(), goalId));
    }

    @GetMapping
    @Operation(summary = "Get all goals for the current user")
    public ResponseEntity<List<GoalResponse>> getAllGoals(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(goalService.getUserGoals(userDetails.getId()));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get goals by status")
    public ResponseEntity<List<GoalResponse>> getGoalsByStatus(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable GoalStatus status) {
        return ResponseEntity.ok(goalService.getUserGoalsByStatus(userDetails.getId(), status));
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming goals before a date")
    public ResponseEntity<List<GoalResponse>> getUpcomingGoals(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate beforeDate) {
        return ResponseEntity.ok(goalService.getUpcomingGoals(userDetails.getId(), beforeDate));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get goals within a date range")
    public ResponseEntity<List<GoalResponse>> getGoalsByDateRange(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(goalService.getGoalsByDateRange(userDetails.getId(), startDate, endDate));
    }

    @PatchMapping("/{goalId}/saved-amount")
    @Operation(summary = "Update the saved amount for a goal")
    public ResponseEntity<GoalResponse> updateSavedAmount(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long goalId,
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(goalService.updateSavedAmount(userDetails.getId(), goalId, amount));
    }

    @GetMapping("/insights/weekly")
    @PremiumFeature
    @Operation(summary = "Get weekly insights for goals (Premium Feature)")
    public ResponseEntity<WeeklyInsightsResponse> getWeeklyInsights(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(goalService.getWeeklyInsights(userDetails.getId()));
    }

    @GetMapping("/insights/recommendations")
    @PremiumFeature
    @Operation(summary = "Get personalized goal recommendations (Premium Feature)")
    public ResponseEntity<List<GoalResponse>> getGoalRecommendations(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(goalService.getGoalRecommendations(userDetails.getId()));
    }

    @GetMapping("/insights/progress-analysis")
    @PremiumFeature
    @Operation(summary = "Get detailed progress analysis (Premium Feature)")
    public ResponseEntity<WeeklyInsightsResponse> getProgressAnalysis(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(goalService.getProgressAnalysis(userDetails.getId(), startDate, endDate));
    }
} 