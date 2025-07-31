package com.example.csis3275.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "experience_instance")
public class ExperienceInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "experience_id")
    private Experience experience;
    private String startDateTime;
    private String endDateTime;
    private int availableSlots;
    private double price;
    private boolean active = true;
}
