package com.it3030.smartcampus.member3_ticketing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.it3030.smartcampus.member3_ticketing.model.TicketAttachment;

@Repository
public interface TicketAttachmentRepository extends JpaRepository<TicketAttachment, Long> {

    List<TicketAttachment> findByTicketId(Long ticketId);

    int countByTicketId(Long ticketId);
}
