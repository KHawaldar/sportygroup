package com.sportygroup.ticketing.service;

import com.sportygroup.ticketing.exception.TicketAlreadyAssignedException;
import com.sportygroup.ticketing.exception.TicketNotFoundException;
import com.sportygroup.ticketing.model.Ticket;
import com.sportygroup.ticketing.model.TicketRequest;
import com.sportygroup.ticketing.model.TicketStatus;
import com.sportygroup.ticketing.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketRepository repository;
    private final LockService lockService;

    public TicketService(TicketRepository repository, LockService lockService) {
        this.repository = repository;
        this.lockService = lockService;
    }

    public Ticket createTicket(final TicketRequest request) {

        UUID ticketId = UUID.randomUUID();
        Instant now = Instant.now();
        Ticket ticket = new Ticket(
                ticketId,
                request.userId(),
                request.subject(),
                request.description(),
                TicketStatus.OPEN,
                null,
                now,
                now
        );
        return repository.save(ticket);
    }

    public Optional<Ticket> getTicket(UUID ticketId) {
        return repository.findById(ticketId);
    }

    public Ticket updateStatus(UUID ticketId, TicketStatus newStatus) {
       return lockService.executeWithLock("ticket:lock" + ticketId, () -> {
            Ticket oldTicket = repository.findById(ticketId)
                    .orElseThrow(() -> new TicketNotFoundException(ticketId));
           Instant now = Instant.now();
          Ticket updated =  oldTicket.withStatus(newStatus).withUpdatedAt(now);
          repository.save(updated);
           return updated;

        });
    }

    public Ticket assignAgent(UUID ticketId, String assigneeId) {
       return lockService.executeWithLock("ticket:lock" + ticketId, () -> {
            Ticket oldTicket = repository.findById(ticketId)
                    .orElseThrow(() -> new TicketNotFoundException(ticketId));
            if (oldTicket.assigneeId() != null) {
                throw new TicketAlreadyAssignedException(ticketId);
            }
            Ticket newTicket = oldTicket.withAssignedId(assigneeId).withUpdatedAt(Instant.now());
            repository.save(newTicket);
            return newTicket;
        });

    }
}

