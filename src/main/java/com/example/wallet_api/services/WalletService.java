package com.example.wallet_api.services;

import com.example.wallet_api.exceptions.exception.InsufficientFundsException;
import com.example.wallet_api.exceptions.exception.InvalidOperationException;
import com.example.wallet_api.exceptions.exception.WalletNotFoundException;
import com.example.wallet_api.models.Wallet;
import com.example.wallet_api.models.WalletDto;
import com.example.wallet_api.repositories.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@EnableRetry
@RequiredArgsConstructor
public class WalletService {

    private static final int MAX_RETRIES = 5;
    private final WalletRepository walletRepository;

    @Transactional
    @Retryable(
            maxAttempts = MAX_RETRIES,
            backoff = @Backoff(delay = 100, multiplier = 2))
    public void performOperation(WalletDto walletDto) {

        Wallet wallet = walletRepository.findById(walletDto.getWalletId())
                .orElseThrow(WalletNotFoundException::new);

        if (wallet.getTransactionId() != null) {
            log.info("Operation with transaction ID {} has already been performed.", wallet.getTransactionId());
            return;
        }

        switch (walletDto.getOperationType()) {
            case DEPOSIT -> deposit(wallet, walletDto.getAmount());
            case WITHDRAW -> withdraw(wallet, walletDto.getAmount());
            default -> throw new InvalidOperationException();
        }

        wallet.setTransactionId(UUID.randomUUID());

        walletRepository.saveAndFlush(wallet);

        log.info("Operation completed successfully for walletId {}", walletDto.getWalletId());
    }

    private void deposit(Wallet wallet, Long amount) {
        wallet.setBalance(wallet.getBalance() + amount);
    }

    private void withdraw(Wallet wallet, Long amount) {
        if (wallet.getBalance() < amount) {
            throw new InsufficientFundsException();
        }
        wallet.setBalance(wallet.getBalance() - amount);
    }

    @Transactional(readOnly = true)
    public Long getBalance(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(WalletNotFoundException::new);
        return wallet.getBalance();
    }
}
   
