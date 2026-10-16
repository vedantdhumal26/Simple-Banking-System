package com.bank.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Bank Class
 * 
 * Demonstrates:
 * 1. OBJECT COMPOSITION & COLLECTIONS: Manages a collection of BankAccount objects
 *    using Map<String, BankAccount> keyed by unique account numbers.
 * 2. DATA INTEGRITY: Prevents duplicate account numbers and generates sequential
 *    unique account numbers (100001, 100002, ...).
 */
public class Bank {

    // Bank account store using thread-safe ConcurrentHashMap
    private final Map<String, BankAccount> accounts;

    // Counter to generate unique sequential 6-digit account numbers starting from 100001
    private final AtomicInteger accountNumberGenerator;

    /**
     * Default Constructor initializing the account storage.
     */
    public Bank() {
        this.accounts = new ConcurrentHashMap<>();
        this.accountNumberGenerator = new AtomicInteger(100001);
    }

    /**
     * Generates a guaranteed unique account number.
     *
     * @return unique account number string
     */
    public synchronized String generateNextAccountNumber() {
        String newAccNum;
        do {
            newAccNum = String.valueOf(accountNumberGenerator.getAndIncrement());
        } while (accounts.containsKey(newAccNum));
        return newAccNum;
    }

    /**
     * Adds a new bank account to the bank.
     * Prevents duplicate account numbers.
     *
     * @param account BankAccount object to add
     */
    public void addAccount(BankAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null.");
        }
        if (accounts.containsKey(account.getAccountNumber())) {
            throw new IllegalArgumentException("Account number " + account.getAccountNumber() + " already exists.");
        }
        accounts.put(account.getAccountNumber(), account);
    }

    /**
     * Finds an account by account number.
     *
     * @param accountNumber Account number to look up
     * @return BankAccount if found, or null if not found
     */
    public BankAccount findAccount(String accountNumber) {
        if (accountNumber == null) {
            return null;
        }
        return accounts.get(accountNumber.trim());
    }

    /**
     * Checks if an account exists by its number.
     *
     * @param accountNumber Account number
     * @return true if exists, false otherwise
     */
    public boolean exists(String accountNumber) {
        if (accountNumber == null) {
            return false;
        }
        return accounts.containsKey(accountNumber.trim());
    }

    /**
     * Returns an unmodifiable list of all registered accounts.
     *
     * @return List of BankAccount
     */
    public List<BankAccount> getAllAccounts() {
        return Collections.unmodifiableList(new ArrayList<>(accounts.values()));
    }

    /**
     * Returns total number of accounts managed.
     *
     * @return count of accounts
     */
    public int getTotalAccountsCount() {
        return accounts.size();
    }
}
