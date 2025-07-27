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

    @PrePersist
    public void prePersist() {
        if (this.username == null) {
            this.username = "";
        }
        if (this.passwordHash == null) {
            this.passwordHash = "";
        }
        if (this.email == null) {
            this.email = "";
        }
        if (this.firstName == null) {
            this.firstName = "";
        }
        if (this.lastName == null) {
            this.lastName = "";
        }
        if (this.phone == null) {
            this.phone = "";
        }
        if (this.country == null) {
            this.country = "";
        }
    }
}
