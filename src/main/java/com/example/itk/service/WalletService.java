package com.example.itk.service;

import com.example.itk.model.Wallet;
import com.example.itk.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class WalletService {
    private final WalletRepository repository;
    private final WalletLockService lockService;

    public WalletService(WalletRepository repository, WalletLockService lockService) {
        this.repository = repository;
        this.lockService = lockService;
    }


    public void changeBalance(UUID id, String operationType, Long amount) {
        lockService.withWalletLock(id, () -> changeBalanceLocked(id, operationType, amount));
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    protected void changeBalanceLocked(UUID id, String operationType, Long amount) {
        Wallet wallet = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Кошелек не найден"));

        switch (operationType) {
            case "DEPOSIT":
                wallet.setBalance(wallet.getBalance() + amount);
                break;
            case "WITHDRAW":
                if (wallet.getBalance() < amount)
                    throw new IllegalArgumentException("Недостаточно средств");
                wallet.setBalance(wallet.getBalance() - amount);
                break;
            default:
                throw new IllegalArgumentException("Неизвестный тип операции");
        }
        repository.save(wallet);
    }


    public Wallet getWallet(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Кошелек не найден"));
    }


    public List<Wallet> getAllWallets() {
        return repository.findAll();
    }
}