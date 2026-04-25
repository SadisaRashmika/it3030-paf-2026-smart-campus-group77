package com.it3030.smartcampus.member2.repository;

import com.it3030.smartcampus.member2.model.ResourceManagement;
import com.it3030.smartcampus.member2.model.ResourceStatus;
import com.it3030.smartcampus.member4.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ResourceManagementRepository extends JpaRepository<ResourceManagement, Long> {

    @Query("SELECT r FROM ResourceManagement r LEFT JOIN FETCH r.resource LEFT JOIN FETCH r.user WHERE r.user = :user ORDER BY r.startTime DESC")
    List<ResourceManagement> findByUserOrderByStartTimeDesc(@Param("user") UserAccount user);

    @Query("SELECT r FROM ResourceManagement r LEFT JOIN FETCH r.resource LEFT JOIN FETCH r.user WHERE r.status = :status ORDER BY r.startTime ASC")
    List<ResourceManagement> findByStatusOrderByStartTimeAsc(
            @Param("status") ResourceStatus status);

    @Query("SELECT r FROM ResourceManagement r WHERE r.resource.id = :resourceId " +
            "AND r.status = 'APPROVED' " +
            "AND r.startTime < :endTime " +
            "AND r.endTime > :startTime")
    List<ResourceManagement> findOverlappingApprovedResources(
            @Param("resourceId") Long resourceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Query("SELECT r FROM ResourceManagement r LEFT JOIN FETCH r.resource LEFT JOIN FETCH r.user " +
            "WHERE r.status = 'APPROVED' " +
            "AND r.startTime < :weekEnd " +
            "AND r.endTime > :weekStart " +
            "ORDER BY r.startTime ASC")
    List<ResourceManagement> findAllApprovedInWeek(
            @Param("weekStart") LocalDateTime weekStart,
            @Param("weekEnd") LocalDateTime weekEnd);
}
