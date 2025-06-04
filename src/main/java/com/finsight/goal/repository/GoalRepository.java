package com.finsight.goal.repository;

import com.finsight.goal.model.Goal;
import com.finsight.goal.model.GoalCategory;
import com.finsight.goal.model.GoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUserId(Long userId);
    
    List<Goal> findByUserIdAndStatus(Long userId, GoalStatus status);
    
    List<Goal> findByUserIdAndTargetDateBefore(Long userId, LocalDate date);
    
    List<Goal> findByUserIdAndTargetDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT g FROM Goal g WHERE g.user.id = :userId " +
           "AND g.targetDate >= CURRENT_DATE " +
           "AND g.currentSaved < g.targetAmount " +
           "ORDER BY g.targetDate ASC")
    List<Goal> findActiveGoals(@Param("userId") Long userId);

    @Query("SELECT g FROM Goal g WHERE g.user.id = :userId " +
           "AND g.status = 'IN_PROGRESS' " +
           "AND g.targetDate < CURRENT_DATE")
    List<Goal> findOverdueGoals(@Param("userId") Long userId);

    @Query("SELECT g.category, COUNT(g), " +
           "SUM(CASE WHEN g.status = 'COMPLETED' THEN 1 ELSE 0 END) as completed " +
           "FROM Goal g WHERE g.user.id = :userId " +
           "GROUP BY g.category")
    List<Object[]> getGoalStatsByCategory(@Param("userId") Long userId);

    @Query("SELECT g FROM Goal g WHERE g.user.id = :userId " +
           "AND g.status = 'IN_PROGRESS' " +
           "AND g.currentSaved >= 0.9 * g.targetAmount")
    List<Goal> findNearlyCompletedGoals(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Goal g SET g.currentSaved = g.currentSaved + :amount, " +
           "g.completionPercentage = (g.currentSaved + :amount) * 100.0 / g.targetAmount, " +
           "g.status = CASE WHEN (g.currentSaved + :amount) >= g.targetAmount THEN 'COMPLETED' " +
           "ELSE g.status END " +
           "WHERE g.id = :goalId AND g.user.id = :userId")
    void updateProgress(
        @Param("userId") Long userId,
        @Param("goalId") Long goalId,
        @Param("amount") BigDecimal amount
    );

    @Query("SELECT g FROM Goal g WHERE g.user.id = :userId " +
           "AND g.category = :category " +
           "AND g.status = 'COMPLETED' " +
           "ORDER BY g.targetAmount DESC")
    List<Goal> findCompletedGoalsByCategory(
        @Param("userId") Long userId,
        @Param("category") GoalCategory category
    );

    @Query("SELECT AVG(g.targetAmount) FROM Goal g " +
           "WHERE g.user.id = :userId AND g.category = :category " +
           "AND g.status = 'COMPLETED'")
    BigDecimal getAverageCompletedGoalAmount(
        @Param("userId") Long userId,
        @Param("category") GoalCategory category
    );

    @Query("SELECT g FROM Goal g WHERE g.user.id = :userId " +
           "AND g.status = 'IN_PROGRESS' " +
           "AND g.targetDate BETWEEN :startDate AND :endDate " +
           "ORDER BY (g.currentSaved / g.targetAmount) DESC")
    List<Goal> findGoalsByProgressInPeriod(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
} 