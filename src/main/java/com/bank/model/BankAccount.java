package com.bank.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * BankAccount Class
 * 
 * Demonstrates:
 * 1. ENCAPSULATION: All internal fields are private. State changes only occur
 *    through controlled public methods with business validation.
 * 2. ABSTRACTION: Implements the AccountOperations interface.
 * 3. Base class for INHERITANCE and POLYMORPHISM.
 */
public class BankAccount implements AccountOperations {

    // Default daily withdrawal limit is ₹10,000.00
    public static final BigDecimal DEFAULT_DAILY_LIMIT = new BigDecimal("10000.00");

    // Private fields enforcing Encapsulation
    private final String accountNumber;
    private String accountHolderName;
    private BigDecimal balance;
    private BigDecimal dailyWithdrawalLimit;
    private BigDecimal amountWithdrawnToday;
    private LocalDate lastWithdrawalDate;

    /**
     * Parameterized Constructor to initialize account state safely.
     *
     * @param accountNumber     Unique account number
     * @param accountHolderName Account holder's full name
     * @param initialDeposit    Initial deposit amount (must be >= 500)
     */
    public BankAccount(String accountNumber, String accountHolderName, BigDecimal initialDeposit) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty.");
        }
        if (accountHolderName == null || accountHolderName.trim().isEmpty()) {
            throw new IllegalArgumentException("Account holder name cannot be empty.");
        }
        if (initialDeposit == null || initialDeposit.compareTo(new BigDecimal("500.00")) < 0) {
            throw new IllegalArgumentException("Initial deposit must be at least \u20B9500.");
        }

        this.accountNumber = accountNumber.trim();
        this.accountHolderName = accountHolderName.trim();
        this.balance = initialDeposit.setScale(2, RoundingMode.HALF_UP);
        this.dailyWithdrawalLimit = DEFAULT_DAILY_LIMIT.setScale(2, RoundingMode.HALF_UP);
        this.amountWithdrawnToday = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        this.lastWithdrawalDate = LocalDate.now();
    }

    /**
     * Deposit Money into the account.
     * Validates that deposit amount is strictly greater than 0.
     *
     * @param amount Deposit amount
     */
    @Override
    public synchronized void deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than \u20B90.");
        }
        this.balance = this.balance.add(amount.setScale(2, RoundingMode.HALF_UP));
    }

    /**
     * Withdraw Money from the account.
     * Validates positive amount, sufficient balance, and daily withdrawal limit.
     *
     * @param amount Withdrawal amount
     */
    @Override
    public synchronized void withdraw(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than \u20B90.");
        }

        BigDecimal scaledAmount = amount.setScale(2, RoundingMode.HALF_UP);

        // Check 1: Insufficient Balance Check
        if (scaledAmount.compareTo(this.balance) > 0) {
            throw new IllegalArgumentException("Insufficient balance.");
        }

        // Check 2: Check & update Daily Withdrawal Limit for current calendar date
        checkAndResetDailyLimit(LocalDate.now());

        BigDecimal remainingLimit = getRemainingDailyLimit();
        if (scaledAmount.compareTo(remainingLimit) > 0) {
            throw new IllegalArgumentException("Daily withdrawal limit exceeded.");
        }

        // Deduct from balance and track daily withdrawal
        this.balance = this.balance.subtract(scaledAmount);
        this.amountWithdrawnToday = this.amountWithdrawnToday.add(scaledAmount);
        this.lastWithdrawalDate = LocalDate.now();
    }

    /**
     * Resets the daily withdrawal counter if the calendar date has changed.
     * Does NOT reset on page reloads, browser refreshes, or navigation.
     *
     * @param currentDate Current calendar date
     */
    public synchronized void checkAndResetDailyLimit(LocalDate currentDate) {
        if (currentDate == null) {
            currentDate = LocalDate.now();
        }
        if (this.lastWithdrawalDate == null || !this.lastWithdrawalDate.isEqual(currentDate)) {
            this.amountWithdrawnToday = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            this.lastWithdrawalDate = currentDate;
        }
    }

    /**
     * Calculates the remaining withdrawal limit for today.
     *
     * @return remaining daily limit (Daily Limit - Amount Withdrawn Today)
     */
    @Override
    public synchronized BigDecimal getRemainingDailyLimit() {
        checkAndResetDailyLimit(LocalDate.now());
        BigDecimal remaining = this.dailyWithdrawalLimit.subtract(this.amountWithdrawnToday);
        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return remaining.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getBalance() {
        return this.balance;
    }

    @Override
    public String getAccountType() {
        return "Standard Account";
    }

    // Getters demonstrating Encapsulation (no raw setters for sensitive fields)
    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        if (accountHolderName == null || accountHolderName.trim().isEmpty()) {
            throw new IllegalArgumentException("Account holder name cannot be empty.");
        }
        this.accountHolderName = accountHolderName.trim();
    }

    public BigDecimal getDailyWithdrawalLimit() {
        return dailyWithdrawalLimit;
    }

    public BigDecimal getAmountWithdrawnToday() {
        checkAndResetDailyLimit(LocalDate.now());
        return amountWithdrawnToday;
    }

    public LocalDate getLastWithdrawalDate() {
        return lastWithdrawalDate;
    }
}
