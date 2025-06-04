package com.finsight.goal.model;

import com.finsight.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "goal_milestones")
@Getter
@Setter
public class GoalMilestone extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    private Goal goal;

    @NotNull
    @Column(nullable = false)
    private String title;

    @NotNull
    @Positive
    @Column(name = "target_amount", nullable = false)
    private BigDecimal targetAmount;

    @NotNull
    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Column(name = "completion_percentage")
    private Double completionPercentage = 0.0;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @PrePersist
    @PreUpdate
    private void calculateCompletion() {
        if (goal != null && goal.getCurrentSaved() != null && targetAmount.compareTo(BigDecimal.ZERO) > 0) {
            this.completionPercentage = goal.getCurrentSaved()
                .multiply(BigDecimal.valueOf(100))
                .divide(targetAmount, 2, java.math.RoundingMode.HALF_UP)
                .doubleValue();
            
            this.completed = this.completionPercentage >= 100;
        }
    }
} 