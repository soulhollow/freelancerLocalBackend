package com.example.freelancelocal.repository;

import com.example.freelancelocal.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    @Query("SELECT f FROM Favorite f WHERE f.customer.id = :customerId")
    List<Favorite> findByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT f FROM Favorite f WHERE f.customer.id = :customerId AND f.freelancer.id = :freelancerId")
    Optional<Favorite> findByCustomerAndFreelancer(
            @Param("customerId") Long customerId,
            @Param("freelancerId") Long freelancerId);
}