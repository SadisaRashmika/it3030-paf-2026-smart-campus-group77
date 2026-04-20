package com.it3030.smartcampus.member3_ticketing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.it3030.smartcampus.member3_ticketing.model.TicketComment;

@Repository
public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> {

    List<TicketComment> findByTicketIdOrderByCreatedAtAsc(Long ticketId);

    int countByTicketId(Long ticketId);

    void deleteByTicketId(Long ticketId);
}
