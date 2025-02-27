package com.example.freelancelocal.dto;

import com.example.freelancelocal.model.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private Long id;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
    private Long freelancerId;
    private Long customerId;
    private String customerUsername;

    public ReviewDto(Review review) {
        if (review != null) {
            this.id = review.getId();
            this.rating = review.getRating();
            this.comment = review.getComment();
            this.createdAt = review.getCreatedAt();

            if (review.getFreelancer() != null) {
                this.freelancerId = review.getFreelancer().getId();
            }

            if (review.getCustomer() != null) {
                this.customerId = review.getCustomer().getId();
                this.customerUsername = review.getCustomer().getUsername();
            }
        }
    }
}