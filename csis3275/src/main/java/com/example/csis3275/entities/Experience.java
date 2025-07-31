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
    @Column(columnDefinition = "TEXT")
    private String description;
    private String shortDescription;
    private String country;
    private String city;
    private String location;
    private int maxParticipants;
    private int duration;
    private double price;
    private boolean active = true;
    @OneToMany(mappedBy = "experience", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExperienceInstance> instances;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
