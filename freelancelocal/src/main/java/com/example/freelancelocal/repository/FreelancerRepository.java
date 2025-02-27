package com.example.freelancelocal.repository;

import com.example.freelancelocal.model.FreelancerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FreelancerRepository extends JpaRepository<FreelancerProfile, Long> {

    List<FreelancerProfile> findByCity(String city);

    @Query("SELECT fp FROM FreelancerProfile fp WHERE " +
            "LOWER(fp.skills) LIKE LOWER(CONCAT('%', :skills, '%'))")
    List<FreelancerProfile> findBySkillsContaining(@Param("skills") String skills);

    @Query("SELECT fp FROM FreelancerProfile fp WHERE " +
            "fp.city = :city AND LOWER(fp.skills) LIKE LOWER(CONCAT('%', :skills, '%'))")
    List<FreelancerProfile> findByCityAndSkillsContaining(
            @Param("city") String city,
            @Param("skills") String skills);

    @Query("SELECT fp FROM FreelancerProfile fp " +
            "WHERE fp.available = true " +
            "ORDER BY fp.averageRating DESC")
    List<FreelancerProfile> findRecommended();

    @Query("SELECT fp FROM FreelancerProfile fp " +
            "WHERE fp.id NOT IN " +
            "(SELECT f.id FROM Favorite f WHERE f.customer.id = :userId) " +
            "AND fp.available = true " +
            "ORDER BY RAND()")
    List<FreelancerProfile> findForDiscovery(@Param("userId") Long userId);

    @Query("SELECT fp FROM FreelancerProfile fp " +
            "WHERE fp.user.id = :userId")
    FreelancerProfile findByUserId(@Param("userId") Long userId);
}