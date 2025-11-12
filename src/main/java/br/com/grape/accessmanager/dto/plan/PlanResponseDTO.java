package br.com.grape.accessmanager.dto.plan;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PlanResponseDTO {
    private Integer id;
    private String planName;
    private BigDecimal monthlyPrice;
}