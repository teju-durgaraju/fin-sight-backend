package com.finsight.goal.dto;

import com.finsight.goal.model.GoalStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class GoalResponse {
    private Long id;
    private String name;
    private BigDecimal targetAmount;
    private LocalDate targetDate;
    private BigDecimal currentSaved;
    private Double completionPercentage;
    private GoalStatus status;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional calculated fields
    private BigDecimal remainingAmount;
    private Long daysRemaining;
    private BigDecimal requiredDailySaving;
    
    public void calculateAdditionalFields() {
        if (targetAmount != null && currentSaved != null) {
            this.remainingAmount = targetAmount.subtract(currentSaved);
        }
        
        if (targetDate != null) {
            this.daysRemaining = Long.valueOf(LocalDate.now().until(targetDate).getDays());
            
            if (remainingAmount != null && daysRemaining > 0) {
                this.requiredDailySaving = remainingAmount.divide(
                    BigDecimal.valueOf(daysRemaining),
                    2,
                    java.math.RoundingMode.HALF_UP
                );
            }
        }
    }
} 