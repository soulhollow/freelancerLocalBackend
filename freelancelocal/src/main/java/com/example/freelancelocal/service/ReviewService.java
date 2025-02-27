package com.example.freelancelocal.service;

import com.example.freelancelocal.dto.ReviewDto;
import com.example.freelancelocal.model.FreelancerProfile;
import com.example.freelancelocal.model.Review;
import com.example.freelancelocal.model.User;
import com.example.freelancelocal.repository.FreelancerRepository;
import com.example.freelancelocal.repository.ReviewRepository;
import com.example.freelancelocal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private FreelancerRepository freelancerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FreelancerService freelancerService;

    public List<ReviewDto> getReviewsByFreelancer(Long freelancerId) {
        List<Review> reviews = reviewRepository.findByFreelancerId(freelancerId);
        return reviews.stream()
                .map(ReviewDto::new)
                .collect(Collectors.toList());
    }

    public List<ReviewDto> getReviewsByCustomer(Long customerId) {
        List<Review> reviews = reviewRepository.findByCustomerId(customerId);
        return reviews.stream()
                .map(ReviewDto::new)
                .collect(Collectors.toList());
    }

    public ReviewDto createReview(ReviewDto reviewDto, Long customerId) {
        // Prüfen, ob der Kunde bereits eine Bewertung für diesen Freelancer abgegeben hat
        List<Review> existingReviews = reviewRepository.findByFreelancerAndCustomer(
                reviewDto.getFreelancerId(), customerId);

        if (!existingReviews.isEmpty()) {
            // Bestehende Bewertung aktualisieren
            Review existingReview = existingReviews.get(0);
            existingReview.setRating(reviewDto.getRating());
            existingReview.setComment(reviewDto.getComment());
            existingReview.setCreatedAt(LocalDateTime.now());

            Review updated = reviewRepository.save(existingReview);

            // Durchschnittsbewertung aktualisieren
            freelancerService.updateAverageRating(reviewDto.getFreelancerId());

            return new ReviewDto(updated);
        }

        // Neue Bewertung erstellen
        Review review = new Review();
        review.setRating(reviewDto.getRating());
        review.setComment(reviewDto.getComment());
        review.setCreatedAt(LocalDateTime.now());

        // Freelancer zuordnen
        FreelancerProfile freelancer = freelancerRepository.findById(reviewDto.getFreelancerId())
                .orElseThrow(() -> new IllegalArgumentException("Freelancer nicht gefunden"));
        review.setFreelancer(freelancer);

        // Kunde zuordnen
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Kunde nicht gefunden"));
        review.setCustomer(customer);

        Review saved = reviewRepository.save(review);

        // Durchschnittsbewertung aktualisieren
        freelancerService.updateAverageRating(reviewDto.getFreelancerId());

        return new ReviewDto(saved);
    }

    public void deleteReview(Long reviewId, Long customerId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Bewertung nicht gefunden"));

        // Sicherheitscheck - nur eigene Bewertungen löschen
        if (!review.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("Keine Berechtigung zum Löschen dieser Bewertung");
        }

        Long freelancerId = review.getFreelancer().getId();

        reviewRepository.delete(review);

        // Durchschnittsbewertung aktualisieren
        freelancerService.updateAverageRating(freelancerId);
    }

    public boolean hasUserReviewedFreelancer(Long customerId, Long freelancerId) {
        List<Review> reviews = reviewRepository.findByFreelancerAndCustomer(freelancerId, customerId);
        return !reviews.isEmpty();
    }
}