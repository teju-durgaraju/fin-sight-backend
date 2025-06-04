package com.finsight.expense.repository;

import com.finsight.expense.model.Expense;
import com.finsight.expense.model.ExpenseCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    Page<Expense> findByUserId(Long userId, Pageable pageable);
    
    List<Expense> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    
    List<Expense> findByUserIdAndCategory(Long userId, ExpenseCategory category);
    
    List<Expense> findByUserIdAndDateBetweenAndCategory(
        Long userId, 
        LocalDate startDate, 
        LocalDate endDate, 
        ExpenseCategory category
    );
} 