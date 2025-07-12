package com.example.csis3275.entities.dto;

import com.example.csis3275.entities.User;
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
    private boolean traveler;
    private boolean guide;

    public User toUser() {
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(password);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);
        user.setCountry(country);
        user.setAdmin(false);
        user.setTraveler(traveler);
        user.setGuide(guide);
        return user;
    }
}
