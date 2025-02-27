package com.example.freelancelocal.controller;

import com.example.freelancelocal.dto.ReviewDto;
import com.example.freelancelocal.model.User;
import com.example.freelancelocal.service.ReviewService;
import com.example.freelancelocal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @GetMapping("/freelancer/{freelancerId}")
    public ResponseEntity<List<ReviewDto>> getReviewsByFreelancer(@PathVariable Long freelancerId) {
        List<ReviewDto> reviews = reviewService.getReviewsByFreelancer(freelancerId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/check/{freelancerId}")
    public ResponseEntity<Boolean> checkIfUserHasReviewed(@PathVariable Long freelancerId) {
        // Authentifizierter Benutzer
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(authentication.getName());

        // Prüfen, ob der Benutzer bereits eine Bewertung abgegeben hat
        boolean hasReviewed = reviewService.hasUserReviewedFreelancer(currentUser.getId(), freelancerId);

        return ResponseEntity.ok(hasReviewed);
    }

    @PostMapping("/freelancer/{freelancerId}")
    public ResponseEntity<?> createReview(
            @PathVariable Long freelancerId,
            @RequestBody ReviewDto reviewDto) {

        // Authentifizierter Benutzer
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(authentication.getName());

        // Prüfen, ob der Benutzer ein Kunde ist
        if (!"KUNDE".equals(currentUser.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Nur Kunden können Bewertungen abgeben");
        }

        // Freelancer-ID setzen
        reviewDto.setFreelancerId(freelancerId);

        try {
            ReviewDto created = reviewService.createReview(reviewDto, currentUser.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId) {
        // Authentifizierter Benutzer
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(authentication.getName());

        try {
            reviewService.deleteReview(reviewId, currentUser.getId());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}