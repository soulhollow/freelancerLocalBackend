package com.example.freelancelocal.controller;

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
@RequestMapping("/api/services")
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private FreelancerService freelancerService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<ServiceDto>> searchServices(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double minRate,
            @RequestParam(required = false) Double maxRate) {

        List<ServiceDto> services = serviceService.searchServices(keyword, minRate, maxRate);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceDto> getService(@PathVariable Long id) {
        try {
            ServiceDto service = serviceService.getService(id);
            return ResponseEntity.ok(service);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceDto> updateService(
            @PathVariable Long id,
            @RequestBody ServiceDto serviceDto) {

        try {
            // Sicherheitscheck - nur der Besitzer darf seinen Service aktualisieren
            ServiceDto existingService = serviceService.getService(id);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findByUsername(authentication.getName());

            FreelancerProfile freelancer = freelancerService.getFreelancerProfile(existingService.getFreelancerId());
            if (freelancer == null || !freelancer.getUser().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            ServiceDto updated = serviceService.updateService(id, serviceDto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        try {
            // Sicherheitscheck - nur der Besitzer darf seinen Service löschen
            ServiceDto existingService = serviceService.getService(id);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findByUsername(authentication.getName());

            FreelancerProfile freelancer = freelancerService.getFreelancerProfile(existingService.getFreelancerId());
            if (freelancer == null || !freelancer.getUser().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            serviceService.deleteService(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}