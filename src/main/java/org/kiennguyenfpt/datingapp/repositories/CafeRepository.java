package org.kiennguyenfpt.datingapp.repositories;

import jakarta.transaction.Transactional;
import org.kiennguyenfpt.datingapp.entities.Cafe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface CafeRepository extends JpaRepository<Cafe, Long> {
    List<Cafe> findByNameContainingIgnoreCase(String name);

    Optional<Cafe> findByName(String name);

    Optional<Cafe> findByAddress(String address);

    @Modifying
    @Query("UPDATE Cafe c SET c.status = CASE WHEN c.status = 'ACTIVE' THEN 'INACTIVE' ELSE 'ACTIVE' END WHERE c.cafeId = :id")
    int lockOrUnLockCafe(@Param("id") Long id);
}
