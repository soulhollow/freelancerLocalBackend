package com.example.freelancelocal.dto;

import com.example.freelancelocal.model.Service;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDto {
    private Long id;
    private String title;
    private String description;
    private Double hourlyRate;
    private Long freelancerId;

    public ServiceDto(Service service) {
        if (service != null) {
            this.id = service.getId();
            this.title = service.getTitle();
            this.description = service.getDescription();
            this.hourlyRate = service.getHourlyRate();

            if (service.getFreelancer() != null) {
                this.freelancerId = service.getFreelancer().getId();
            }
        }
    }
}