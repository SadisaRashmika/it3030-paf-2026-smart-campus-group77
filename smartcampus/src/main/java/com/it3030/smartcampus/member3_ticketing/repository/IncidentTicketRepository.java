package com.it3030.smartcampus.member3_ticketing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.it3030.smartcampus.member3_ticketing.model.IncidentTicket;
import com.it3030.smartcampus.member3_ticketing.model.TicketStatus;

@Repository
public interface IncidentTicketRepository extends JpaRepository<IncidentTicket, Long> {

    List<IncidentTicket> findByReporterEmailOrderByCreatedAtDesc(String reporterEmail);

    List<IncidentTicket> findByAssignedTechnicianEmailOrderByCreatedAtDesc(String technicianEmail);

    List<IncidentTicket> findByStatusOrderByCreatedAtDesc(TicketStatus status);

    List<IncidentTicket> findAllByOrderByCreatedAtDesc();
}
