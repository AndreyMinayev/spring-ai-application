package org.training.springai.tools;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.training.springai.dto.TicketRequest;
import org.training.springai.entity.HelpDeskTicket;
import org.training.springai.service.HelpDeskTicketService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HelpDeskTools {

    private final HelpDeskTicketService helpDeskTicketService;

    @Tool(name = "createTicket", description = "Create the Support Ticket")
    String createTicket(@ToolParam(description = "Details to create a Support ticket")
                        TicketRequest ticketRequest, ToolContext toolContext) {
        String username = toolContext.getContext().get("username").toString();
        HelpDeskTicket savedTicket =  helpDeskTicketService.createTicket(ticketRequest, username);
        log.info("Ticket created successfully. Ticket ID: {}, Username: {}", savedTicket.getId(), savedTicket.getUsername());
        return "Ticker #" + savedTicket.getId() +  " created successfully for user " + savedTicket.getUsername();
    }

    @Tool(description = "Fetch the status of the tickets based on a given username")
    List<HelpDeskTicket> getTicketStatus(ToolContext toolContext) {
        String username = (String) toolContext.getContext().get("username");
        log.info("Fetching tickets for user: {}", username);
        List<HelpDeskTicket> tickets = helpDeskTicketService.getTicketsByUsername(username);
        log.info("Found {} tickets for user: {}", tickets.size(), username);
        return tickets;
    }
}
