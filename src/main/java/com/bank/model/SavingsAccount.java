package com.bank.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * SavingsAccount Class
 * 
 * Demonstrates:
 * 1. INHERITANCE: Inherits all state and capabilities from BankAccount using "extends".
 * 2. POLYMORPHISM & METHOD OVERRIDING: Overrides getAccountType() and withdraw()
 *    to specialize behavior for a savings account.
 */
public class SavingsAccount extends BankAccount {

    // Annual interest rate applicable to Savings Accounts (4.0% per annum)
    private static final BigDecimal ANNUAL_INTEREST_RATE = new BigDecimal("4.00");

    /**
     * Parameterized Constructor invoking parent constructor via super().
     *
     * @param accountNumber     Unique account number
     * @param accountHolderName Account holder's full name
     * @param initialDeposit    Initial deposit amount (must be >= 500)
     */
    public SavingsAccount(String accountNumber, String accountHolderName, BigDecimal initialDeposit) {
        super(accountNumber, accountHolderName, initialDeposit);
    }

    /**
     * Demonstrates METHOD OVERRIDING & RUNTIME POLYMORPHISM.
     * Returns specialized account type for Savings Account.
     */
    @Override
    public String getAccountType() {
        return "Savings Account";
    }

    /**
     * Demonstrates METHOD OVERRIDING & RUNTIME POLYMORPHISM.
     * Specializes withdrawal for savings accounts while delegating the core
     * balance and daily limit validation to super.withdraw(amount).
     */
    @Override
    public synchronized void withdraw(BigDecimal amount) {
        // Delegates core withdrawal, balance checks, and daily limit tracking to parent
        super.withdraw(amount);
    }

    /**
     * Helper method specific to SavingsAccount to calculate annual interest earned.
     *
     * @return Estimated annual interest based on current balance
     */
    public BigDecimal calculateAnnualInterest() {
        return getBalance()
                .multiply(ANNUAL_INTEREST_RATE)
                .divide(new BigDecimal("100.00"), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getAnnualInterestRate() {
        return ANNUAL_INTEREST_RATE;
    }
}
