package org.kiennguyenfpt.datingapp.repositories;

import org.kiennguyenfpt.datingapp.dtos.responses.AdminMatchResponse;
import org.kiennguyenfpt.datingapp.entities.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByUser1_UserIdOrUser2_UserId(Long user1Id, Long user2Id);

    boolean existsByUser1_UserIdAndUser2_UserId(Long user1Id, Long user2Id);

    @Query(value = "SELECT \n" +
            "    m.match_id,\n" +
            "    u1.user_id AS user1_id,\n" +
            "    u2.user_id AS user2_id,\n" +
            "    u1.email AS user1_email,\n" +
            "    u2.email AS user2_email,\n" +
            "    p1.name AS user1_name,\n" +
            "    p2.name AS user2_name,\n" +
            "    l1.address AS user1_address,\n" +
            "    l2.address AS user2_address,\n" +
            "    m.created_at AS match_created_at\n" +
            "FROM\n" +
            "    matches m\n" +
            "        JOIN\n" +
            "    users u1 ON m.user1_id = u1.user_id\n" +
            "        JOIN\n" +
            "    users u2 ON m.user2_id = u2.user_id\n" +
            "        LEFT JOIN\n" +
            "    profiles p1 ON u1.user_id = p1.user_id\n" +
            "        LEFT JOIN\n" +
            "    profiles p2 ON u2.user_id = p2.user_id\n" +
            "        LEFT JOIN\n" +
            "    user_location l1 ON u1.user_id = l1.user_id\n" +
            "        LEFT JOIN\n" +
            "    user_location l2 ON u2.user_id = l2.user_id\n",
            nativeQuery = true)
    List<Object[]> getAllMatchesWithUserDetailsRaw();

    default List<AdminMatchResponse> getAllMatchesWithUserDetails() {
        List<Object[]> results = getAllMatchesWithUserDetailsRaw();
        return results.stream()
                .map(result -> new AdminMatchResponse(
                        (Long) result[0],  // match_id
                        (Long) result[1], // user1_id
                        (Long) result[2], // user2_id
                        (String) result[3], // user1_email
                        (String) result[4], // user2_email
                        (String) result[5], // user1_name
                        (String) result[6], // user2_name
                        (String) result[7], // user2_location
                        (String) result[8], // user2_location
                        ((Timestamp) result[9]).toLocalDateTime() // created_at
                ))
                .collect(Collectors.toList());
    }

}
