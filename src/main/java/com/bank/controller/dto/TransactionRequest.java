package com.bank.controller.dto;

import java.math.BigDecimal;

/**
 * Request payload for Deposit and Withdraw monetary transactions.
 */
public class TransactionRequest {

    private String accountNumber;
    private BigDecimal amount;

    public TransactionRequest() {
    }

    public TransactionRequest(String accountNumber, BigDecimal amount) {
        this.accountNumber = accountNumber;
        this.amount = amount;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
