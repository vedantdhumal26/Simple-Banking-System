package com.bank.service;

import com.bank.model.Bank;
import com.bank.model.BankAccount;
import com.bank.model.SavingsAccount;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * BankService Class
 * 
 * Demonstrates:
 * 1. SEPARATION OF CONCERNS: Business logic is decoupled from controllers
 *    and data storage.
 * 2. POLYMORPHISM: Manages BankAccount references pointing to SavingsAccount instances.
 */
@Service
public class BankService {

    private final Bank bank;

    public BankService() {
        this.bank = new Bank();
    }

    // Constructor for testing or dependency injection
    public BankService(Bank bank) {
        this.bank = bank != null ? bank : new Bank();
    }

    /**
     * Creates a new bank account.
     * Validates account holder name and minimum initial deposit (>= ₹500).
     *
     * @param accountHolderName Name of account holder
     * @param initialDeposit    Initial deposit amount
     * @return Newly created BankAccount
     */
    public BankAccount createAccount(String accountHolderName, BigDecimal initialDeposit) {
        if (accountHolderName == null || accountHolderName.trim().isEmpty()) {
            throw new IllegalArgumentException("Account holder name cannot be empty.");
        }
        if (initialDeposit == null || initialDeposit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Initial deposit cannot be negative or zero.");
        }
        if (initialDeposit.compareTo(new BigDecimal("500.00")) < 0) {
            throw new IllegalArgumentException("Initial deposit must be at least \u20B9500.");
        }

        String accountNumber = bank.generateNextAccountNumber();

        // Demonstrating POLYMORPHISM: A BankAccount reference refers to a SavingsAccount object
        BankAccount newAccount = new SavingsAccount(accountNumber, accountHolderName, initialDeposit);
        bank.addAccount(newAccount);

        return newAccount;
    }

    /**
     * Checks the balance for an existing account.
     *
     * @param accountNumber Account number
     * @return Current balance as BigDecimal
     */
    public BigDecimal getBalance(String accountNumber) {
        BankAccount account = getAccountOrThrow(accountNumber);
        return account.getBalance();
    }

    /**
     * Deposits money into an existing account.
     *
     * @param accountNumber Account number
     * @param amount        Deposit amount
     * @return Updated balance
     */
    public BigDecimal deposit(String accountNumber, BigDecimal amount) {
        BankAccount account = getAccountOrThrow(accountNumber);

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than \u20B90.");
        }

        account.deposit(amount);
        return account.getBalance();
    }

    /**
     * Withdraws money from an existing account following multi-step validation.
     *
     * Step 1: Check account existence.
     * Step 2: Validate amount > 0.
     * Step 3: Check available balance (Insufficient balance).
     * Step 4: Check daily withdrawal limit (Limit ₹10,000).
     * Step 5: Perform deduction and limit update.
     *
     * @param accountNumber Account number
     * @param amount        Withdrawal amount
     * @return Updated balance
     */
    public BigDecimal withdraw(String accountNumber, BigDecimal amount) {
        // Step 1: Check whether account exists
        BankAccount account = getAccountOrThrow(accountNumber);

        // Step 2: Validate withdrawal amount > 0
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than \u20B90.");
        }

        BigDecimal scaledAmount = amount.setScale(2, RoundingMode.HALF_UP);

        // Step 3: Check available balance
        if (scaledAmount.compareTo(account.getBalance()) > 0) {
            throw new IllegalArgumentException("Insufficient balance.");
        }

        // Step 4: Check daily withdrawal limit
        account.checkAndResetDailyLimit(LocalDate.now());
        BigDecimal remainingLimit = account.getRemainingDailyLimit();
        if (scaledAmount.compareTo(remainingLimit) > 0) {
            throw new IllegalArgumentException("Daily withdrawal limit exceeded.");
        }

        // Step 5: Deduct amount
        account.withdraw(scaledAmount);
        return account.getBalance();
    }

    /**
     * Retrieves full account information.
     *
     * @param accountNumber Account number
     * @return BankAccount object
     */
    public BankAccount getAccountInformation(String accountNumber) {
        BankAccount account = getAccountOrThrow(accountNumber);
        account.checkAndResetDailyLimit(LocalDate.now());
        return account;
    }

    /**
     * Retrieves all accounts in the bank.
     *
     * @return list of all accounts
     */
    public List<BankAccount> getAllAccounts() {
        return bank.getAllAccounts();
    }

    /**
     * Helper method to find an account or throw an "Account not found." exception.
     *
     * @param accountNumber Account number
     * @return BankAccount
     */
    public BankAccount getAccountOrThrow(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account not found.");
        }
        BankAccount account = bank.findAccount(accountNumber.trim());
        if (account == null) {
            throw new IllegalArgumentException("Account not found.");
        }
        return account;
    }
}
