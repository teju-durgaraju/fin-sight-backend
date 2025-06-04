package com.finsight.goal.model;

import com.finsight.common.BaseEntity;
import com.finsight.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "goals")
@Getter
@Setter
public class Goal extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotNull
    @Positive
    @Column(name = "target_amount", nullable = false)
    private BigDecimal targetAmount;

    @NotNull
    @Future
    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @NotNull
    @Column(name = "current_saved", nullable = false)
    private BigDecimal currentSaved = BigDecimal.ZERO;

    @Column(name = "completion_percentage")
    private Double completionPercentage = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalStatus status = GoalStatus.IN_PROGRESS;

    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private GoalTemplate template;

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GoalMilestone> milestones = new ArrayList<>();

    @Column(name = "reminder_frequency")
    @Enumerated(EnumType.STRING)
    private ReminderFrequency reminderFrequency = ReminderFrequency.WEEKLY;

    @Column(name = "auto_adjust_enabled")
    private boolean autoAdjustEnabled = false;

    @Column(name = "priority_level")
    private Integer priorityLevel = 1;

    @PrePersist
    @PreUpdate
    private void calculateCompletionPercentage() {
        if (targetAmount != null && currentSaved != null && targetAmount.compareTo(BigDecimal.ZERO) > 0) {
            this.completionPercentage = currentSaved
                .multiply(BigDecimal.valueOf(100))
                .divide(targetAmount, 2, java.math.RoundingMode.HALF_UP)
                .doubleValue();
            
            if (this.completionPercentage >= 100) {
                this.status = GoalStatus.COMPLETED;
            } else if (LocalDate.now().isAfter(targetDate)) {
                this.status = GoalStatus.EXPIRED;
            } else {
                this.status = GoalStatus.IN_PROGRESS;
            }
        }
    }

    public enum ReminderFrequency {
        DAILY, WEEKLY, MONTHLY, NEVER
    }
} 