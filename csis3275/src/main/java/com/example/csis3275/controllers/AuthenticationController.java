package com.example.csis3275.controllers;

import com.example.csis3275.entities.User;
import com.example.csis3275.entities.dto.LoginResponse;
import com.example.csis3275.entities.dto.RegisterUser;
import com.example.csis3275.services.AuthenticationService;
import com.example.csis3275.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUser registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody RegisterUser loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(new org.springframework.security.core.userdetails.User(
                authenticatedUser.getUsername(),
                authenticatedUser.getPasswordHash(),
                java.util.Collections.emptyList()
        ));

        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }
}
