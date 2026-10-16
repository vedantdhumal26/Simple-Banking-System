package com.bank;

import com.bank.model.BankAccount;
import com.bank.model.SavingsAccount;
import com.bank.service.BankService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests validating all requirements from Section 31 of the specifications.
 */
public class BankServiceTest {

    private BankService bankService;

    @BeforeEach
    void setUp() {
        bankService = new BankService();
    }

    @Test
    @DisplayName("Test 1: Create Account - Successful account creation")
    void testCreateAccountSuccess() {
        BankAccount account = bankService.createAccount("Rahul", new BigDecimal("5000.00"));
        assertNotNull(account);
        assertNotNull(account.getAccountNumber());
        assertEquals("Rahul", account.getAccountHolderName());
        assertEquals(new BigDecimal("5000.00"), account.getBalance());
        assertEquals(new BigDecimal("10000.00"), account.getDailyWithdrawalLimit());
        assertEquals(new BigDecimal("0.00"), account.getAmountWithdrawnToday());
        assertEquals(new BigDecimal("10000.00"), account.getRemainingDailyLimit());
        assertTrue(account instanceof SavingsAccount, "Account should be instance of SavingsAccount demonstrating polymorphism");
    }

    @Test
    @DisplayName("Test 2: Initial Deposit Below Minimum - Should be rejected")
    void testInitialDepositBelowMinimum() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> bankService.createAccount("Rahul", new BigDecimal("100.00"))
        );
        assertEquals("Initial deposit must be at least \u20B9500.", ex.getMessage());
    }

    @Test
    @DisplayName("Test 3: Deposit - Balance increases correctly")
    void testDepositIncreasesBalance() {
        BankAccount account = bankService.createAccount("Rahul", new BigDecimal("5500.00"));
        BigDecimal updatedBalance = bankService.deposit(account.getAccountNumber(), new BigDecimal("2000.00"));
        assertEquals(new BigDecimal("7500.00"), updatedBalance);
        assertEquals(new BigDecimal("7500.00"), account.getBalance());
    }

    @Test
    @DisplayName("Test 4: Valid Withdrawal - Balance and daily limit update correctly")
    void testValidWithdrawal() {
        BankAccount account = bankService.createAccount("Rahul", new BigDecimal("10000.00"));
        BigDecimal updatedBalance = bankService.withdraw(account.getAccountNumber(), new BigDecimal("2000.00"));
        
        assertEquals(new BigDecimal("8000.00"), updatedBalance);
        assertEquals(new BigDecimal("8000.00"), account.getBalance());
        assertEquals(new BigDecimal("2000.00"), account.getAmountWithdrawnToday());
        assertEquals(new BigDecimal("8000.00"), account.getRemainingDailyLimit());
    }

    @Test
    @DisplayName("Test 5: Insufficient Balance - Withdrawal rejected, balance unchanged")
    void testInsufficientBalance() {
        BankAccount account = bankService.createAccount("Rahul", new BigDecimal("2000.00"));
        
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> bankService.withdraw(account.getAccountNumber(), new BigDecimal("5000.00"))
        );
        assertEquals("Insufficient balance.", ex.getMessage());
        assertEquals(new BigDecimal("2000.00"), account.getBalance());
        assertEquals(new BigDecimal("0.00"), account.getAmountWithdrawnToday());
    }

    @Test
    @DisplayName("Test 6: Daily Limit - Exceeding daily limit should be rejected")
    void testDailyWithdrawalLimitExceeded() {
        BankAccount account = bankService.createAccount("Rahul", new BigDecimal("15000.00"));
        
        // First withdraw ₹8,000 (valid, remaining = ₹2,000)
        bankService.withdraw(account.getAccountNumber(), new BigDecimal("8000.00"));
        assertEquals(new BigDecimal("2000.00"), account.getRemainingDailyLimit());

        // Then attempt another ₹3,000 (exceeds ₹2,000 remaining limit)
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> bankService.withdraw(account.getAccountNumber(), new BigDecimal("3000.00"))
        );
        assertEquals("Daily withdrawal limit exceeded.", ex.getMessage());
        assertEquals(new BigDecimal("7000.00"), account.getBalance());
        assertEquals(new BigDecimal("8000.00"), account.getAmountWithdrawnToday());
    }

    @Test
    @DisplayName("Test 7: Daily Limit Exact Amount - Withdrawing exactly ₹10,000 total")
    void testDailyLimitExactAmount() {
        BankAccount account = bankService.createAccount("Rahul", new BigDecimal("15000.00"));
        
        // Withdraw exactly ₹10,000
        bankService.withdraw(account.getAccountNumber(), new BigDecimal("10000.00"));
        assertEquals(new BigDecimal("10000.00"), account.getAmountWithdrawnToday());
        assertEquals(new BigDecimal("0.00"), account.getRemainingDailyLimit());

        // Any additional withdrawal must be rejected
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> bankService.withdraw(account.getAccountNumber(), new BigDecimal("100.00"))
        );
        assertEquals("Daily withdrawal limit exceeded.", ex.getMessage());
    }

    @Test
    @DisplayName("Test 8: Invalid Amount - Zero or negative amounts rejected")
    void testInvalidAmountsRejected() {
        BankAccount account = bankService.createAccount("Rahul", new BigDecimal("1000.00"));

        // Zero deposit
        assertThrows(
                IllegalArgumentException.class,
                () -> bankService.deposit(account.getAccountNumber(), BigDecimal.ZERO)
        );

        // Negative deposit
        assertThrows(
                IllegalArgumentException.class,
                () -> bankService.deposit(account.getAccountNumber(), new BigDecimal("-500.00"))
        );

        // Zero withdrawal
        assertThrows(
                IllegalArgumentException.class,
                () -> bankService.withdraw(account.getAccountNumber(), BigDecimal.ZERO)
        );

        // Negative withdrawal
        assertThrows(
                IllegalArgumentException.class,
                () -> bankService.withdraw(account.getAccountNumber(), new BigDecimal("-200.00"))
        );
    }

    @Test
    @DisplayName("Test 9: Invalid Account - Non-existent account rejected")
    void testInvalidAccountNotFound() {
        IllegalArgumentException ex1 = assertThrows(
                IllegalArgumentException.class,
                () -> bankService.getBalance("999999")
        );
        assertEquals("Account not found.", ex1.getMessage());

        IllegalArgumentException ex2 = assertThrows(
                IllegalArgumentException.class,
                () -> bankService.deposit("999999", new BigDecimal("1000.00"))
        );
        assertEquals("Account not found.", ex2.getMessage());

        IllegalArgumentException ex3 = assertThrows(
                IllegalArgumentException.class,
                () -> bankService.withdraw("999999", new BigDecimal("1000.00"))
        );
        assertEquals("Account not found.", ex3.getMessage());
    }

    @Test
    @DisplayName("Test 10: Daily Limit Reset Only on Calendar Date Change")
    void testDailyLimitCalendarDateReset() {
        BankAccount account = bankService.createAccount("Rahul", new BigDecimal("10000.00"));
        bankService.withdraw(account.getAccountNumber(), new BigDecimal("4000.00"));

        // Calling checkAndResetDailyLimit for same day does NOT reset
        account.checkAndResetDailyLimit(LocalDate.now());
        assertEquals(new BigDecimal("4000.00"), account.getAmountWithdrawnToday());
        assertEquals(new BigDecimal("6000.00"), account.getRemainingDailyLimit());

        // Calling checkAndResetDailyLimit for next day DOES reset
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        account.checkAndResetDailyLimit(tomorrow);
        assertEquals(new BigDecimal("0.00"), account.getAmountWithdrawnToday());
        assertEquals(new BigDecimal("10000.00"), account.getRemainingDailyLimit());
    }
}
