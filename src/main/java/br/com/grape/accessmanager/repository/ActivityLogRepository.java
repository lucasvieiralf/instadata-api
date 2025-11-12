package br.com.grape.accessmanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.grape.accessmanager.entity.ActivityLog;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByCompanyIdOrderByCreatedAtDesc(Integer companyId);
    List<ActivityLog> findByUserIdOrderByCreatedAtDesc(Integer userId);
}