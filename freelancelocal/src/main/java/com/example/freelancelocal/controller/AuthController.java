package com.example.freelancelocal.controller;

import com.example.freelancelocal.dto.AuthResponse;
import com.example.freelancelocal.dto.LoginRequest;
import com.example.freelancelocal.dto.RegisterRequest;
import com.example.freelancelocal.model.User;
import com.example.freelancelocal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            User newUser = userService.register(registerRequest);
            AuthResponse response = new AuthResponse(newUser.getId(), newUser.getUsername(), newUser.getEmail(), newUser.getRole(), null);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fehler bei der Registrierung: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            String token = userService.login(loginRequest);
            User user = userService.findByUsername(loginRequest.getUsername());
            AuthResponse response = new AuthResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), token);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Falsche Anmeldedaten");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fehler beim Login: " + e.getMessage());
        }
    }
}