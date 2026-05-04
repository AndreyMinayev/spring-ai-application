package org.training.springai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.training.springai.entity.HelpDeskTicket;

import java.util.List;

public interface HelpDeskTicketRepository extends JpaRepository<HelpDeskTicket, Long> {
    List<HelpDeskTicket> findByUsername(String username);
}
