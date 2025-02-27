package com.example.freelancelocal.controller;

import com.example.freelancelocal.dto.FreelancerDto;
import com.example.freelancelocal.model.User;
import com.example.freelancelocal.service.FavoriteService;
import com.example.freelancelocal.service.FreelancerService;
import com.example.freelancelocal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/discovery")
public class DiscoveryController {

    @Autowired
    private FreelancerService freelancerService;

    @Autowired
    private UserService userService;

    @Autowired
    private FavoriteService favoriteService;

    @GetMapping("/next")
    public ResponseEntity<FreelancerDto> getNextFreelancer() {
        // Authentifizierter Benutzer
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(authentication.getName());

        // Prüfen, ob der Benutzer ein Kunde ist
        if (!"KUNDE".equals(currentUser.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        FreelancerDto freelancer = freelancerService.getFreelancerForDiscovery(currentUser.getId());
        if (freelancer == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(freelancer);
    }

    @PostMapping("/like/{freelancerId}")
    public ResponseEntity<?> likeFreelancer(@PathVariable Long freelancerId) {
        // Authentifizierter Benutzer
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(authentication.getName());

        // Prüfen, ob der Benutzer ein Kunde ist
        if (!"KUNDE".equals(currentUser.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            favoriteService.addToFavorites(currentUser.getId(), freelancerId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/pass/{freelancerId}")
    public ResponseEntity<?> passFreelancer(@PathVariable Long freelancerId) {
        // Diese Methode könnte in Zukunft genutzt werden, um ein Logging-System für "übersprungene"
        // Freelancer zu implementieren oder um das Empfehlungssystem zu verbessern

        // Für jetzt ist sie ein Platzhalter, der immer erfolgreich zurückkehrt
        return ResponseEntity.ok().build();
    }
}