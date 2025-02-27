package com.example.freelancelocal.service;

import com.example.freelancelocal.dto.FreelancerDto;
import com.example.freelancelocal.dto.ReviewDto;
import com.example.freelancelocal.dto.ServiceDto;
import com.example.freelancelocal.model.FreelancerProfile;
import com.example.freelancelocal.model.Review;
import com.example.freelancelocal.model.User;
import com.example.freelancelocal.repository.FreelancerRepository;
import com.example.freelancelocal.repository.ReviewRepository;
import com.example.freelancelocal.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FreelancerService {

    @Autowired
    private FreelancerRepository freelancerRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    public List<FreelancerDto> findByCity(String city) {
        List<FreelancerProfile> freelancers = freelancerRepository.findByCity(city);
        return convertToDtoWithDetails(freelancers);
    }

    public List<FreelancerDto> findBySkills(String skills) {
        List<FreelancerProfile> freelancers = freelancerRepository.findBySkillsContaining(skills);
        return convertToDtoWithDetails(freelancers);
    }

    public List<FreelancerDto> findByCityAndSkills(String city, String skills) {
        List<FreelancerProfile> freelancers = freelancerRepository.findByCityAndSkillsContaining(city, skills);
        return convertToDtoWithDetails(freelancers);
    }

    public List<FreelancerDto> findAll() {
        List<FreelancerProfile> freelancers = freelancerRepository.findAll();
        return convertToDtoWithDetails(freelancers);
    }

    public FreelancerDto getFreelancer(Long id) {
        FreelancerProfile fp = freelancerRepository.findById(id).orElse(null);
        if (fp == null) {
            return null;
        }

        FreelancerDto dto = new FreelancerDto(fp);

        // Dienste hinzufügen
        List<ServiceDto> services = serviceRepository.findByFreelancerId(id).stream()
                .map(ServiceDto::new)
                .collect(Collectors.toList());
        dto.setServices(services);

        // Bewertungen hinzufügen
        List<ReviewDto> reviews = reviewRepository.findByFreelancerId(id).stream()
                .map(ReviewDto::new)
                .collect(Collectors.toList());
        dto.setReviews(reviews);

        return dto;
    }

    public FreelancerProfile getFreelancerProfile(Long id) {
        return freelancerRepository.findById(id).orElse(null);
    }

    public FreelancerDto updateFreelancer(Long id, FreelancerDto freelancerDto) {
        FreelancerProfile fp = freelancerRepository.findById(id).orElse(null);
        if (fp != null) {
            fp.setBiography(freelancerDto.getBiography());
            fp.setSkills(freelancerDto.getSkills());
            fp.setCity(freelancerDto.getCity());
            fp.setAvailable(freelancerDto.getAvailable());
            freelancerRepository.save(fp);

            // Aktualisiertes Profil mit allen Details zurückgeben
            return getFreelancer(id);
        }
        return null;
    }

    public FreelancerDto createFreelancerProfile(User user, FreelancerDto freelancerDto) {
        // Prüfen, ob bereits ein Profil existiert
        FreelancerProfile existingProfile = freelancerRepository.findByUserId(user.getId());
        if (existingProfile != null) {
            // Profil existiert bereits, aktualisieren
            existingProfile.setBiography(freelancerDto.getBiography());
            existingProfile.setSkills(freelancerDto.getSkills());
            existingProfile.setCity(freelancerDto.getCity());
            existingProfile.setAvailable(freelancerDto.getAvailable());
            FreelancerProfile saved = freelancerRepository.save(existingProfile);
            return new FreelancerDto(saved);
        }

        // Neues Profil erstellen
        FreelancerProfile newProfile = new FreelancerProfile();
        newProfile.setUser(user);
        newProfile.setBiography(freelancerDto.getBiography());
        newProfile.setSkills(freelancerDto.getSkills());
        newProfile.setCity(freelancerDto.getCity());
        newProfile.setAvailable(freelancerDto.getAvailable());
        newProfile.setAverageRating(0.0); // Anfangsbewertung

        FreelancerProfile saved = freelancerRepository.save(newProfile);
        return new FreelancerDto(saved);
    }

    public List<FreelancerDto> getRecommendedFreelancers(Long userId) {
        List<FreelancerProfile> recommended = freelancerRepository.findRecommended();
        return convertToDtoWithDetails(recommended);
    }

    public FreelancerDto getFreelancerForDiscovery(Long userId) {
        List<FreelancerProfile> forDiscovery = freelancerRepository.findForDiscovery(userId);
        if (forDiscovery.isEmpty()) {
            return null;
        }

        FreelancerProfile freelancer = forDiscovery.get(0);
        FreelancerDto dto = new FreelancerDto(freelancer);

        // Dienste hinzufügen
        List<ServiceDto> services = serviceRepository.findByFreelancerId(freelancer.getId()).stream()
                .map(ServiceDto::new)
                .collect(Collectors.toList());
        dto.setServices(services);

        // Bewertungen hinzufügen (begrenzt auf 5)
        List<ReviewDto> reviews = reviewRepository.findByFreelancerId(freelancer.getId()).stream()
                .limit(5)
                .map(ReviewDto::new)
                .collect(Collectors.toList());
        dto.setReviews(reviews);

        return dto;
    }

    private List<FreelancerDto> convertToDtoWithDetails(List<FreelancerProfile> profiles) {
        return profiles.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private FreelancerDto convertToDto(FreelancerProfile profile) {
        FreelancerDto dto = new FreelancerDto(profile);

        // Dienste hinzufügen (begrenzt auf 3 für Listenansicht)
        List<ServiceDto> services = serviceRepository.findByFreelancerId(profile.getId()).stream()
                .limit(3)
                .map(ServiceDto::new)
                .collect(Collectors.toList());
        dto.setServices(services);

        return dto;
    }

    public double updateAverageRating(Long freelancerId) {
        List<Review> reviews = reviewRepository.findByFreelancerId(freelancerId);

        if (reviews.isEmpty()) {
            return 0.0;
        }

        double averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        FreelancerProfile profile = freelancerRepository.findById(freelancerId).orElse(null);
        if (profile != null) {
            profile.setAverageRating(averageRating);
            freelancerRepository.save(profile);
        }

        return averageRating;
    }
}