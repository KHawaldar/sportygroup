package com.sportygroup.ticketing.controller;

import com.sportygroup.ticketing.model.Ticket;
import com.sportygroup.ticketing.model.TicketRequest;
import com.sportygroup.ticketing.model.TicketStatus;
import com.sportygroup.ticketing.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
         this.ticketService = ticketService;
    }
    @Operation(description = "create a new support ticket")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ticket created"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<Ticket> createTicket(@Valid  @RequestBody TicketRequest request){

        Ticket ticket = ticketService.createTicket(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(ticket.ticketId())
                .toUri();

        return ResponseEntity.created(location).body(ticket);

    }
    @Operation(description = "update the ticket status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ticket updated."),
            @ApiResponse(responseCode = "404", description = "Ticket is not found."),
            @ApiResponse(responseCode = "423", description = "Ticket is locked by another operation.")
    })
    @PatchMapping("/{ticketId}/status")
    public ResponseEntity<Ticket> updateStatus(@PathVariable UUID ticketId, @RequestBody StatusUpdateRequest statusUpdate) {
        Ticket updated = ticketService.updateStatus(ticketId, statusUpdate.status());
        return ResponseEntity.ok(updated);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ticket assigned"),
            @ApiResponse(responseCode = "404", description = "Ticket is not found."),
            @ApiResponse(responseCode = "409", description = "Ticket is already assigned."),
            @ApiResponse(responseCode = "423", description = "Ticket is locked by another operation.")
    })

    @PatchMapping("/{ticketId}/assign")
    public ResponseEntity<Ticket> assignTicket(@PathVariable UUID ticketId, @RequestBody AssignRequest assignRequest) {
        Ticket updated = ticketService.assignAgent(ticketId, assignRequest.assigneeId());
        return ResponseEntity.ok(updated);
    }
    public static record StatusUpdateRequest(TicketStatus status) {}

    public static record AssignRequest(String assigneeId) {}


}
