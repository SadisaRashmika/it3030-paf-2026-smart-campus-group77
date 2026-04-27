package com.it3030.smartcampus.member1.repository;

import com.it3030.smartcampus.member1.model.ResourceAnalytics;
import com.it3030.smartcampus.member1.model.EnhancedResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ResourceAnalyticsRepository extends JpaRepository<ResourceAnalytics, Long> {
    
    List<ResourceAnalytics> findByResource(EnhancedResource resource);
    
    List<ResourceAnalytics> findByResourceId(Long resourceId);
    
    @Query("SELECT a FROM ResourceAnalytics a WHERE a.dateRecorded BETWEEN :startDate AND :endDate ORDER BY a.dateRecorded DESC")
    List<ResourceAnalytics> findAnalyticsInDateRange(@Param("startDate") LocalDateTime startDate, 
                                                     @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT a FROM ResourceAnalytics a WHERE a.resource.id = :resourceId AND a.dateRecorded BETWEEN :startDate AND :endDate ORDER BY a.dateRecorded DESC")
    List<ResourceAnalytics> findResourceAnalyticsInDateRange(@Param("resourceId") Long resourceId, 
                                                            @Param("startDate") LocalDateTime startDate, 
                                                            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT AVG(a.totalBookings) FROM ResourceAnalytics a WHERE a.resource.id = :resourceId AND a.dateRecorded >= :date")
    Double findAverageBookingsForResource(@Param("resourceId") Long resourceId, @Param("date") LocalDateTime date);
    
    @Query("SELECT AVG(a.utilizationRate) FROM ResourceAnalytics a WHERE a.dateRecorded >= :date")
    Double findAverageUtilizationRate(@Param("date") LocalDateTime date);
    
    @Query("SELECT a FROM ResourceAnalytics a WHERE a.utilizationRate >= :rate ORDER BY a.utilizationRate DESC")
    List<ResourceAnalytics> findHighUtilizationResources(@Param("rate") Double rate);
    
    @Query("SELECT a FROM ResourceAnalytics a WHERE a.userSatisfactionScore >= :score ORDER BY a.userSatisfactionScore DESC")
    List<ResourceAnalytics> findHighSatisfactionResources(@Param("score") Double score);
    
    @Query("SELECT SUM(a.revenueGenerated) FROM ResourceAnalytics a WHERE a.dateRecorded BETWEEN :startDate AND :endDate")
    Double calculateTotalRevenue(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT a FROM ResourceAnalytics a ORDER BY a.totalBookings DESC")
    List<ResourceAnalytics> findMostBookedResources();
    
    @Query("SELECT a FROM ResourceAnalytics a ORDER BY a.userSatisfactionScore DESC")
    List<ResourceAnalytics> findHighestRatedResources();
}
