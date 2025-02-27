package com.example.freelancelocal.controller;

import com.example.freelancelocal.dto.FreelancerDto;
import com.example.freelancelocal.dto.ServiceDto;
import com.example.freelancelocal.model.FreelancerProfile;
import com.example.freelancelocal.model.User;
import com.example.freelancelocal.service.FreelancerService;
import com.example.freelancelocal.service.ServiceService;
import com.example.freelancelocal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/freelancers")
public class FreelancerController {

    @Autowired
    private FreelancerService freelancerService;

    @Autowired
    private UserService userService;

    @Autowired
    private ServiceService serviceService;

    @GetMapping("/search")
    public ResponseEntity<List<FreelancerDto>> searchFreelancers(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String skills) {

        List<FreelancerDto> freelancers;

        if (city != null && skills != null) {
            freelancers = freelancerService.findByCityAndSkills(city, skills);
        } else if (city != null) {
            freelancers = freelancerService.findByCity(city);
        } else if (skills != null) {
            freelancers = freelancerService.findBySkills(skills);
        } else {
            freelancers = freelancerService.findAll();
        }

        return ResponseEntity.ok(freelancers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FreelancerDto> getFreelancer(@PathVariable Long id) {
        FreelancerDto freelancer = freelancerService.getFreelancer(id);
        if (freelancer != null) {
            return ResponseEntity.ok(freelancer);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<FreelancerDto> updateFreelancer(
            @PathVariable Long id,
            @RequestBody FreelancerDto freelancerDto) {

        // Sicherheitscheck - nur der eigene Freelancer-Eintrag darf aktualisiert werden
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(authentication.getName());

        FreelancerProfile profile = freelancerService.getFreelancerProfile(id);
        if (profile == null || !profile.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        FreelancerDto updated = freelancerService.updateFreelancer(id, freelancerDto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}/services")
    public ResponseEntity<List<ServiceDto>> getFreelancerServices(@PathVariable Long id) {
        List<ServiceDto> services = serviceService.findByFreelancerId(id);
        return ResponseEntity.ok(services);
    }

    @PostMapping("/{id}/services")
    public ResponseEntity<ServiceDto> addService(
            @PathVariable Long id,
            @RequestBody ServiceDto serviceDto) {

        // Sicherheitscheck - nur der eigene Freelancer-Eintrag darf aktualisiert werden
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(authentication.getName());

        FreelancerProfile profile = freelancerService.getFreelancerProfile(id);
        if (profile == null || !profile.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        serviceDto.setFreelancerId(id);
        ServiceDto created = serviceService.createService(serviceDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/recommended")
    public ResponseEntity<List<FreelancerDto>> getRecommendedFreelancers() {
        // Authentifizierter Benutzer
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(authentication.getName());

        List<FreelancerDto> recommended = freelancerService.getRecommendedFreelancers(currentUser.getId());
        return ResponseEntity.ok(recommended);
    }

    @GetMapping("/discovery")
    public ResponseEntity<FreelancerDto> getFreelancerForDiscovery() {
        // Authentifizierter Benutzer
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(authentication.getName());

        FreelancerDto freelancer = freelancerService.getFreelancerForDiscovery(currentUser.getId());
        if (freelancer != null) {
            return ResponseEntity.ok(freelancer);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}