package com.example.csis3275.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "experience")
public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String shortDescription;
    private String country;
    private String city;
    private String location;
    private int maxParticipants;
    private double price;
    @OneToMany(mappedBy = "experience")
    private List<ExperienceInstance> instances;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
