package com.example.wallet_api;

import com.example.wallet_api.controllers.WalletController;
import com.example.wallet_api.exceptions.exception.InsufficientFundsException;
import com.example.wallet_api.exceptions.exception.InvalidOperationException;
import com.example.wallet_api.exceptions.exception.WalletNotFoundException;
import com.example.wallet_api.models.OperationType;
import com.example.wallet_api.models.WalletDto;
import com.example.wallet_api.services.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
public class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WalletService walletService;

    // Утилитный метод для преобразования объекта в JSON-строку
    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper()
                    .writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPerformOperation_SuccessfulDeposit() throws Exception {
        WalletDto request = new WalletDto();
        request.setWalletId(UUID.randomUUID());
        request.setOperationType(OperationType.DEPOSIT);
        request.setAmount(100L);

        doNothing().when(walletService).performOperation(any(WalletDto.class));

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void testPerformOperation_WalletNotFound() throws Exception {
        WalletDto request = new WalletDto();
        request.setWalletId(UUID.randomUUID());
        request.setOperationType(OperationType.WITHDRAW);
        request.setAmount(50L);

        doThrow(new WalletNotFoundException()).when(walletService).performOperation(any(WalletDto.class));

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Wallet not found"));
    }

    @Test
    public void testPerformOperation_InsufficientFunds() throws Exception {
        WalletDto request = new WalletDto();
        request.setWalletId(UUID.randomUUID());
        request.setOperationType(OperationType.WITHDRAW);
        request.setAmount(1000L);

        doThrow(new InsufficientFundsException()).when(walletService).performOperation(any(WalletDto.class));

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Insufficient funds in the wallet"));
    }

    @Test
    public void testPerformOperation_InvalidOperation() throws Exception {
        WalletDto request = new WalletDto();
        request.setWalletId(UUID.randomUUID());
        request.setOperationType(null);
        request.setAmount(100L);

        doThrow(new InvalidOperationException()).when(walletService).performOperation(any(WalletDto.class));

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Validation error"));
    }

    @Test
    public void testPerformOperation_ValidationError() throws Exception {
        WalletDto request = new WalletDto();

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Validation error"));
    }

    @Test
    public void testPerformOperation_InvalidJson() throws Exception {
        String invalidJson = "{ invalid json }";

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid JSON format"));
    }

    @Test
    public void testGetBalance_Successful() throws Exception {
        UUID walletId = UUID.randomUUID();
        Long balance = 500L;

        Mockito.when(walletService.getBalance(walletId)).thenReturn(balance);

        mockMvc.perform(get("/api/v1/wallets/{walletId}", walletId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(walletId.toString()))
                .andExpect(jsonPath("$.balance").value(balance));
    }

    @Test
    public void testGetBalance_WalletNotFound() throws Exception {
        UUID walletId = UUID.randomUUID();

        Mockito.when(walletService.getBalance(walletId)).thenThrow(
                new WalletNotFoundException());

        mockMvc.perform(get("/api/v1/wallets/{walletId}", walletId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Wallet not found"));
    }
}