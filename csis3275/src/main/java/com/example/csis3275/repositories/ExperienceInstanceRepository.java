package com.example.csis3275.repositories;

import com.example.csis3275.entities.ExperienceInstance;
import com.example.csis3275.entities.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExperienceInstanceRepository extends JpaRepository<ExperienceInstance, Long> {
    
    List<ExperienceInstance> findByExperience(Experience experience);
    
    List<ExperienceInstance> findByExperienceId(Long experienceId);
    
}
