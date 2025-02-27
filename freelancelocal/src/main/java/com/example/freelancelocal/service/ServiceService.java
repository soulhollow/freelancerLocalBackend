package com.example.freelancelocal.service;

import com.example.freelancelocal.dto.ServiceDto;
import com.example.freelancelocal.model.FreelancerProfile;
import com.example.freelancelocal.model.Service;
import com.example.freelancelocal.repository.FreelancerRepository;
import com.example.freelancelocal.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private FreelancerRepository freelancerRepository;

    public List<ServiceDto> findByFreelancerId(Long freelancerId) {
        List<Service> services = serviceRepository.findByFreelancerId(freelancerId);
        return services.stream()
                .map(ServiceDto::new)
                .collect(Collectors.toList());
    }

    public ServiceDto createService(ServiceDto serviceDto) {
        Service service = new Service();
        service.setTitle(serviceDto.getTitle());
        service.setDescription(serviceDto.getDescription());
        service.setHourlyRate(serviceDto.getHourlyRate());

        // Freelancer zuordnen
        if (serviceDto.getFreelancerId() != null) {
            FreelancerProfile freelancer = freelancerRepository.findById(serviceDto.getFreelancerId())
                    .orElseThrow(() -> new IllegalArgumentException("Freelancer nicht gefunden"));
            service.setFreelancer(freelancer);
        }

        Service saved = serviceRepository.save(service);
        return new ServiceDto(saved);
    }

    public ServiceDto updateService(Long id, ServiceDto serviceDto) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service nicht gefunden"));

        service.setTitle(serviceDto.getTitle());
        service.setDescription(serviceDto.getDescription());
        service.setHourlyRate(serviceDto.getHourlyRate());

        Service updated = serviceRepository.save(service);
        return new ServiceDto(updated);
    }

    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }

    public ServiceDto getService(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service nicht gefunden"));
        return new ServiceDto(service);
    }

    public List<ServiceDto> searchServices(String keyword, Double minRate, Double maxRate) {
        List<Service> services;

        if (keyword != null && minRate != null && maxRate != null) {
            // Kombination aus Keyword und Preisspanne
            // Hier müsste eine spezifische Abfrage implementiert werden
            services = serviceRepository.findAll().stream()
                    .filter(s -> (s.getTitle().toLowerCase().contains(keyword.toLowerCase())
                            || s.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                            && s.getHourlyRate() >= minRate
                            && s.getHourlyRate() <= maxRate)
                    .collect(Collectors.toList());
        } else if (keyword != null) {
            services = serviceRepository.findByKeyword(keyword);
        } else if (minRate != null && maxRate != null) {
            services = serviceRepository.findByHourlyRateRange(minRate, maxRate);
        } else {
            services = serviceRepository.findAll();
        }

        return services.stream()
                .map(ServiceDto::new)
                .collect(Collectors.toList());
    }
}