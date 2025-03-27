package com.example.freelancelocal.controller;

import com.example.freelancelocal.dto.FreelancerDto;
import com.example.freelancelocal.model.User;
import com.example.freelancelocal.service.FavoriteService;
import com.example.freelancelocal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<FreelancerDto>> getFavorites() {
        // Authentifizierten Benutzer holen
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(authentication.getName());

        // Prüfen, ob der Benutzer ein Kunde ist
        if (!"KUNDE".equals(currentUser.getRole())) {
            return ResponseEntity.status(403).build();
        }

        List<FreelancerDto> favorites = favoriteService.getFavoriteFreelancers(currentUser.getId());
        return ResponseEntity.ok(favorites);
    }

    @DeleteMapping("/{freelancerId}")
    public ResponseEntity<?> removeFromFavorites(@PathVariable Long freelancerId) {
        // Authentifizierten Benutzer holen
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(authentication.getName());

        // Prüfen, ob der Benutzer ein Kunde ist
        if (!"KUNDE".equals(currentUser.getRole())) {
            return ResponseEntity.status(403).build();
        }

        favoriteService.removeFromFavorites(currentUser.getId(), freelancerId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check/{freelancerId}")
    public ResponseEntity<Boolean> checkIfFavorite(@PathVariable Long freelancerId) {
        // Authentifizierten Benutzer holen
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(authentication.getName());

        boolean isFavorite = favoriteService.isFavorite(currentUser.getId(), freelancerId);
        return ResponseEntity.ok(isFavorite);
    }
}