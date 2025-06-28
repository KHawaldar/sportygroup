package com.sportygroup.ticketing.model;

import java.time.Instant;
import java.util.UUID;

public record Ticket(UUID ticketId,
                     String userId,
                     String subject,
                     String description,
                     TicketStatus status,
                     String assigneeId,
                     Instant createdAt,
                     Instant updatedAt) {

    public Ticket withStatus(TicketStatus status){
        return new Ticket(ticketId, userId, subject, description, status,assigneeId, createdAt, updatedAt);
    }
    public Ticket withUpdatedAt(Instant now){
        return new Ticket(ticketId, userId, subject, description, status,assigneeId, createdAt, now);
    }
    public Ticket withAssignedId(String assignedId){
        return new Ticket(ticketId, userId, subject, description, status,assignedId , createdAt, updatedAt);

    }

}
