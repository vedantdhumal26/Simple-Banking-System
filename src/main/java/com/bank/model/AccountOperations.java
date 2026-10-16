package com.bank.model;

import java.math.BigDecimal;

/**
 * AccountOperations Interface
 * 
 * Demonstrates ABSTRACTION:
 * This interface defines the contract and essential operations of a bank account,
 * hiding implementation details from the caller. Any concrete bank account
 * class must implement these methods.
 */
public interface AccountOperations {

    /**
     * Deposit a specific amount into the account.
     * @param amount the monetary amount to deposit (must be > 0)
     */
    void deposit(BigDecimal amount);

    /**
     * Withdraw a specific amount from the account after validating balance
     * and daily limits.
     * @param amount the monetary amount to withdraw (must be > 0)
     */
    void withdraw(BigDecimal amount);

    /**
     * Retrieve the current account balance.
     * @return current balance as BigDecimal
     */
    BigDecimal getBalance();

    /**
     * Retrieve the remaining withdrawal limit for today.
     * @return remaining daily limit as BigDecimal
     */
    BigDecimal getRemainingDailyLimit();

    /**
     * Retrieve the account type description.
     * @return account type string
     */
    String getAccountType();
}
