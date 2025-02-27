package com.example.freelancelocal.repository;

import com.example.freelancelocal.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    @Query("SELECT s FROM Service s WHERE s.freelancer.id = :freelancerId")
    List<Service> findByFreelancerId(@Param("freelancerId") Long freelancerId);

    @Query("SELECT s FROM Service s WHERE " +
            "LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Service> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT s FROM Service s WHERE " +
            "s.hourlyRate BETWEEN :minRate AND :maxRate")
    List<Service> findByHourlyRateRange(
            @Param("minRate") Double minRate,
            @Param("maxRate") Double maxRate);
}