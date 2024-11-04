package org.kiennguyenfpt.datingapp.repositories;

import org.kiennguyenfpt.datingapp.entities.Like;
import org.kiennguyenfpt.datingapp.entities.Profile;
import org.kiennguyenfpt.datingapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    // Tìm kiếm một lượt like bởi user và profile (tức là xem profile của targetUser đã được user like chưa)
    Like findByUserAndProfile(User user, Profile profile);

}
