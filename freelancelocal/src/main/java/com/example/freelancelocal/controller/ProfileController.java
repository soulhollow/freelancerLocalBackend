package com.example.freelancelocal.controller;

import com.example.freelancelocal.dto.FreelancerDto;
import com.example.freelancelocal.dto.UserProfileDto;
import com.example.freelancelocal.model.FreelancerProfile;
import com.example.freelancelocal.model.User;
import com.example.freelancelocal.service.FreelancerService;
import com.example.freelancelocal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private FreelancerService freelancerService;

    @GetMapping
    public ResponseEntity<UserProfileDto> getUserProfile() {
        // Authentifizierter Benutzer
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(authentication.getName());

        UserProfileDto profile = userService.getUserProfile(currentUser.getId());
        return ResponseEntity.ok(profile);
    }

    @PutMapping
    public ResponseEntity<UserProfileDto> updateUserProfile(@RequestBody UserProfileDto profileDto) {
        // Authentifizierter Benutzer
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(authentication.getName());

        UserProfileDto updated = userService.updateUserProfile(currentUser.getId(), profileDto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/freelancer")
    public ResponseEntity<FreelancerDto> getFreelancerProfile() {
        // Authentifizierter Benutzer
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(authentication.getName());

        // Prüfen, ob der Benutzer ein Freelancer ist
        if (!"FREELANCER".equals(currentUser.getRole())) {
            return ResponseEntity.notFound().build();
        }

        FreelancerProfile profile = freelancerService.getFreelancerProfile(currentUser.getId());
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }

        FreelancerDto freelancerDto = freelancerService.getFreelancer(profile.getId());
        return ResponseEntity.ok(freelancerDto);
    }

    @PostMapping("/freelancer")
    public ResponseEntity<FreelancerDto> createOrUpdateFreelancerProfile(@RequestBody FreelancerDto freelancerDto) {
        // Authentifizierter Benutzer
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(authentication.getName());

        // Prüfen, ob der Benutzer ein Freelancer ist
        if (!"FREELANCER".equals(currentUser.getRole())) {
            return ResponseEntity.badRequest().build();
        }

        FreelancerDto created = freelancerService.createFreelancerProfile(currentUser, freelancerDto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeDto passwordDto) {
        // Authentifizierter Benutzer
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(authentication.getName());

        try {
            userService.changePassword(currentUser.getId(), passwordDto.getCurrentPassword(), passwordDto.getNewPassword());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    static class PasswordChangeDto {
        private String currentPassword;
        private String newPassword;

        public String getCurrentPassword() {
            return currentPassword;
        }

        public void setCurrentPassword(String currentPassword) {
            this.currentPassword = currentPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}