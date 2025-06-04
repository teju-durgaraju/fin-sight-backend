package com.finsight.goal.service;

import com.finsight.goal.dto.GoalRequest;
import com.finsight.goal.dto.GoalResponse;
import com.finsight.goal.dto.WeeklyInsightsResponse;
import com.finsight.goal.model.GoalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface GoalService {
    GoalResponse createGoal(Long userId, GoalRequest request);
    
    GoalResponse updateGoal(Long userId, Long goalId, GoalRequest request);
    
    void deleteGoal(Long userId, Long goalId);
    
    GoalResponse getGoal(Long userId, Long goalId);
    
    List<GoalResponse> getUserGoals(Long userId);
    
    List<GoalResponse> getUserGoalsByStatus(Long userId, GoalStatus status);
    
    List<GoalResponse> getUpcomingGoals(Long userId, LocalDate date);
    
    List<GoalResponse> getGoalsByDateRange(Long userId, LocalDate startDate, LocalDate endDate);
    
    GoalResponse updateSavedAmount(Long userId, Long goalId, BigDecimal amount);
    
    // Premium features
    WeeklyInsightsResponse getWeeklyInsights(Long userId);
    
    List<GoalResponse> getGoalRecommendations(Long userId);
    
    WeeklyInsightsResponse getProgressAnalysis(Long userId, LocalDate startDate, LocalDate endDate);
} 