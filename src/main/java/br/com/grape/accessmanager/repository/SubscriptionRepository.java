package br.com.grape.accessmanager.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.grape.accessmanager.entity.Subscription;
import br.com.grape.accessmanager.enums.SubscriptionStatus;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {

    List<Subscription> findByStatusAndNextBillingAtLessThanEqual(
        SubscriptionStatus status,
        LocalDate today
    );

    Optional<Subscription> findByCompanyIdAndStatusIn(Integer companyId, java.util.Collection<SubscriptionStatus> statuses);

    Optional<Subscription> findByCompanyId(Integer companyId);
}