package com.example.itk.service;

import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WalletLockService {
    private final ConcurrentHashMap<UUID, Object> locks = new ConcurrentHashMap<>();

    public void withWalletLock(UUID walletId, Runnable operation) {
        Object lock = locks.computeIfAbsent(walletId, k -> new Object());
        synchronized (lock) {
            operation.run();
        }
    }
}