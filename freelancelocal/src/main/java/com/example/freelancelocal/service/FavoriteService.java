package com.example.freelancelocal.service;

import com.example.freelancelocal.dto.FreelancerDto;
import com.example.freelancelocal.model.Favorite;
import com.example.freelancelocal.model.FreelancerProfile;
import com.example.freelancelocal.model.User;
import com.example.freelancelocal.repository.FavoriteRepository;
import com.example.freelancelocal.repository.FreelancerRepository;
import com.example.freelancelocal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FreelancerRepository freelancerRepository;

    @Autowired
    private FreelancerService freelancerService;

    public List<FreelancerDto> getFavoriteFreelancers(Long customerId) {
        List<Favorite> favorites = favoriteRepository.findByCustomerId(customerId);

        return favorites.stream()
                .map(favorite -> freelancerService.getFreelancer(favorite.getFreelancer().getId()))
                .collect(Collectors.toList());
    }

    public void addToFavorites(Long customerId, Long freelancerId) {
        // Prüfen, ob bereits als Favorit markiert
        if (isFavorite(customerId, freelancerId)) {
            return; // Bereits als Favorit markiert, nichts zu tun
        }

        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Kunde nicht gefunden"));

        FreelancerProfile freelancer = freelancerRepository.findById(freelancerId)
                .orElseThrow(() -> new IllegalArgumentException("Freelancer nicht gefunden"));

        Favorite favorite = new Favorite();
        favorite.setCustomer(customer);
        favorite.setFreelancer(freelancer);

        favoriteRepository.save(favorite);
    }

    public void removeFromFavorites(Long customerId, Long freelancerId) {
        favoriteRepository.findByCustomerAndFreelancer(customerId, freelancerId)
                .ifPresent(favorite -> favoriteRepository.delete(favorite));
    }

    public boolean isFavorite(Long customerId, Long freelancerId) {
        return favoriteRepository.findByCustomerAndFreelancer(customerId, freelancerId).isPresent();
    }
}