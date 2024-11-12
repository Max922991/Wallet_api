package com.example.wallet_api.models;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WalletDto {

    @Column(name = "wallet_id", nullable = false)
    private UUID walletId;

    @Column(name = "balance", nullable = false)
    @NotNull(message = "Amount cannot be null")
    private Long amount;

    @Column(name = "operation_type", nullable = false)
    @NotNull(message = "Operation type cannot be null")
    private OperationType operationType;
}