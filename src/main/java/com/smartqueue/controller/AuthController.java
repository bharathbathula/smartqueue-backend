package com.smartqueue.controller;

import com.smartqueue.dto.LoginRequest;
import com.smartqueue.dto.SignupRequest;
import com.smartqueue.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        System.out.println("Login request received for: " + loginRequest.getEmail());
        return authService.authenticateUser(loginRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        System.out.println("Register request received for: " + signUpRequest.getEmail());
        return authService.registerUser(signUpRequest);
    }
}
