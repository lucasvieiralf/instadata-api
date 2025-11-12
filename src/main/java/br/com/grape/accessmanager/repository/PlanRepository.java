package br.com.grape.accessmanager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.grape.accessmanager.entity.Plan;
import br.com.grape.accessmanager.enums.PlanStatus;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Integer> {

    Optional<Plan> findByPlanName(String planName);
    
    List<Plan> findByStatus(PlanStatus status);

}