package com.sportygroup.ticketing.exception;

import java.util.UUID;

public class TicketAlreadyAssignedException extends RuntimeException{
    public TicketAlreadyAssignedException(UUID ticketID){
        super("ticket already assigned"+ticketID);
    }
}
