package com.finsight.goal.model;

import com.finsight.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "goal_templates")
@Getter
@Setter
public class GoalTemplate extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalCategory category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Positive
    @Column(name = "suggested_duration_months")
    private Integer suggestedDurationMonths;

    @Positive
    @Column(name = "typical_amount")
    private BigDecimal typicalAmount;

    @Column(name = "is_premium", nullable = false)
    private boolean premium = false;

    @Column(columnDefinition = "TEXT")
    private String tips;

    @Column(name = "success_rate")
    private Double successRate;

    @Column(name = "difficulty_level")
    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficultyLevel;

    public enum DifficultyLevel {
        EASY, MODERATE, CHALLENGING
    }
} 