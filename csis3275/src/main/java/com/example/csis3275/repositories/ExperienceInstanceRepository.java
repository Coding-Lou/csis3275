package com.example.csis3275.repositories;

import com.example.csis3275.entities.ExperienceInstance;
import com.example.csis3275.entities.Experience;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExperienceInstanceRepository extends JpaRepository<ExperienceInstance, Long> {
    
    List<ExperienceInstance> findByExperience(Experience experience);
    
    List<ExperienceInstance> findByExperienceId(Long experienceId);

    @Query("SELECT e from ExperienceInstance e where LOWER(e.experience.title) LIKE lower( concat('%' , :experienceKey ,'%')) ")
    List<ExperienceInstance> findByTitleContainingIgnoreCase(@Param("experienceKey") String experienceKey);

    @Modifying
    @Transactional
    @Query("update ExperienceInstance e set e.availableSlots = e.availableSlots + :changeQuantity where e.id = :id")
    void updateQuantity(@Param("id") long id, @Param("changeQuantity") int changeQuantity);
    
}
