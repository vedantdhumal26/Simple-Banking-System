package com.bank.controller.dto;

import java.math.BigDecimal;

/**
 * Request payload for creating a new bank account.
 */
public class CreateAccountRequest {

    private String accountHolderName;
    private BigDecimal initialDeposit;

    public CreateAccountRequest() {
    }

    public CreateAccountRequest(String accountHolderName, BigDecimal initialDeposit) {
        this.accountHolderName = accountHolderName;
        this.initialDeposit = initialDeposit;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public BigDecimal getInitialDeposit() {
        return initialDeposit;
    }

    public void setInitialDeposit(BigDecimal initialDeposit) {
        this.initialDeposit = initialDeposit;
    }
}
