package com.finsight.goal.repository;

import com.finsight.goal.model.Goal;
import com.finsight.goal.model.GoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUserId(Long userId);
    
    List<Goal> findByUserIdAndStatus(Long userId, GoalStatus status);
    
    List<Goal> findByUserIdAndTargetDateBefore(Long userId, LocalDate date);
    
    List<Goal> findByUserIdAndTargetDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
} 