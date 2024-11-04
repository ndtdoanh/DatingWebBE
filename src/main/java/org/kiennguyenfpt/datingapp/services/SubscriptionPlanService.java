package org.kiennguyenfpt.datingapp.services;

import org.kiennguyenfpt.datingapp.entities.SubscriptionPlan;

import java.util.List;

public interface SubscriptionPlanService {

    List<SubscriptionPlan> getAllSubscriptionPlans();

    SubscriptionPlan updateSubscriptionPlan(Long planId, SubscriptionPlan updatedSubscriptionPlan);
}
