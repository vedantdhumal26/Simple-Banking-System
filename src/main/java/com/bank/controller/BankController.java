package com.bank.controller;

import com.bank.controller.dto.AccountDto;
import com.bank.controller.dto.ApiResponse;
import com.bank.controller.dto.CreateAccountRequest;
import com.bank.controller.dto.TransactionRequest;
import com.bank.model.BankAccount;
import com.bank.service.BankService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * BankController Class
 * 
 * Exposes REST API endpoints for the Simple Banking System.
 * Handles HTTP requests, delegates business operations to BankService,
 * and returns clean, structured JSON responses.
 */
@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class BankController {

    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    /**
     * Endpoint: POST /api/accounts/create
     * Creates a new bank account.
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<AccountDto>> createAccount(@RequestBody CreateAccountRequest request) {
        try {
            if (request == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Request body cannot be empty."));
            }
            BankAccount account = bankService.createAccount(
                    request.getAccountHolderName(),
                    request.getInitialDeposit()
            );
            return ResponseEntity.ok(ApiResponse.ok("Account created successfully.", new AccountDto(account)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to create account: " + ex.getMessage()));
        }
    }

    /**
     * Endpoint: GET /api/accounts/{accountNumber}/balance
     * Checks balance for an existing account.
     */
    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<ApiResponse<BigDecimal>> getBalance(@PathVariable String accountNumber) {
        try {
            BigDecimal balance = bankService.getBalance(accountNumber);
            return ResponseEntity.ok(ApiResponse.ok("Balance retrieved successfully.", balance));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    /**
     * Endpoint: POST /api/accounts/deposit
     * Deposits money into an account.
     */
    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<AccountDto>> deposit(@RequestBody TransactionRequest request) {
        try {
            if (request == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Request body cannot be empty."));
            }
            BigDecimal updatedBalance = bankService.deposit(request.getAccountNumber(), request.getAmount());
            BankAccount account = bankService.getAccountInformation(request.getAccountNumber());

            String formattedAmount = formatCurrency(request.getAmount());
            String formattedBalance = formatCurrency(updatedBalance);
            String message = formattedAmount + " deposited successfully. Updated Balance: " + formattedBalance;

            return ResponseEntity.ok(ApiResponse.ok(message, new AccountDto(account)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Deposit failed: " + ex.getMessage()));
        }
    }

    /**
     * Endpoint: POST /api/accounts/withdraw
     * Withdraws money from an account.
     */
    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<AccountDto>> withdraw(@RequestBody TransactionRequest request) {
        try {
            if (request == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Request body cannot be empty."));
            }
            BigDecimal updatedBalance = bankService.withdraw(request.getAccountNumber(), request.getAmount());
            BankAccount account = bankService.getAccountInformation(request.getAccountNumber());

            String formattedAmount = formatCurrency(request.getAmount());
            String message = formattedAmount + " withdrawn successfully.";

            return ResponseEntity.ok(ApiResponse.ok(message, new AccountDto(account)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Withdrawal failed: " + ex.getMessage()));
        }
    }

    /**
     * Endpoint: GET /api/accounts/{accountNumber}
     * Retrieves full account information including daily limit stats.
     */
    @GetMapping("/{accountNumber}")
    public ResponseEntity<ApiResponse<AccountDto>> getAccountInformation(@PathVariable String accountNumber) {
        try {
            BankAccount account = bankService.getAccountInformation(accountNumber);
            return ResponseEntity.ok(ApiResponse.ok("Account details retrieved successfully.", new AccountDto(account)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    /**
     * Endpoint: GET /api/accounts
     * Retrieves all accounts registered in the bank.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountDto>>> getAllAccounts() {
        List<AccountDto> accounts = bankService.getAllAccounts().stream()
                .map(AccountDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok("All accounts retrieved.", accounts));
    }

    /**
     * Helper to format currency string.
     */
    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "\u20B90.00";
        return "\u20B9" + amount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }
}
