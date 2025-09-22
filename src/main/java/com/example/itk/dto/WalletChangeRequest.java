package com.example.itk.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;

public class WalletChangeRequest {
    @NotNull(message = "ID кошелька обязателен")
    private UUID walletId;

    @NotNull(message = "Тип операции обязателен")
    @Pattern(regexp = "DEPOSIT|WITHDRAW", message = "Тип операции должен быть DEPOSIT или WITHDRAW")
    private String operationType;

    @NotNull(message = "Сумма операции обязательна")
    @Min(value = 1, message = "Сумма операции должна быть целым положительным числом")
    private Long amount;

    public WalletChangeRequest() {}

    public UUID getWalletId() {
        return walletId;
    }

    public void setWalletId(UUID walletId) {
        this.walletId = walletId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}