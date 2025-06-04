package com.finsight.income.model;

import com.finsight.common.BaseEntity;
import com.finsight.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "incomes")
@Getter
@Setter
public class Income extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Column(nullable = false)
    private String source;

    @NotNull
    @Positive
    @Column(nullable = false)
    private BigDecimal amount;

    @NotNull
    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncomeType type = IncomeType.SALARY;

    @Column(name = "is_recurring")
    private boolean recurring = false;

    private String description;

    public enum IncomeType {
        SALARY,
        BONUS,
        INVESTMENT,
        RENTAL,
        FREELANCE,
        OTHER
    }
} 