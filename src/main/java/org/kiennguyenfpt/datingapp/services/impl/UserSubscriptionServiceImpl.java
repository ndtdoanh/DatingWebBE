package org.kiennguyenfpt.datingapp.services.impl;

import org.kiennguyenfpt.datingapp.dtos.responses.*;
import org.kiennguyenfpt.datingapp.entities.UserSubscription;
import org.kiennguyenfpt.datingapp.enums.SubscriptionStatus;
import org.kiennguyenfpt.datingapp.repositories.UserSubscriptionRepository;
import org.kiennguyenfpt.datingapp.services.UserSubscriptionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

    private final UserSubscriptionRepository userSubscriptionRepository;

    public UserSubscriptionServiceImpl(UserSubscriptionRepository userSubscriptionRepository) {
        this.userSubscriptionRepository = userSubscriptionRepository;
    }

    // Lấy danh sách tất cả các gói đăng ký của người dùng
    public List<AdminUserSubscriptionResponse> getAllUserSubscriptions() {
        List<AdminUserSubscriptionResponse> subscriptions = userSubscriptionRepository.getAllUserSubscriptions();
        return subscriptions;
    }

    // Lấy gói đăng ký cụ thể theo ID
    public AdminUserWithSubscriptionDetails getUserSubscriptionById(Long userSubscriptionId) {
        AdminUserWithSubscriptionDetails user = userSubscriptionRepository.getUserSubscriptionById(userSubscriptionId);
        return user;
    }

    public List<SubscriptionStatsResponse> getPackageCountByMonth() {
        return mapStats(userSubscriptionRepository.countPackagesByMonth());
    }

    public List<SubscriptionStatsResponse> getPackageCountByQuarter() {
        return mapStats(userSubscriptionRepository.countPackagesByQuarter());
    }

    public List<SubscriptionStatsResponse> getPackageCountByYear() {
        return mapStatsWithYears(userSubscriptionRepository.countPackagesByYear());
    }

    private List<SubscriptionStatsResponse> mapStatsWithYears(List<Object[]> stats) {
        List<SubscriptionStatsResponse> response = new ArrayList<>();

        for (Object[] stat : stats) {
            SubscriptionStatsResponse entry = new SubscriptionStatsResponse();

            // Handle year as Integer
            entry.setYear((Integer) stat[0]);
            entry.setPeriod(null);  // Set to null if period is not available
            entry.setCount((Long) stat[1]);

            response.add(entry);
        }

        return response;
    }

    // Map Object[] to SubscriptionStatsResponse
    private List<SubscriptionStatsResponse> mapStats(List<Object[]> stats) {
        List<SubscriptionStatsResponse> response = new ArrayList<>();

        for (Object[] stat : stats) {
            SubscriptionStatsResponse entry = new SubscriptionStatsResponse();

            // Handle year as Integer
            entry.setYear((Integer) stat[0]);

            // Handle period as Integer (month)
            if (stat[1] instanceof Integer) {
                entry.setPeriod((Integer) stat[1]);
            } else if (stat[1] instanceof Long) {
                entry.setPeriod(((Long) stat[1]).intValue());
            }

            if (stat[2] instanceof Integer) {
                entry.setCount(((Integer) stat[2]).longValue());
            } else if (stat[2] instanceof Long) {
                entry.setCount((Long) stat[2]);
            }

            response.add(entry);
        }

        return response;
    }

    public List<RevenueStatsResponse> getMonthlyRevenue() {
        return mapRevenues(userSubscriptionRepository.findMonthlyRevenue());
    }

    public List<RevenueStatsResponse> getQuarterlyRevenue() {
        return mapRevenues(userSubscriptionRepository.findQuarterlyRevenue());
    }

    public List<RevenueStatsResponse> getYearlyRevenue() {
        return mapRevenuesWithYears(userSubscriptionRepository.findYearlyRevenue());
    }

    private List<RevenueStatsResponse> mapRevenuesWithYears(List<Object[]> revenues) {
        List<RevenueStatsResponse> response = new ArrayList<>();

        for (Object[] revenue : revenues) {
            RevenueStatsResponse entry = new RevenueStatsResponse();

            // Handle year as Integer
            entry.setYear((Integer) revenue[0]);
            entry.setPeriod(null);  // Set to null if period is not available
            entry.setTotalRevenue((Double) revenue[1]);

            response.add(entry);
        }

        return response;
    }

    // Map Object[] to RevenueStatsResponse
    private List<RevenueStatsResponse> mapRevenues(List<Object[]> stats) {
        List<RevenueStatsResponse> response = new ArrayList<>();

        for (Object[] stat : stats) {
            RevenueStatsResponse entry = new RevenueStatsResponse();

            // Handle year as Integer
            entry.setYear((Integer) stat[0]);

            // Handle period as Integer (month)
            if (stat[1] instanceof Integer) {
                entry.setPeriod((Integer) stat[1]);
            } else if (stat[1] instanceof Long) {
                entry.setPeriod(((Long) stat[1]).intValue());
            }

            entry.setTotalRevenue((Double) stat[2]);

            response.add(entry);
        }

        return response;
    }

    @Override
    public SubscriptionPlanResponse getActiveSubscriptionByUserId(Long userId) {
        UserSubscription userSubscription = userSubscriptionRepository.findByUser_UserIdAndStatus(userId, SubscriptionStatus.ACTIVE);
        if (userSubscription == null) {
            return null;
        }

        // Map UserSubscription to SubscriptionPlanResponse
        return new SubscriptionPlanResponse(
                userSubscription.getSubscriptionPlan().getPlanId(),
                userSubscription.getSubscriptionPlan().getName(),
                userSubscription.getSubscriptionPlan().getDescription(),
                userSubscription.getSubscriptionPlan().getPrice()
        );
    }
}
