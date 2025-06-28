package com.sportygroup.ticketing.unit;

import com.sportygroup.ticketing.model.Ticket;
import com.sportygroup.ticketing.model.TicketRequest;
import com.sportygroup.ticketing.service.TicketService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class TicketConcurrencyTest {

    @Autowired
    private TicketService service;
    @Autowired
    private  RedissonClient redissonClient;

    @Container
    @ServiceConnection
     static GenericContainer<?> redis = new GenericContainer<>("redis:7.2.4").withExposedPorts(6379);



    @Test
    void concurrentAssignmentOnlyOneSucceeds() throws Exception {
        TicketRequest ticketRequest =
                new TicketRequest("user-001", "subject", "desc");
        Ticket ticket = service.createTicket(ticketRequest);
        UUID ticketId = ticket.ticketId();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        List<Future<String>> results = new ArrayList<>();
        results.add(executor.submit(() -> {
            try {
                return service.assignAgent(ticketId, "agent-3").assigneeId();
            } catch (Exception e) {
                return null;
            }
        }));
        results.add(executor.submit(() -> {
            try {
                return service.assignAgent(ticketId, "agent-2").assigneeId();
            } catch (Exception e) {
                return null;
            }
        }));

        Set<String> assignees = new HashSet<>();
        for (Future<String> future : results) {
            String assignee = future.get();
           if (assignee != null) assignees.add(assignee);
        }
        // Only one agent should be assigned
        assertTrue( assignees.size() == 1);

    }

    @Test
    void lockIsReleasedAfterSimulatedCrashByTTL() throws Exception {
        String lockKey = "ticket:crash-test";
        long leaseTime = 5; // seconds

        // Simulate process 1 acquiring the lock and "crashing" (not releasing)
        RLock lock1 = redissonClient.getLock(lockKey);
        boolean acquired1 = lock1.tryLock(1, 5, TimeUnit.SECONDS);
        assertTrue(acquired1);

        // Simulate another process/thread
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> result = executor.submit(() -> {
            RLock lock2 = redissonClient.getLock(lockKey);
            // Try to acquire the lock while lock1 is holding it
            return lock2.tryLock(1, 1, TimeUnit.SECONDS); // waitTime = 1s, leaseTime = 1s
        });

        boolean acquired2 = result.get();
        assertFalse(acquired2); // Should be false, lock is still held by lock1

        // Wait for the TTL (lease time) to expire
        Thread.sleep((leaseTime + 1) * 1000);

        // Now process 2 should be able to acquire the lock
        RLock lock3 = redissonClient.getLock(lockKey);
        boolean acquired3 = lock3.tryLock(1, leaseTime, TimeUnit.SECONDS);
        assertTrue(acquired3);

        // Clean up
        lock3.unlock();
    }

}
