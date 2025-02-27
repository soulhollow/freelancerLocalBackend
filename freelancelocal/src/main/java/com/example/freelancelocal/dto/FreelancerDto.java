package com.example.freelancelocal.dto;

import com.example.freelancelocal.model.FreelancerProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FreelancerDto {
    private Long id;
    private Long userId;
    private String username;
    private String email;
    private String biography;
    private String skills;
    private String city;
    private Boolean available;
    private Double averageRating;
    private List<ServiceDto> services = new ArrayList<>();
    private List<ReviewDto> reviews = new ArrayList<>();

    public FreelancerDto(FreelancerProfile profile) {
        if (profile != null) {
            this.id = profile.getId();
            this.biography = profile.getBiography();
            this.skills = profile.getSkills();
            this.city = profile.getCity();
            this.available = profile.getAvailable();
            this.averageRating = profile.getAverageRating();

            if (profile.getUser() != null) {
                this.userId = profile.getUser().getId();
                this.username = profile.getUser().getUsername();
                this.email = profile.getUser().getEmail();
            }
        }
    }
}