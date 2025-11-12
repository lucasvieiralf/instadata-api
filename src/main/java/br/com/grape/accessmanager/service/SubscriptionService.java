package br.com.grape.accessmanager.service;

import java.util.List;
import java.util.Optional;

import br.com.grape.accessmanager.dto.subscription.SubscriptionDTO.*;
import br.com.grape.accessmanager.entity.User;

public interface SubscriptionService {

    List<PublicPlanResponse> listAvailablePlans();

    Optional<CurrentSubscriptionResponse> getMySubscription(User adminUser);

    CurrentSubscriptionResponse createSubscription(SubscribeRequest request, User adminUser);

    CurrentSubscriptionResponse cancelSubscription(User adminUser);
}