package com.finsight.chart.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "charts")
public class Chart {
    @Id
    private Long id;
    // This is a dummy entity just to satisfy JpaRepository requirements
    // All actual chart data is retrieved through native queries
} 