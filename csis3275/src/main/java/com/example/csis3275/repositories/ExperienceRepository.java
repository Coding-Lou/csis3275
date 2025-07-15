package com.example.csis3275.repositories;

import com.example.csis3275.entities.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    List<Experience> findByTitleContainingIgnoreCase(String title);
}
