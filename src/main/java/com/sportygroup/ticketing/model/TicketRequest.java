package com.sportygroup.ticketing.model;

import jakarta.validation.constraints.NotBlank;

public record TicketRequest(@NotBlank(message = "user id is blank") String userId,
                            @NotBlank(message = "subject is blank") String subject,
                            @NotBlank(message = "description is blank") String description) {
}
