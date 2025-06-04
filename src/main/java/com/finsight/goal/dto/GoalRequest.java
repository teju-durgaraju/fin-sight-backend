package com.finsight.goal.dto;

import com.finsight.goal.model.GoalCategory;
import com.finsight.goal.model.Goal.ReminderFrequency;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class GoalRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotNull(message = "Target amount is required")
    @Positive(message = "Target amount must be positive")
    private BigDecimal targetAmount;
    
    @NotNull(message = "Target date is required")
    @Future(message = "Target date must be in the future")
    private LocalDate targetDate;
    
    private String description;

    @NotNull(message = "Category is required")
    private GoalCategory category;

    private Long templateId;

    private List<MilestoneRequest> milestones;

    private ReminderFrequency reminderFrequency;

    private boolean autoAdjustEnabled;

    private Integer priorityLevel;

    @Data
    public static class MilestoneRequest {
        @NotBlank(message = "Milestone title is required")
        private String title;

        @NotNull(message = "Target amount is required")
        @Positive(message = "Target amount must be positive")
        private BigDecimal targetAmount;

        @NotNull(message = "Target date is required")
        @Future(message = "Target date must be in the future")
        private LocalDate targetDate;

        private String notes;
    }
} 