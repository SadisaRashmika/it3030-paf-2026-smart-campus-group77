package com.it3030.smartcampus.member1.controller;

import com.it3030.smartcampus.member1.model.ResourceRating;
import com.it3030.smartcampus.member1.service.ResourceRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/member1/ratings")
public class ResourceRatingController {

    @Autowired
    private ResourceRatingService ratingService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceRating> getAllRatings() {
        return ratingService.getAllRatings();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResourceRating> getRatingById(@PathVariable Long id) {
        Optional<ResourceRating> rating = ratingService.getRatingById(id);
        return rating.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/resource/{resourceId}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceRating> getRatingsByResourceId(@PathVariable Long resourceId) {
        return ratingService.getRatingsByResourceId(resourceId);
    }

    @GetMapping("/resource/{resourceId}/paginated")
    @PreAuthorize("isAuthenticated()")
    public Page<ResourceRating> getRatingsByResourcePaginated(
            @PathVariable Long resourceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        // We need to get the resource first, then get paginated ratings
        // This is a simplified approach - in production, you'd want to optimize this
        List<ResourceRating> allRatings = ratingService.getRatingsByResourceId(resourceId);
        Pageable pageable = PageRequest.of(page, size);
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allRatings.size());
        
        if (start >= allRatings.size()) {
            return Page.empty(pageable);
        }
        
        List<ResourceRating> pageContent = allRatings.subList(start, end);
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, allRatings.size());
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createRating(@RequestBody ResourceRating rating) {
        try {
            ResourceRating createdRating = ratingService.createRating(rating);
            return ResponseEntity.ok(createdRating);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateRating(@PathVariable Long id, @RequestBody ResourceRating ratingDetails) {
        try {
            ResourceRating updatedRating = ratingService.updateRating(id, ratingDetails);
            return ResponseEntity.ok(updatedRating);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteRating(@PathVariable Long id) {
        try {
            ratingService.deleteRating(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}/resource/{resourceId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResourceRating> getUserRatingForResource(@PathVariable Long resourceId, @PathVariable String userId) {
        Optional<ResourceRating> rating = ratingService.getUserRatingForResource(resourceId, userId);
        return rating.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/resource/{resourceId}/average")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Double> getAverageRatingForResource(@PathVariable Long resourceId) {
        Double averageRating = ratingService.getAverageRatingForResource(resourceId);
        return ResponseEntity.ok(averageRating != null ? averageRating : 0.0);
    }

    @GetMapping("/resource/{resourceId}/count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> getRatingCountForResource(@PathVariable Long resourceId) {
        Long count = ratingService.getRatingCountForResource(resourceId);
        return ResponseEntity.ok(count != null ? count : 0L);
    }

    @GetMapping("/reviews")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceRating> getReviewsWithText() {
        return ratingService.getReviewsWithText();
    }

    @GetMapping("/resource/{resourceId}/reviews")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceRating> getReviewsWithTextByResourceId(@PathVariable Long resourceId) {
        return ratingService.getReviewsWithTextByResourceId(resourceId);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceRating> getRatingsByUserId(@PathVariable String userId) {
        return ratingService.getRatingsByUserId(userId);
    }

    @GetMapping("/high-ratings/{minRating}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceRating> getHighRatings(@PathVariable Integer minRating) {
        return ratingService.getHighRatings(minRating);
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<Object> getRatingStatistics() {
        long positiveCount = ratingService.getPositiveRatingCount();
        long negativeCount = ratingService.getNegativeRatingCount();
        
        return ResponseEntity.ok(Map.of(
            "positive", positiveCount,
            "negative", negativeCount,
            "total", positiveCount + negativeCount
        ));
    }

    @PostMapping("/update-or-create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateOrCreateRating(@RequestBody Map<String, Object> request) {
        try {
            Long resourceId = Long.valueOf(request.get("resourceId").toString());
            String userId = request.get("userId").toString();
            Integer rating = Integer.valueOf(request.get("rating").toString());
            String reviewText = request.get("reviewText") != null ? request.get("reviewText").toString() : null;
            
            ResourceRating savedRating = ratingService.updateOrCreateRating(resourceId, userId, rating, reviewText);
            return ResponseEntity.ok(savedRating);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
