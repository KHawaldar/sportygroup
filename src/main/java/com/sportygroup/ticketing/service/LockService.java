package com.sportygroup.ticketing.service;

import com.sportygroup.ticketing.config.LockingProperties;
import com.sportygroup.ticketing.exception.LockAcquisitionException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Service
public class LockService {
    private final RedissonClient redissonClient;
    private final LockingProperties lockingProperties;

    public LockService(RedissonClient redissonClient, LockingProperties lockingProperties) {
        this.redissonClient = redissonClient;
        this.lockingProperties = lockingProperties;
    }

    public  <T> T executeWithLock(String key, Callable<T> criticalSection) {
        RLock lock = redissonClient.getLock(key);
        try {
            // Wait up to 5 seconds to acquire, auto-release after 10 seconds
            if (lock.tryLock(lockingProperties.waitTime(), lockingProperties.leaseTime(), TimeUnit.SECONDS)) {
                try {
                   return  criticalSection.call();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    lock.unlock();
                }
            } else {
                throw new LockAcquisitionException("Could not acquire lock for " + key);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Lock interrupted", e);
        }
    }
}
