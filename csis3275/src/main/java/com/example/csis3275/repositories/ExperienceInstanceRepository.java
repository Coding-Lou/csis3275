package com.example.csis3275.repositories;

import com.example.csis3275.entities.ExperienceInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExperienceInstanceRepository extends JpaRepository<ExperienceInstance, Long> {
    List<ExperienceInstance> findExperienceInstanceById(Long id);
}
