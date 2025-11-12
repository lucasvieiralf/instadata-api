package br.com.grape.accessmanager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.grape.accessmanager.entity.Feature;
import br.com.grape.accessmanager.entity.Plan;
import br.com.grape.accessmanager.entity.PlanLimit;

@Repository
public interface PlanLimitRepository extends JpaRepository<PlanLimit, Integer> {

    List<PlanLimit> findByPlanId(Integer planId);

    Optional<PlanLimit> findByPlanIdAndFeatureId(Integer planId, Integer featureId);
    
    Optional<PlanLimit> findByPlanAndFeature(Plan plan, Feature feature);

}