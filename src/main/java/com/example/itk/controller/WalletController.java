package com.example.itk.controller;

import com.example.itk.model.Wallet;
import com.example.itk.service.WalletService;
import com.example.itk.dto.WalletChangeRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

import jakarta.validation.Valid;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Validated
@Tag(name = "Wallet API", description = "Операции с кошельками")
public class WalletController {
    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @Operation(
            summary = "Изменить баланс кошелька (депозит/вывод)",
            description = "Изменяет баланс кошелька на указанную сумму. operationType: DEPOSIT или WITHDRAW."
    )
    @PostMapping("/wallet")
    public ResponseEntity<?> changeWallet(
            @Valid @RequestBody WalletChangeRequest request) {
        try {
            walletService.changeBalance(
                    request.getWalletId(),
                    request.getOperationType(),
                    request.getAmount()
            );
            return ResponseEntity.ok(Map.of("result", "Операция успешно выполнена"));
        } catch (IllegalArgumentException ex) {

            return ResponseEntity.badRequest().body(Map.of("ошибка", ex.getMessage()));
        } catch (Exception ex) {

            return ResponseEntity.status(500).body(Map.of("ошибка", "Внутренняя ошибка сервера"));
        }
    }

    @Operation(
            summary = "Получить баланс конкретного кошелька",
            description = "Передайте UUID кошелька, чтобы узнать баланс."
    )
    @GetMapping("/wallets/{id}")
    public ResponseEntity<?> getWallet(
            @Parameter(description="UUID кошелька", required=true)
            @PathVariable("id") UUID id) {
        try {
            Wallet wallet = walletService.getWallet(id);
            return ResponseEntity.ok(Map.of(
                    "walletId", wallet.getId(),
                    "balance", wallet.getBalance()
            ));
        } catch (IllegalArgumentException ex) {
            // Кошелёк не найден
            return ResponseEntity.status(404).body(Map.of("ошибка", "Кошелёк не найден"));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("ошибка", "Внутренняя ошибка сервера"));
        }
    }

    @Operation(
            summary = "Получить список всех кошельков",
            description = "Возвращает список всех кошельков с текущим балансом."
    )
    @GetMapping("/wallets")
    public ResponseEntity<List<Map<String, Object>>> getAllWallets() {
        List<Wallet> wallets = walletService.getAllWallets();
        List<Map<String, Object>> result = wallets.stream()
                .map(wallet -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("walletId", wallet.getId());
                    map.put("balance", wallet.getBalance());
                    return map;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}