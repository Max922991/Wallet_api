package com.example.wallet_api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class WalletBalanceResponse {
    private UUID walletId;
    private Long balance;
}
