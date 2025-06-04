package com.finsight.budget.model;

import com.finsight.common.BaseEntity;
import com.finsight.expense.model.ExpenseCategory;
import com.finsight.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.YearMonth;

@Entity
@Table(name = "budgets",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "category", "month_year"})
    })
@Getter
@Setter
public class Budget extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseCategory category;

    @NotNull
    @Column(name = "month_year", nullable = false)
    private YearMonth monthYear;

    @NotNull
    @Positive
    @Column(name = "monthly_limit", nullable = false)
    private BigDecimal monthlyLimit;

    @NotNull
    @Column(name = "current_spent", nullable = false)
    private BigDecimal currentSpent = BigDecimal.ZERO;

    @Column(name = "alert_threshold")
    private Integer alertThreshold = 80; // Default 80%

    @Column(name = "is_threshold_reached")
    private boolean isThresholdReached = false;
} 