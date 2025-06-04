package com.finsight.goal.model;

public enum GoalCategory {
    EMERGENCY_FUND("Emergency Fund"),
    RETIREMENT("Retirement Savings"),
    HOME_PURCHASE("Home Purchase"),
    EDUCATION("Education"),
    DEBT_PAYMENT("Debt Payment"),
    VACATION("Vacation"),
    CAR_PURCHASE("Car Purchase"),
    WEDDING("Wedding"),
    BUSINESS("Business"),
    INVESTMENT("Investment");

    private final String displayName;

    GoalCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 