package com.example.csis3275.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    @JsonIgnore
    private String passwordHash;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String country;
    private boolean admin;
    private boolean traveler;
    private boolean guide;
}
