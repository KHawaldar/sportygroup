package com.sportygroup.ticketing.repository;

import com.sportygroup.ticketing.model.Ticket;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TicketRepository {

    private final Map<UUID, Ticket> store = new ConcurrentHashMap<>();

    public Ticket save(Ticket ticket) {
        store.put(ticket.ticketId(), ticket);
        return ticket;
    }

    public Optional<Ticket> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }
}

