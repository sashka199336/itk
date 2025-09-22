package com.example.itk.controller;

import com.example.itk.model.Wallet;
import com.example.itk.service.WalletService;
import com.example.itk.dto.WalletChangeRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
public class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WalletService walletService;

    private UUID walletId;
    private Wallet wallet;

    @BeforeEach
    void setup() {
        walletId = UUID.randomUUID();
        wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(1000L);


        Mockito.when(walletService.getWallet(eq(walletId))).thenReturn(wallet);
    }


    @Test
    void testChangeWalletDeposit() throws Exception {
        WalletChangeRequest request = new WalletChangeRequest();
        request.setWalletId(walletId);
        request.setOperationType("DEPOSIT");
        request.setAmount(200L);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("Операция успешно выполнена"));
    }


    @Test
    void testChangeWalletWithdrawInsufficientFunds() throws Exception {
        WalletChangeRequest request = new WalletChangeRequest();
        request.setWalletId(walletId);
        request.setOperationType("WITHDRAW");
        request.setAmount(2000L);

        Mockito.doThrow(new IllegalArgumentException("Недостаточно средств"))
                .when(walletService)
                .changeBalance(eq(walletId), eq("WITHDRAW"), eq(2000L));

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.ошибка").value("Недостаточно средств"));
    }


    @Test
    void testGetWalletOk() throws Exception {
        mockMvc.perform(get("/api/v1/wallets/{id}", walletId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(walletId.toString()))
                .andExpect(jsonPath("$.balance").value(1000L));
    }


    @Test
    void testGetWalletNotFound() throws Exception {
        UUID notExist = UUID.randomUUID();
        Mockito.when(walletService.getWallet(eq(notExist)))
                .thenThrow(new IllegalArgumentException("Кошелёк не найден"));

        mockMvc.perform(get("/api/v1/wallets/{id}", notExist))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.ошибка").value("Кошелёк не найден"));
    }


    @Test
    void testGetAllWallets() throws Exception {
        Wallet another = new Wallet();
        another.setId(UUID.randomUUID());
        another.setBalance(200L);
        List<Wallet> wallets = Arrays.asList(wallet, another);
        Mockito.when(walletService.getAllWallets()).thenReturn(wallets);

        mockMvc.perform(get("/api/v1/wallets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].walletId").value(walletId.toString()))
                .andExpect(jsonPath("$[1].balance").value(200));
    }
}