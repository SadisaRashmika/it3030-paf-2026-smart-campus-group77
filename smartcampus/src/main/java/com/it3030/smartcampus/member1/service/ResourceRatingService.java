package com.it3030.smartcampus.member1.service;

import com.it3030.smartcampus.member1.model.ResourceRating;
import com.it3030.smartcampus.member1.model.EnhancedResource;
import com.it3030.smartcampus.member1.repository.ResourceRatingRepository;
import com.it3030.smartcampus.member1.repository.EnhancedResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ResourceRatingService {

    @Autowired
    private ResourceRatingRepository ratingRepository;

    @Autowired
    private EnhancedResourceRepository resourceRepository;

    public List<ResourceRating> getAllRatings() {
        return ratingRepository.findAll();
    }

    public Optional<ResourceRating> getRatingById(Long id) {
        return ratingRepository.findById(id);
    }

    public List<ResourceRating> getRatingsByResourceId(Long resourceId) {
        return ratingRepository.findByResourceId(resourceId);
    }

    public Page<ResourceRating> getRatingsByResource(EnhancedResource resource, Pageable pageable) {
        return ratingRepository.findByResource(resource, pageable);
    }

    public ResourceRating createRating(ResourceRating rating) {
        // Validate rating value
        if (rating.getRating() < 1 || rating.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        // Check if user has already rated this resource
        Optional<ResourceRating> existingRating = ratingRepository.findByResourceAndUserId(rating.getResource(), rating.getUserId());
        if (existingRating.isPresent()) {
            throw new IllegalStateException("User has already rated this resource");
        }

        // Validate resource exists
        EnhancedResource resource = resourceRepository.findById(rating.getResource().getId())
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + rating.getResource().getId()));

        rating.setCreatedAt(LocalDateTime.now());
        rating.setUpdatedAt(LocalDateTime.now());

        ResourceRating savedRating = ratingRepository.save(rating);

        // Update resource rating statistics
        updateResourceRatingStatistics(resource.getId());

        return savedRating;
    }

    public ResourceRating updateRating(Long id, ResourceRating ratingDetails) {
        ResourceRating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rating not found with id: " + id));

        // Validate rating value
        if (ratingDetails.getRating() < 1 || ratingDetails.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        rating.setRating(ratingDetails.getRating());
        rating.setReviewText(ratingDetails.getReviewText());
        rating.setUpdatedAt(LocalDateTime.now());

        ResourceRating savedRating = ratingRepository.save(rating);

        // Update resource rating statistics
        updateResourceRatingStatistics(rating.getResource().getId());

        return savedRating;
    }

    public void deleteRating(Long id) {
        ResourceRating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rating not found with id: " + id));

        Long resourceId = rating.getResource().getId();

        ratingRepository.delete(rating);

        // Update resource rating statistics
        updateResourceRatingStatistics(resourceId);
    }

    public Optional<ResourceRating> getUserRatingForResource(Long resourceId, String userId) {
        EnhancedResource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + resourceId));

        return ratingRepository.findByResourceAndUserId(resource, userId);
    }

    public Double getAverageRatingForResource(Long resourceId) {
        return ratingRepository.findAverageRatingByResourceId(resourceId);
    }

    public Long getRatingCountForResource(Long resourceId) {
        return ratingRepository.countRatingsByResourceId(resourceId);
    }

    public List<ResourceRating> getReviewsWithText() {
        return ratingRepository.findReviewsWithText();
    }

    public List<ResourceRating> getReviewsWithTextByResourceId(Long resourceId) {
        return ratingRepository.findReviewsWithTextByResourceId(resourceId);
    }

    public List<ResourceRating> getRatingsByUserId(String userId) {
        return ratingRepository.findByUserId(userId);
    }

    public List<ResourceRating> getHighRatings(Integer minRating) {
        return ratingRepository.findByMinRating(minRating);
    }

    public long getPositiveRatingCount() {
        return ratingRepository.countPositiveRatings();
    }

    public long getNegativeRatingCount() {
        return ratingRepository.countNegativeRatings();
    }

    private void updateResourceRatingStatistics(Long resourceId) {
        Double averageRating = ratingRepository.findAverageRatingByResourceId(resourceId);
        Long ratingCount = ratingRepository.countRatingsByResourceId(resourceId);

        EnhancedResource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + resourceId));

        resource.setAverageRating(averageRating != null ? averageRating : 0.0);
        resource.setTotalRatings(ratingCount != null ? ratingCount.intValue() : 0);
        resource.setUpdatedAt(LocalDateTime.now());

        resourceRepository.save(resource);
    }

    public ResourceRating updateOrCreateRating(Long resourceId, String userId, Integer rating, String reviewText) {
        EnhancedResource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + resourceId));

        Optional<ResourceRating> existingRating = ratingRepository.findByResourceAndUserId(resource, userId);

        if (existingRating.isPresent()) {
            // Update existing rating
            ResourceRating ratingToUpdate = existingRating.get();
            ratingToUpdate.setRating(rating);
            ratingToUpdate.setReviewText(reviewText);
            ratingToUpdate.setUpdatedAt(LocalDateTime.now());
            
            ResourceRating savedRating = ratingRepository.save(ratingToUpdate);
            updateResourceRatingStatistics(resourceId);
            return savedRating;
        } else {
            // Create new rating
            ResourceRating newRating = new ResourceRating(resource, userId, rating, reviewText);
            newRating.setCreatedAt(LocalDateTime.now());
            newRating.setUpdatedAt(LocalDateTime.now());
            
            ResourceRating savedRating = ratingRepository.save(newRating);
            updateResourceRatingStatistics(resourceId);
            return savedRating;
        }
    }
}
