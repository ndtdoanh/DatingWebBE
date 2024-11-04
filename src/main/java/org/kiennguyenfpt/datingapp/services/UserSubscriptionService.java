package org.kiennguyenfpt.datingapp.services;

import org.kiennguyenfpt.datingapp.dtos.responses.*;

import java.util.List;

public interface UserSubscriptionService {

    List<AdminUserSubscriptionResponse> getAllUserSubscriptions();

    AdminUserWithSubscriptionDetails getUserSubscriptionById(Long id);

    List<SubscriptionStatsResponse> getPackageCountByMonth();

    List<SubscriptionStatsResponse> getPackageCountByQuarter();

    List<SubscriptionStatsResponse> getPackageCountByYear();

    List<RevenueStatsResponse> getMonthlyRevenue();

    List<RevenueStatsResponse> getQuarterlyRevenue();

    List<RevenueStatsResponse> getYearlyRevenue();

    SubscriptionPlanResponse getActiveSubscriptionByUserId(Long userId);

}
