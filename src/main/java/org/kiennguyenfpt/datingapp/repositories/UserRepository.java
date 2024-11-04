package org.kiennguyenfpt.datingapp.repositories;

import jakarta.transaction.Transactional;
import org.kiennguyenfpt.datingapp.dtos.responses.AdminUserResponse;
import org.kiennguyenfpt.datingapp.dtos.responses.NearlyUserResponse;
import org.kiennguyenfpt.datingapp.entities.User;
import org.kiennguyenfpt.datingapp.enums.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    // Tìm kiếm người dùng theo email hoặc name chứa từ khóa
    @Query("SELECT u FROM User u " +
            "JOIN u.userRoles ur " +
            "JOIN ur.role r " +
            "WHERE r.roleName = 'User' " +
            "AND (:keyword IS NULL OR :keyword = '' OR u.email LIKE %:keyword% OR u.phone LIKE %:keyword%)")
    List<User> searchUsersByKeyword(@Param("keyword") String keyword);

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
            "    GROUP_CONCAT(ph.url) AS photoUrls\n" +
            "FROM \n" +
            "    users u\n" +
            "JOIN \n" +
            "    user_roles ur ON u.user_id = ur.user_id\n" +
            "JOIN \n" +
            "    roles r ON ur.role_id = r.role_id\n" +
            "LEFT JOIN \n" +
            "    profiles p ON u.user_id = p.user_id\n" +
            "LEFT JOIN \n" +
            "    user_location l ON u.user_id = l.user_id\n" +
            "LEFT JOIN \n" +
            "    user_subscriptions us ON u.user_id = us.user_id\n" +
            "LEFT JOIN \n" +
            "    subscription_plans sp ON us.plan_id = sp.plan_id\n " +
            "LEFT JOIN\n" +
            "    photos ph ON ph.profile_id = p.profile_id\n" +
            "WHERE \n" +
            "    r.role_name = 'User' AND us.status = 'ACTIVE' \n" +
            "GROUP BY u.user_id , p.name , u.email , l.address , u.status , r.role_name , p.bio , p.gender , p.phone , sp.name",
            nativeQuery = true)
    List<Object[]> searchAdminUsersRaw();

    default List<AdminUserResponse> searchAdminUsers() {
        // Execute the raw query method
        List<Object[]> results = searchAdminUsersRaw();

        // Convert raw results to UserSearchResponse DTOs
        return results.stream()
                .map(result -> new AdminUserResponse(
                        (Long) result[0],      // userId
                        result[1] != null ? (String) result[1] : "-",  // name, default "-" if null
                        (String) result[2],    // email
                        result[3] != null ? (String) result[3] : "-",  // address, default "-" if null
                        result[4] != null ? (String) result[4] : "-",  // status, default "-" if null
                        result[5] != null ? (String) result[5] : "-",  // role name, default "-" if null
                        result[6] != null ? (String) result[6] : "-",   // bio, default "-" if null
                        result[7] != null ? (String) result[7] : "-",   // gender, default "-" if null
                        result[8] != null ? (String) result[8] : "-",   // phone, default "-" if null
                        result[9] != null ? (String) result[9] : "-",   // package name, default "-" if null
                        result[10] != null ? List.of(((String) result[10]).split(",")) : Collections.emptyList() // photoUrls (Profile)
                ))
                .collect(Collectors.toList());
    }

    @Modifying
    @Query("UPDATE User u SET u.status = CASE WHEN u.status = 'ACTIVE' THEN 'INACTIVE' ELSE 'ACTIVE' END WHERE u.userId = :id")
    int lockOrUnLockUser(@Param("id") Long id);

    @Query(value = "SELECT u.user_id as userId, u.email, u.phone, " +
            "ul.latitude, ul.longitude, ul.address, " +
            "p.name, p.age, p.gender, p.bio, p.avatar, " +
            "GROUP_CONCAT(ph.url) as photoUrls " +  // Sử dụng GROUP_CONCAT để gộp các URL lại thành một chuỗi
            "FROM users u " +
            "JOIN user_location ul ON ul.user_id = u.user_id " +
            "LEFT JOIN profiles p ON p.user_id = u.user_id " +
            "LEFT JOIN photos ph ON ph.profile_id = p.profile_id " +  // Liên kết với bảng photos
            "WHERE (6371000 * acos(cos(radians(:latitude)) * cos(radians(ul.latitude)) * " +
            "cos(radians(ul.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(ul.latitude)))) " +
            "<= :rangeInMeters " +
            "GROUP BY u.user_id, ul.latitude, ul.longitude, ul.address, p.name, p.age, p.gender, p.bio, p.avatar",
            nativeQuery = true)
    List<Object[]> findNearbyUsersRaw(@Param("latitude") double latitude,
                                      @Param("longitude") double longitude,
                                      @Param("rangeInMeters") double rangeInMeters);

    default List<NearlyUserResponse> findNearbyUsers(@Param("latitude") double latitude,
                                                     @Param("longitude") double longitude,
                                                     @Param("rangeInMeters") double rangeInMeters) {
        List<Object[]> results = findNearbyUsersRaw(latitude, longitude, rangeInMeters);
        return results.stream()
                .map(result -> new NearlyUserResponse(
                        (Long) result[0],    // userId
                        (String) result[1],  // email
                        (String) result[2],  // phone
                        (Double) result[3],  // latitude
                        (Double) result[4],  // longitude
                        (String) result[5],  // address
                        result[6] != null ? (String) result[6] : "-",  // Default name if null
                        (Integer) result[7], // age (Profile)
                        result[8] != null ? Gender.valueOf((String) result[8]) : Gender.OTHER, // gender (Profile)
                        result[9] != null ? (String) result[9] : "-",  // bio (Profile)
                        result[10] != null ? (String) result[10] : "-", // avatar (Profile)
                        result[11] != null ? List.of(((String) result[11]).split(",")) : Collections.emptyList() // photoUrls (Profile)
                ))
                .collect(Collectors.toList());
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
            "    sp.name AS subscription_plan_name\n," +
            "    GROUP_CONCAT(ph.url) AS photoUrls\n" +
            "FROM \n" +
            "    users u\n" +
            "JOIN \n" +
            "    user_roles ur ON u.user_id = ur.user_id\n" +
            "JOIN \n" +
            "    roles r ON ur.role_id = r.role_id\n" +
            "LEFT JOIN \n" +
            "    profiles p ON u.user_id = p.user_id\n" +
            "LEFT JOIN \n" +
            "    user_location l ON u.user_id = l.user_id\n" +
            "LEFT JOIN \n" +
            "    user_subscriptions us ON u.user_id = us.user_id\n" +
            "LEFT JOIN \n" +
            "    subscription_plans sp ON us.plan_id = sp.plan_id\n" +
            "LEFT JOIN\n" +
            "    photos ph ON ph.profile_id = p.profile_id\n" +
            "WHERE \n" +
            "    r.role_name = 'User' AND us.status = 'ACTIVE' \n" +
            "AND u.user_id = :id\n" +
            "GROUP BY u.user_id , p.name , u.email , l.address , u.status , r.role_name , p.bio , p.gender , p.phone , sp.name",
            nativeQuery = true)
    List<Object> getUserByIdRaw(@Param("id") Long id);

    default AdminUserResponse getUserById(@Param("id") Long id) {
        List<Object> result = getUserByIdRaw(id);

        // Check if the result is null or empty before proceeding
        if (result == null || result.isEmpty()) {
            // Handle the case when no user is found
            return null; // or throw an exception, or return a default AdminUserResponse
        }

        // Since you're querying for a single user, we can safely cast the first element of the list to an Object array.
        Object[] userData = (Object[]) result.get(0);

        // Convert raw results to AdminUserResponse DTOs
        return new AdminUserResponse(
                userData[0] != null ? ((Number) userData[0]).longValue() : null, // userId
                userData[1] != null ? (String) userData[1] : "-",                // profile_name
                userData[2] != null ? (String) userData[2] : "-",                // email
                userData[3] != null ? (String) userData[3] : "-",                // address
                userData[4] != null ? (String) userData[4] : "-",                // status
                userData[5] != null ? (String) userData[5] : "-",                // role_name
                userData[6] != null ? (String) userData[6] : "-",                // bio
                userData[7] != null ? (String) userData[7] : "-",                // gender
                userData[8] != null ? (String) userData[8] : "-",                // phone
                userData[9] != null ? (String) userData[9] : "-",                 // subscription_plan_name
                userData[10] != null ? List.of(((String) userData[10]).split(",")) : Collections.emptyList() // photoUrls (Profile)
        );
    }
}
