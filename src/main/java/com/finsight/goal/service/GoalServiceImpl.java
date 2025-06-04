package com.finsight.goal.service;

import com.finsight.goal.dto.GoalRequest;
import com.finsight.goal.dto.GoalResponse;
import com.finsight.goal.dto.WeeklyInsightsResponse;
import com.finsight.goal.dto.WeeklyInsightsResponse.GoalInsight;
import com.finsight.goal.model.Goal;
import com.finsight.goal.model.GoalStatus;
import com.finsight.goal.repository.GoalRepository;
import com.finsight.exception.ResourceNotFoundException;
import com.finsight.user.model.User;
import com.finsight.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public GoalResponse createGoal(Long userId, GoalRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Goal goal = new Goal();
        mapRequestToGoal(request, goal);
        goal.setUser(user);

        return mapGoalToResponse(goalRepository.save(goal));
    }

    @Override
    @Transactional
    public GoalResponse updateGoal(Long userId, Long goalId, GoalRequest request) {
        Goal goal = goalRepository.findById(goalId)
            .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", goalId));

        if (!goal.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Goal not found for user");
        }

        mapRequestToGoal(request, goal);
        return mapGoalToResponse(goalRepository.save(goal));
    }

    @Override
    @Transactional
    public void deleteGoal(Long userId, Long goalId) {
        Goal goal = goalRepository.findById(goalId)
            .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", goalId));

        if (!goal.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Goal not found for user");
        }

        goalRepository.delete(goal);
    }

    @Override
    public GoalResponse getGoal(Long userId, Long goalId) {
        Goal goal = goalRepository.findById(goalId)
            .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", goalId));

        if (!goal.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Goal not found for user");
        }

        return mapGoalToResponse(goal);
    }

    @Override
    public List<GoalResponse> getUserGoals(Long userId) {
        return goalRepository.findByUserId(userId).stream()
            .map(this::mapGoalToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<GoalResponse> getUserGoalsByStatus(Long userId, GoalStatus status) {
        return goalRepository.findByUserIdAndStatus(userId, status).stream()
            .map(this::mapGoalToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<GoalResponse> getUpcomingGoals(Long userId, LocalDate date) {
        return goalRepository.findByUserIdAndTargetDateBefore(userId, date).stream()
            .map(this::mapGoalToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<GoalResponse> getGoalsByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return goalRepository.findByUserIdAndTargetDateBetween(userId, startDate, endDate).stream()
            .map(this::mapGoalToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GoalResponse updateSavedAmount(Long userId, Long goalId, BigDecimal amount) {
        Goal goal = goalRepository.findById(goalId)
            .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", goalId));

        if (!goal.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Goal not found for user");
        }

        goal.setCurrentSaved(goal.getCurrentSaved().add(amount));
        return mapGoalToResponse(goalRepository.save(goal));
    }

    @Override
    public WeeklyInsightsResponse getWeeklyInsights(Long userId) {
        // Get current week's start and end dates
        LocalDate now = LocalDate.now();
        LocalDate weekStart = now.with(WeekFields.ISO.dayOfWeek(), 1);
        LocalDate weekEnd = now.with(WeekFields.ISO.dayOfWeek(), 7);

        return generateInsights(userId, weekStart, weekEnd);
    }

    @Override
    public List<GoalResponse> getGoalRecommendations(Long userId) {
        List<Goal> activeGoals = goalRepository.findByUserIdAndStatus(userId, GoalStatus.IN_PROGRESS);
        List<GoalResponse> recommendations = new ArrayList<>();

        // Analyze current goals and generate recommendations
        for (Goal goal : activeGoals) {
            if (shouldRecommendAdjustment(goal)) {
                GoalResponse recommendation = mapGoalToResponse(goal);
                recommendations.add(recommendation);
            }
        }

        return recommendations;
    }

    @Override
    public WeeklyInsightsResponse getProgressAnalysis(Long userId, LocalDate startDate, LocalDate endDate) {
        return generateInsights(userId, startDate, endDate);
    }

    private WeeklyInsightsResponse generateInsights(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Goal> goals = goalRepository.findByUserIdAndTargetDateBetween(userId, startDate, endDate);
        
        WeeklyInsightsResponse insights = new WeeklyInsightsResponse();
        insights.setWeekStartDate(startDate);
        insights.setWeekEndDate(endDate);
        
        // Calculate overall progress
        insights.setTotalGoals(goals.size());
        insights.setCompletedGoals((int) goals.stream()
            .filter(g -> g.getStatus() == GoalStatus.COMPLETED)
            .count());
        
        if (!goals.isEmpty()) {
            insights.setOverallCompletionRate(
                (double) insights.getCompletedGoals() / insights.getTotalGoals() * 100
            );
        }
        
        // Calculate totals
        BigDecimal totalSaved = goals.stream()
            .map(Goal::getCurrentSaved)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalRequired = goals.stream()
            .map(Goal::getTargetAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        insights.setTotalSaved(totalSaved);
        insights.setTotalRequired(totalRequired);
        
        // Generate goal-specific insights
        List<GoalInsight> goalInsights = goals.stream()
            .map(this::createGoalInsight)
            .collect(Collectors.toList());
        insights.setGoalInsights(goalInsights);
        
        // Generate recommendations
        List<String> recommendations = generateRecommendations(goals);
        insights.setRecommendations(recommendations);
        
        // Calculate performance metrics
        calculatePerformanceMetrics(insights, goals);
        
        return insights;
    }

    private GoalInsight createGoalInsight(Goal goal) {
        GoalInsight insight = new GoalInsight();
        insight.setGoalId(goal.getId());
        insight.setGoalName(goal.getName());
        
        // Calculate weekly progress
        BigDecimal weeklyTarget = calculateWeeklyTarget(goal);
        BigDecimal weeklySaved = goal.getCurrentSaved().subtract(BigDecimal.ZERO); // Replace with actual weekly savings
        
        insight.setWeeklyProgress(
            weeklySaved.multiply(BigDecimal.valueOf(100))
                .divide(weeklyTarget, 2, RoundingMode.HALF_UP)
                .doubleValue()
        );
        
        insight.setOnTrack(insight.getWeeklyProgress() >= 100);
        insight.setWeeklySaved(weeklySaved);
        insight.setWeeklyTarget(weeklyTarget);
        
        // Generate suggestions
        insight.setSuggestions(generateSuggestions(goal));
        
        return insight;
    }

    private BigDecimal calculateWeeklyTarget(Goal goal) {
        long totalWeeks = goal.getTargetDate().until(LocalDate.now()).getDays() / 7;
        if (totalWeeks <= 0) totalWeeks = 1;
        
        return goal.getTargetAmount()
            .divide(BigDecimal.valueOf(totalWeeks), 2, RoundingMode.HALF_UP);
    }

    private List<String> generateSuggestions(Goal goal) {
        List<String> suggestions = new ArrayList<>();
        
        // Add suggestions based on goal progress
        if (goal.getCompletionPercentage() < 50) {
            suggestions.add("Consider increasing your weekly savings to reach your goal on time");
        }
        
        if (LocalDate.now().plusMonths(1).isAfter(goal.getTargetDate()) && 
            goal.getCompletionPercentage() < 90) {
            suggestions.add("Your goal deadline is approaching. Consider extending the target date or increasing savings");
        }
        
        return suggestions;
    }

    private List<String> generateRecommendations(List<Goal> goals) {
        List<String> recommendations = new ArrayList<>();
        
        // Add overall recommendations based on goals analysis
        if (goals.stream().allMatch(g -> g.getCompletionPercentage() > 80)) {
            recommendations.add("Great progress! Consider setting more ambitious goals");
        }
        
        if (goals.stream().anyMatch(g -> g.getStatus() == GoalStatus.EXPIRED)) {
            recommendations.add("Some goals have expired. Review and adjust your financial planning");
        }
        
        return recommendations;
    }

    private void calculatePerformanceMetrics(WeeklyInsightsResponse insights, List<Goal> goals) {
        // Calculate weekly progress rate
        double avgProgress = goals.stream()
            .mapToDouble(Goal::getCompletionPercentage)
            .average()
            .orElse(0.0);
        insights.setWeeklyProgressRate(avgProgress);
        
        // Determine if on track overall
        insights.setOnTrackOverall(avgProgress >= 80);
        
        // Identify improvement areas
        Map<String, String> improvementAreas = new HashMap<>();
        if (avgProgress < 50) {
            improvementAreas.put("savings_rate", "Increase regular savings to meet goals");
        }
        if (goals.stream().anyMatch(g -> g.getStatus() == GoalStatus.EXPIRED)) {
            improvementAreas.put("goal_planning", "Review goal deadlines and adjust accordingly");
        }
        insights.setImprovementAreas(improvementAreas);
    }

    private boolean shouldRecommendAdjustment(Goal goal) {
        // Recommend adjustment if:
        // 1. Goal is significantly behind schedule
        // 2. Target date is approaching but progress is low
        // 3. Saving rate is insufficient to meet the goal
        
        LocalDate now = LocalDate.now();
        long daysUntilTarget = now.until(goal.getTargetDate()).getDays();
        double progressRate = goal.getCompletionPercentage();
        
        return (daysUntilTarget > 0 && progressRate < 50) || // Significantly behind
               (daysUntilTarget <= 30 && progressRate < 80) || // Approaching deadline
               (calculateRequiredDailySaving(goal).compareTo(
                   goal.getTargetAmount().multiply(BigDecimal.valueOf(0.1))) > 0); // High daily requirement
    }

    private BigDecimal calculateRequiredDailySaving(Goal goal) {
        long remainingDays = LocalDate.now().until(goal.getTargetDate()).getDays();
        if (remainingDays <= 0) return BigDecimal.ZERO;
        
        BigDecimal remaining = goal.getTargetAmount().subtract(goal.getCurrentSaved());
        return remaining.divide(BigDecimal.valueOf(remainingDays), 2, RoundingMode.HALF_UP);
    }

    private void mapRequestToGoal(GoalRequest request, Goal goal) {
        goal.setName(request.getName());
        goal.setTargetAmount(request.getTargetAmount());
        goal.setTargetDate(request.getTargetDate());
        goal.setDescription(request.getDescription());
    }

    private GoalResponse mapGoalToResponse(Goal goal) {
        GoalResponse response = new GoalResponse();
        response.setId(goal.getId());
        response.setName(goal.getName());
        response.setTargetAmount(goal.getTargetAmount());
        response.setTargetDate(goal.getTargetDate());
        response.setCurrentSaved(goal.getCurrentSaved());
        response.setCompletionPercentage(goal.getCompletionPercentage());
        response.setStatus(goal.getStatus());
        response.setDescription(goal.getDescription());
        response.setCreatedAt(goal.getCreatedAt());
        response.setUpdatedAt(goal.getUpdatedAt());
        
        response.calculateAdditionalFields();
        return response;
    }
} 