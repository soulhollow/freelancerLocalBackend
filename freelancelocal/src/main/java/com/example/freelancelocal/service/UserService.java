package com.example.freelancelocal.service;

import com.example.freelancelocal.dto.LoginRequest;
import com.example.freelancelocal.dto.RegisterRequest;
import com.example.freelancelocal.dto.UserProfileDto;
import com.example.freelancelocal.model.User;
import com.example.freelancelocal.repository.UserRepository;
import com.example.freelancelocal.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public User register(RegisterRequest registerRequest) {
        // Check if username already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Username ist bereits vergeben");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email ist bereits registriert");
        }

        // Validate role
        String role = registerRequest.getRole();
        if (role == null || (!role.equals("FREELANCER") && !role.equals("KUNDE"))) {
            throw new IllegalArgumentException("Rolle muss entweder 'FREELANCER' oder 'KUNDE' sein");
        }

        // Create new user with hashed password
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(registerRequest.getRole());

        return userRepository.save(user);
    }

    public String login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Falsche Anmeldedaten"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Falsche Anmeldedaten");
        }

        // Generate JWT token with role
        return jwtTokenProvider.createToken(user.getUsername(), user.getRole());
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Benutzer nicht gefunden"));
    }

    public UserProfileDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Benutzer nicht gefunden"));

        return new UserProfileDto(user);
    }

    public UserProfileDto updateUserProfile(Long userId, UserProfileDto profileDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Benutzer nicht gefunden"));

        // Nur bestimmte Felder können aktualisiert werden
        if (profileDto.getEmail() != null && !profileDto.getEmail().equals(user.getEmail())) {
            // Prüfen, ob die E-Mail bereits verwendet wird
            if (userRepository.existsByEmail(profileDto.getEmail())) {
                throw new IllegalArgumentException("Email ist bereits registriert");
            }
            user.setEmail(profileDto.getEmail());
        }

        if (profileDto.getUsername() != null && !profileDto.getUsername().equals(user.getUsername())) {
            // Prüfen, ob der Benutzername bereits verwendet wird
            if (userRepository.existsByUsername(profileDto.getUsername())) {
                throw new IllegalArgumentException("Username ist bereits vergeben");
            }
            user.setUsername(profileDto.getUsername());
        }

        // Rolle kann nicht geändert werden

        User updated = userRepository.save(user);
        return new UserProfileDto(updated);
    }

    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Benutzer nicht gefunden"));

        // Prüfen, ob das aktuelle Passwort korrekt ist
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Aktuelles Passwort ist falsch");
        }

        // Neues Passwort setzen
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}