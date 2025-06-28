package com.sportygroup.ticketing.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportygroup.ticketing.controller.TicketController;
import com.sportygroup.ticketing.model.Ticket;
import com.sportygroup.ticketing.model.TicketRequest;
import com.sportygroup.ticketing.model.TicketStatus;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.GenericContainer;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class TicketControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    //From the spring
    @ServiceConnection
   static GenericContainer<?> redis = new GenericContainer<>("redis:7.2.4").withExposedPorts(6379);


    @Test
    public void testCreateUpdateAndAssign() throws Exception {
        //create the ticket
        TicketRequest createRequest =
                new TicketRequest("kav001", "issue", "ticket is issued");
        String createRequestJson = objectMapper.writeValueAsString(createRequest);
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("kav001"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println("response" + response);


        //update the ticket status
        Ticket tkt = objectMapper.readValue(response, Ticket.class);
        TicketController.StatusUpdateRequest statusUpdateRequest =
                new TicketController.StatusUpdateRequest(TicketStatus.IN_PROGRESS);
        String statusUpdateJson = objectMapper.writeValueAsString(statusUpdateRequest);
        String result = mockMvc.perform(MockMvcRequestBuilders.patch("/tickets/{ticketId}/status", tkt.ticketId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(statusUpdateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("in_progress"))
                .andReturn()
                .getResponse()
                .getContentAsString();

       //assign the ticket to agent
        TicketController.AssignRequest assignRequest = new TicketController.AssignRequest("agent001");
        String assignRequestJson = objectMapper.writeValueAsString(assignRequest);
    mockMvc.perform(MockMvcRequestBuilders.patch("/tickets/{ticketId}/assign", tkt.ticketId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(assignRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assigneeId").value("agent001"));

    }

       @Test
       public void whenUSerIdIs_Empty_thenReturnBadRequest() throws Exception{
          TicketRequest ticketRequest =
                   new TicketRequest(null, "issue", "testing the bad request");
           String ticketRequestJson = objectMapper.writeValueAsString(ticketRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/tickets")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(ticketRequestJson))
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.userId").value("user id is blank"));

        }
        @Test
        public void whenUpdating_ticketNotFound() throws Exception{
        TicketController.StatusUpdateRequest statusUpdateRequest = new TicketController.StatusUpdateRequest(TicketStatus.IN_PROGRESS);
        String statusUpdateRqInJson = objectMapper.writeValueAsString(statusUpdateRequest);
        UUID uuid = UUID.randomUUID();
        mockMvc.perform(MockMvcRequestBuilders.patch("/tickets/{ticketId}/status", uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusUpdateRqInJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statuscode").value("404"))
                .andExpect(jsonPath("$.message").value("Ticket not found "+uuid));

        }
}
