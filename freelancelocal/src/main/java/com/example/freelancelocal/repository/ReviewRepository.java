package com.example.freelancelocal.repository;

import com.example.freelancelocal.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r WHERE r.freelancer.id = :freelancerId ORDER BY r.createdAt DESC")
    List<Review> findByFreelancerId(@Param("freelancerId") Long freelancerId);

    @Query("SELECT r FROM Review r WHERE r.customer.id = :customerId ORDER BY r.createdAt DESC")
    List<Review> findByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT r FROM Review r WHERE r.freelancer.id = :freelancerId AND r.customer.id = :customerId")
    List<Review> findByFreelancerAndCustomer(
            @Param("freelancerId") Long freelancerId,
            @Param("customerId") Long customerId);
}
