package org.kiennguyenfpt.datingapp.repositories;

import org.kiennguyenfpt.datingapp.entities.Profile;
import org.kiennguyenfpt.datingapp.entities.Swipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SwipeRepository extends JpaRepository<Swipe, Long> {
    Swipe findByUser_UserIdAndTargetUser_UserId(Long userId, Long targetUserId);

    @Query("SELECT p FROM Swipe l JOIN Profile p ON l.user.userId = p.user.userId WHERE l.isLike = true AND l.user.userId <> :currentUserId")
    List<Profile> findAllLikedProfilesExcludingCurrentUser(@Param("currentUserId") Long currentUserId);

}
