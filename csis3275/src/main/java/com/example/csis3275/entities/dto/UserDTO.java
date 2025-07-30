package com.example.csis3275.entities.dto;

import com.example.csis3275.entities.User;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
public class UserDTO {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String country;
    @Transient
    private String sessionRole;


    public User toUser() {
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(password);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);
        user.setCountry(country);
        return user;
    }
}
