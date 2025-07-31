package com.example.csis3275.repositories;

import com.example.csis3275.entities.Experience;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    @Query("SELECT e FROM Experience e WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :title, '%')) and e.active = true")
    List<Experience> findByTitleContainingIgnoreCase(@Param("title") String title);

    List<Experience> findByUserId(Long userId);

    @Query("SELECT e FROM Experience e LEFT JOIN FETCH e.instances WHERE e.user.id = :userId and e.active = true")
    List<Experience> findByUserIdWithInstances(@Param("userId") Long userId);
}
