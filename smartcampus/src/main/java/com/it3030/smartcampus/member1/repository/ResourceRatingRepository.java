package com.it3030.smartcampus.member1.repository;

import com.it3030.smartcampus.member1.model.ResourceRating;
import com.it3030.smartcampus.member1.model.EnhancedResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRatingRepository extends JpaRepository<ResourceRating, Long> {
    
    Optional<ResourceRating> findByResourceAndUserId(EnhancedResource resource, String userId);
    
    List<ResourceRating> findByResource(EnhancedResource resource);
    
    List<ResourceRating> findByResourceId(Long resourceId);
    
    Page<ResourceRating> findByResource(EnhancedResource resource, Pageable pageable);
    
    @Query("SELECT AVG(r.rating) FROM ResourceRating r WHERE r.resource.id = :resourceId")
    Double findAverageRatingByResourceId(@Param("resourceId") Long resourceId);
    
    @Query("SELECT COUNT(r) FROM ResourceRating r WHERE r.resource.id = :resourceId")
    Long countRatingsByResourceId(@Param("resourceId") Long resourceId);
    
    @Query("SELECT r FROM ResourceRating r WHERE r.rating >= :minRating ORDER BY r.rating DESC")
    List<ResourceRating> findByMinRating(@Param("minRating") Integer minRating);
    
    @Query("SELECT r FROM ResourceRating r WHERE r.reviewText IS NOT NULL AND r.reviewText != '' ORDER BY r.createdAt DESC")
    List<ResourceRating> findReviewsWithText();
    
    @Query("SELECT r FROM ResourceRating r WHERE r.resource.id = :resourceId AND r.reviewText IS NOT NULL AND r.reviewText != '' ORDER BY r.createdAt DESC")
    List<ResourceRating> findReviewsWithTextByResourceId(@Param("resourceId") Long resourceId);
    
    @Query("SELECT r FROM ResourceRating r WHERE r.userId = :userId ORDER BY r.createdAt DESC")
    List<ResourceRating> findByUserId(@Param("userId") String userId);
    
    @Query("SELECT COUNT(r) FROM ResourceRating r WHERE r.rating >= 4")
    long countPositiveRatings();
    
    @Query("SELECT COUNT(r) FROM ResourceRating r WHERE r.rating <= 2")
    long countNegativeRatings();
}
