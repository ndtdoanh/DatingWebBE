package org.kiennguyenfpt.datingapp.repositories;

import jakarta.transaction.Transactional;
import org.kiennguyenfpt.datingapp.dtos.responses.AdminUserSubscriptionResponse;
import org.kiennguyenfpt.datingapp.dtos.responses.AdminUserWithSubscriptionDetails;
import org.kiennguyenfpt.datingapp.entities.UserSubscription;
import org.kiennguyenfpt.datingapp.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    @Query("SELECT us FROM UserSubscription us WHERE us.user.userId = :userId AND us.status = 'ACTIVE'")
    Optional<UserSubscription> findActiveSubscriptionByUserId(@Param("userId") Long userId);

    UserSubscription findByUser_UserIdAndStatus(Long userId, SubscriptionStatus status);
    // Native query để lấy danh sách gói đăng ký theo userId
    @Query(value = "SELECT * FROM user_subscriptions WHERE user_id = :userId", nativeQuery = true)
    List<UserSubscription> findByUserId(@Param("userId") Long userId);

    // Native query để tìm các gói đăng ký đang hoạt động
    @Query(value = "SELECT * FROM user_subscriptions WHERE active = true", nativeQuery = true)
    List<UserSubscription> findByActiveTrue();

    // Native query để tìm các gói đăng ký theo userId và trạng thái hoạt động
    @Query(value = "SELECT * FROM user_subscriptions WHERE user_id = :userId AND active = true", nativeQuery = true)
    List<UserSubscription> findByUserIdAndActiveTrue(@Param("userId") Long userId);

    @Query(value = "SELECT us.user_subscription_id, u.user_id, u.email, sp.name AS packageName, " +
            "us.start_date, us.end_date, us.status " +
            "FROM user_subscriptions us " +
            "JOIN subscription_plans sp ON us.plan_id = sp.plan_id " +
            "JOIN users u ON us.user_id = u.user_id",
            nativeQuery = true)
    List<Object[]> getAllUserSubscriptionsRaw();

    default List<AdminUserSubscriptionResponse> getAllUserSubscriptions() {
        List<Object[]> rawData = getAllUserSubscriptionsRaw();
        List<AdminUserSubscriptionResponse> subscriptions = new ArrayList<>();
        for (Object[] row : rawData) {
            Long userSubscriptionId = ((Number) row[0]).longValue();
            Long userId = ((Number) row[1]).longValue();
            String email = (String) row[2];
            String packageName = (String) row[3];
            Timestamp startDate = ((Timestamp) row[4]);
            Timestamp endDate = ((Timestamp) row[5]);
            String status = (String) row[6];

            AdminUserSubscriptionResponse response = new AdminUserSubscriptionResponse(
                    userSubscriptionId, userId, email, packageName, startDate, endDate, status
            );
            subscriptions.add(response);
        }
        return subscriptions;
    }

    @Query(value = "SELECT \n" +
            "    u.user_id, \n" +
            "    p.name AS profile_name, \n" +
            "    u.email, \n" +
            "    l.address, \n" +
            "    u.status, \n" +
            "    r.role_name, \n" +
            "    p.bio, \n" +
            "    p.gender, \n" +
            "    p.phone, \n" +
            "    sp.name AS subscription_plan_name, \n" +
            "    us.start_date AS subscription_start_date, \n" +
            "    us.end_date AS subscription_end_date, \n" +
            "    us.status AS subscription_status, \n" +
            "    GROUP_CONCAT(ph.url) AS photoUrls \n" +
            "FROM \n" +
            "    users u \n" +
            "JOIN \n" +
            "    user_roles ur ON u.user_id = ur.user_id \n" +
            "JOIN \n" +
            "    roles r ON ur.role_id = r.role_id \n" +
            "LEFT JOIN \n" +
            "    profiles p ON u.user_id = p.user_id \n" +
            "LEFT JOIN \n" +
            "    user_location l ON u.user_id = l.user_id \n" +
            "LEFT JOIN \n" +
            "    user_subscriptions us ON u.user_id = us.user_id \n" +
            "LEFT JOIN \n" +
            "    subscription_plans sp ON us.plan_id = sp.plan_id \n" +
            "LEFT JOIN \n" +
            "    photos ph ON ph.profile_id = p.profile_id \n" +
            "WHERE \n" +
            "    r.role_name = 'User' \n" +
            "AND u.user_id = :id \n" +
            "GROUP BY \n" +
            "    u.user_id, p.name, u.email, l.address, u.status, r.role_name, p.bio, p.gender, p.phone, sp.name, us.start_date, us.end_date, us.status",
            nativeQuery = true)
    List<Object> getUserWithSubscriptionDetailsByIdRaw(@Param("id") Long id);

    default AdminUserWithSubscriptionDetails getUserSubscriptionById(Long id) {
        List<Object> rawData = getUserWithSubscriptionDetailsByIdRaw(id);
        Object[] row = (Object[]) rawData.get(0);

        Long userId = ((Number) row[0]).longValue();
        String profileName = (String) row[1];
        String email = (String) row[2];
        String address = (String) row[3];
        String status = (String) row[4];
        String roleName = (String) row[5];
        String bio = (String) row[6];
        String gender = (String) row[7];
        String phone = (String) row[8];
        String subscriptionPlanName = (String) row[9];
        Timestamp subscriptionStartDate = row[10] != null ? ((Timestamp) row[10]) : null;
        Timestamp subscriptionEndDate = row[11] != null ? ((Timestamp) row[11]) : null;
        String subscriptionStatus = (String) row[12];
        List<String> photoUrls = row[13] != null ? List.of(((String) row[13]).split(",")) : Collections.emptyList(); // photoUrls (Profile)

        return new AdminUserWithSubscriptionDetails(
                userId,
                profileName,
                email,
                address,
                status,
                roleName,
                bio,
                gender,
                phone,
                subscriptionPlanName,
                subscriptionStartDate,
                subscriptionEndDate,
                subscriptionStatus,
                photoUrls
        );

    }

    @Modifying
    @Transactional
    @Query(value = "UPDATE user_subscriptions SET status = :status WHERE user_id = :userId AND status = 'ACTIVE'", nativeQuery = true)
    void updateStatusToExpiredByUserId(Long userId, String status);

    @Query(value = "SELECT YEAR(us.start_date) AS year, MONTH(us.start_date) AS month, COUNT(us.user_subscription_id) AS count " +
            "FROM user_subscriptions us " +
            "WHERE us.plan_id = 2 OR us.plan_id = 3 " +
            "GROUP BY YEAR(us.start_date), MONTH(us.start_date)",
            nativeQuery = true)
    List<Object[]> countPackagesByMonth();

    @Query(value = "SELECT YEAR(us.start_date) AS year, QUARTER(us.start_date) AS quarter, COUNT(us.user_subscription_id) AS count " +
            "FROM user_subscriptions us " +
            "WHERE us.plan_id = 2 OR us.plan_id = 3 " +
            "GROUP BY YEAR(us.start_date), QUARTER(us.start_date)",
            nativeQuery = true)
    List<Object[]> countPackagesByQuarter();

    @Query(value = "SELECT YEAR(us.start_date) AS year, COUNT(us.user_subscription_id) AS count " +
            "FROM user_subscriptions us " +
            "WHERE us.plan_id = 2 OR us.plan_id = 3 " +
            "GROUP BY YEAR(us.start_date)",
            nativeQuery = true)
    List<Object[]> countPackagesByYear();

    @Query(value = "SELECT YEAR(us.start_date) AS year, MONTH(us.start_date) AS month, SUM(sp.price) AS total_revenue "
            + "FROM user_subscriptions us JOIN subscription_plans sp ON us.plan_id = sp.plan_id "
            + "WHERE us.start_date IS NOT NULL AND us.plan_id = 2 OR us.plan_id = 3 "
            + "GROUP BY YEAR(us.start_date), MONTH(us.start_date) "
            + "ORDER BY YEAR(us.start_date), MONTH(us.start_date)",
            nativeQuery = true)
    List<Object[]> findMonthlyRevenue();

    @Query(value = "SELECT YEAR(us.start_date) AS year, QUARTER(us.start_date) AS quarter, SUM(sp.price) AS total_revenue "
            + "FROM user_subscriptions us JOIN subscription_plans sp ON us.plan_id = sp.plan_id "
            + "WHERE us.start_date IS NOT NULL AND us.plan_id = 2 OR us.plan_id = 3 "
            + "GROUP BY YEAR(us.start_date), QUARTER(us.start_date) "
            + "ORDER BY YEAR(us.start_date), QUARTER(us.start_date)",
            nativeQuery = true)
    List<Object[]> findQuarterlyRevenue();

    @Query(value = "SELECT YEAR(us.start_date) AS year, SUM(sp.price) AS total_revenue "
            + "FROM user_subscriptions us JOIN subscription_plans sp ON us.plan_id = sp.plan_id "
            + "WHERE us.start_date IS NOT NULL AND us.plan_id = 2 OR us.plan_id = 3 "
            + "GROUP BY YEAR(us.start_date) "
            + "ORDER BY YEAR(us.start_date)",
            nativeQuery = true)
    List<Object[]> findYearlyRevenue();

}
