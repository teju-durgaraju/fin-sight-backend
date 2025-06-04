package com.finsight.goal.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class WeeklyInsightsResponse {
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
    
    // Overall progress
    private int totalGoals;
    private int completedGoals;
    private double overallCompletionRate;
    private BigDecimal totalSaved;
    private BigDecimal totalRequired;
    
    // Daily progress tracking
    private Map<LocalDate, BigDecimal> dailySavings;
    
    // Goal-specific insights
    private List<GoalInsight> goalInsights;
    
    // Recommendations
    private List<String> recommendations;
    
    // Performance metrics
    private double weeklyProgressRate;
    private boolean onTrackOverall;
    private Map<String, String> improvementAreas;
    
    @Data
    public static class GoalInsight {
        private Long goalId;
        private String goalName;
        private double weeklyProgress;
        private boolean onTrack;
        private BigDecimal weeklySaved;
        private BigDecimal weeklyTarget;
        private List<String> suggestions;
    }
} 