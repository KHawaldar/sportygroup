package com.sportygroup.ticketing.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TicketStatus {
    OPEN("open"),
    IN_PROGRESS("in_progress"),
    RESOLVED("resolved"),
    CLOSED("closed");
    private final String value;

    TicketStatus(String value){
        this.value = value;
    }
    @JsonValue
    public String getValue(){
        return value;
    }


}
