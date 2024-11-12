package com.example.wallet_api.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "wallet")
public class Wallet {

    @Id
    @Column(name = "wallet_id", nullable = false)
    UUID walletId;

    @Column(name = "balance", nullable = false)
    Long balance;

    @Transient
    @Column(name = "operation_type", nullable = false)
    OperationType operationType;

    @Column(name = "transaction_id", unique = true)
    UUID transactionId;

    public Wallet(UUID walletId, Long balance) {
        this.walletId = walletId;
        this.balance = balance;
    }
}