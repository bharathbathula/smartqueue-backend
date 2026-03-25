package com.smartqueue.service;

import com.smartqueue.dto.JwtResponse;
import com.smartqueue.dto.LoginRequest;
import com.smartqueue.dto.MessageResponse;
import com.smartqueue.dto.SignupRequest;
import com.smartqueue.model.Role;
import com.smartqueue.model.Staff;
import com.smartqueue.model.User;
import com.smartqueue.repository.StaffRepository;
import com.smartqueue.repository.UserRepository;
import com.smartqueue.security.JwtUtils;
import com.smartqueue.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getEmail(),
                roles));
    }

    public ResponseEntity<?> registerUser(SignupRequest signUpRequest) {
        System.out.println("Processing registration for: " + signUpRequest.getEmail() + " with role: " + signUpRequest.getRole());
        
        try {
            if (userRepository.existsByEmail(signUpRequest.getEmail()) || 
                staffRepository.existsByEmail(signUpRequest.getEmail())) {
                System.out.println("Registration failed: Email already in use.");
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Email is already in use!"));
            }

            String strRole = signUpRequest.getRole();
            
            if (strRole != null && strRole.equalsIgnoreCase("admin")) {
                System.out.println("Registering Admin...");
                Staff admin = Staff.builder()
                        .name(signUpRequest.getName())
                        .email(signUpRequest.getEmail())
                        .password(encoder.encode(signUpRequest.getPassword()))
                        .role(Role.ADMIN)
                        .build();
                staffRepository.save(admin);
                return ResponseEntity.ok(new MessageResponse("Admin registered successfully!"));
            } else if (strRole != null && strRole.equalsIgnoreCase("staff")) {
                System.out.println("Registering Staff...");
                Staff staff = Staff.builder()
                        .name(signUpRequest.getName())
                        .email(signUpRequest.getEmail())
                        .password(encoder.encode(signUpRequest.getPassword()))
                        .role(Role.STAFF)
                        .specialty(signUpRequest.getSpecialty())
                        .build();
                staffRepository.save(staff);
                return ResponseEntity.ok(new MessageResponse("Staff registered successfully!"));
            } else {
                // Default to USER
                System.out.println("Registering User...");
                User user = User.builder()
                        .name(signUpRequest.getName())
                        .email(signUpRequest.getEmail())
                        .password(encoder.encode(signUpRequest.getPassword()))
                        .phone(signUpRequest.getPhone())
                        .role(Role.USER)
                        .build();

                userRepository.save(user);
                System.out.println("User registered successfully.");

                return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
            }
        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity
                    .internalServerError()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
}
