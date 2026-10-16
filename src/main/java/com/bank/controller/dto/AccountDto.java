package com.bank.controller.dto;

import com.bank.model.BankAccount;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object representing Account Information for presentation.
 */
public class AccountDto {

    private String accountNumber;
    private String accountHolderName;
    private String accountType;
    private BigDecimal balance;
    private BigDecimal dailyWithdrawalLimit;
    private BigDecimal amountWithdrawnToday;
    private BigDecimal remainingDailyLimit;
    private LocalDate lastWithdrawalDate;

    public AccountDto() {
    }

    public AccountDto(BankAccount account) {
        if (account != null) {
            this.accountNumber = account.getAccountNumber();
            this.accountHolderName = account.getAccountHolderName();
            this.accountType = account.getAccountType();
            this.balance = account.getBalance();
            this.dailyWithdrawalLimit = account.getDailyWithdrawalLimit();
            this.amountWithdrawnToday = account.getAmountWithdrawnToday();
            this.remainingDailyLimit = account.getRemainingDailyLimit();
            this.lastWithdrawalDate = account.getLastWithdrawalDate();
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getDailyWithdrawalLimit() {
        return dailyWithdrawalLimit;
    }

    public void setDailyWithdrawalLimit(BigDecimal dailyWithdrawalLimit) {
        this.dailyWithdrawalLimit = dailyWithdrawalLimit;
    }

    public BigDecimal getAmountWithdrawnToday() {
        return amountWithdrawnToday;
    }

    public void setAmountWithdrawnToday(BigDecimal amountWithdrawnToday) {
        this.amountWithdrawnToday = amountWithdrawnToday;
    }

    public BigDecimal getRemainingDailyLimit() {
        return remainingDailyLimit;
    }

    public void setRemainingDailyLimit(BigDecimal remainingDailyLimit) {
        this.remainingDailyLimit = remainingDailyLimit;
    }

    public LocalDate getLastWithdrawalDate() {
        return lastWithdrawalDate;
    }

    public void setLastWithdrawalDate(LocalDate lastWithdrawalDate) {
        this.lastWithdrawalDate = lastWithdrawalDate;
    }
}
