# Simple Banking System

A **Second-Year Engineering College Mini-Project** designed to demonstrate core **Java Object-Oriented Programming (OOP)** concepts through a full-featured, interactive banking web application.

---

## 1. Project Description

The **Simple Banking System** provides a clean web-based dashboard where users can create bank accounts and perform essential banking operations with real-time financial tracking and daily withdrawal limit monitoring.

All core banking operations, input validations, financial precision arithmetic, and daily withdrawal limits are implemented in **pure Java OOP** on the backend. The frontend is built using standard **HTML5, CSS3, and Vanilla JavaScript** to ensure it remains simple, beginner-friendly, and easy to explain during a college project review.

---

## 2. Key Features

1. **Create Account**:
   - Accepts Account Holder Name and Initial Deposit.
   - Enforces a minimum initial deposit of **₹500.00**.
   - Rejects negative, zero, or blank entries.
   - Automatically generates a unique 6-digit account number (e.g., `100001`, `100002`).
   - Sets the daily withdrawal limit to **₹10,000.00** and withdrawn today to **₹0.00**.

2. **Check Balance**:
   - Inquires current available balance by account number.
   - Returns a clear error message (`"Account not found."`) if nonexistent.

3. **Deposit Money**:
   - Credits funds using `java.math.BigDecimal` monetary arithmetic.
   - Validates that the deposit amount is strictly greater than **₹0.00**.
   - Immediately displays updated balance.

4. **Withdraw Money**:
   - Multi-step validation:
     - **Step 1:** Verifies account existence.
     - **Step 2:** Validates that amount is strictly greater than **₹0.00**.
     - **Step 3:** Verifies sufficient available balance (rejects with `"Insufficient balance."`).
     - **Step 4:** Verifies daily withdrawal limit (₹10,000.00 per calendar day).
     - **Step 5:** Deducts funds, updates balance, and records today's withdrawal total.

5. **Daily Withdrawal Limit Tracking & Visualization**:
   - Standard limit of **₹10,000.00** per account per calendar day.
   - Calculates Remaining Limit = ₹10,000.00 − Amount Withdrawn Today.
   - **Persistence Rule:** Counter does **NOT** reset on page refresh, browser reload, or navigation.
   - **Calendar Date Rule:** Automatically resets to ₹0.00 when the calendar date changes (`LocalDate.now()`).
   - Features a clean CSS progress bar showing quota usage (e.g., `₹2,000.00 / ₹10,000.00 used (20%)`).

6. **Account Information Statement**:
   - Displays clean cards showing Account Number, Account Holder Name, Account Type (`Savings Account`), Balance, Daily Limit, Withdrawn Today, and Remaining Limit.

7. **Clean Notification Area**:
   - Inline success and error notification boxes (no intrusive browser `alert()` popups).

---

## 3. Technologies Used

- **Programming Language**: Java 17 (OpenJDK)
- **Object-Oriented Programming**: Pure Java OOP (Encapsulation, Inheritance, Abstraction, Polymorphism)
- **Precision Monetary Math**: `java.math.BigDecimal` with `RoundingMode.HALF_UP`
- **Backend Web Server**: Spring Boot 3.2.4 (Embedded Apache Tomcat)
- **Data Storage**: In-Memory Thread-Safe Java Collection (`ConcurrentHashMap<String, BankAccount>`)
- **Frontend**: Semantic HTML5, Vanilla CSS3 (Custom Responsive Banking Theme), Vanilla JavaScript
- **Build Tool**: Apache Maven 3.9.6
- **Testing**: JUnit 5 (Jupiter)

---

## 4. Java OOP Concepts Demonstrated

This project is explicitly structured to demonstrate the 4 pillars of Object-Oriented Programming:

| OOP Concept | Project Implementation | File & Explanation |
| :--- | :--- | :--- |
| **1. Class & Object** | Blueprint and instances representing real-world banking entities. | [`BankAccount.java`](file:///src/main/java/com/bank/model/BankAccount.java), [`SavingsAccount.java`](file:///src/main/java/com/bank/model/SavingsAccount.java), [`Bank.java`](file:///src/main/java/com/bank/model/Bank.java). |
| **2. Encapsulation** | All sensitive fields (`balance`, `accountNumber`, `dailyWithdrawalLimit`, `amountWithdrawnToday`, `lastWithdrawalDate`) are declared `private`. No public setters for balance or withdrawn totals. Modifications happen strictly via validated methods: `deposit()`, `withdraw()`, `checkAndResetDailyLimit()`. | [`BankAccount.java`](file:///src/main/java/com/bank/model/BankAccount.java) lines 21–27. |
| **3. Inheritance** | `SavingsAccount` inherits fields and business logic from `BankAccount` using the `extends` keyword, avoiding code duplication and promoting reuse. | [`SavingsAccount.java`](file:///src/main/java/com/bank/model/SavingsAccount.java) line 14: `public class SavingsAccount extends BankAccount`. |
| **4. Polymorphism** | **Method Overriding**: `SavingsAccount` overrides `getAccountType()` and `withdraw()`.<br>**Dynamic Method Dispatch**: In `BankService.java`, a `BankAccount` reference refers to a `SavingsAccount` object at runtime (`BankAccount newAccount = new SavingsAccount(...)`). | [`SavingsAccount.java`](file:///src/main/java/com/bank/model/SavingsAccount.java) lines 32–47 & [`BankService.java`](file:///src/main/java/com/bank/service/BankService.java) line 56. |
| **5. Abstraction** | The `AccountOperations` interface defines the abstract contract of what operations an account must support (`deposit`, `withdraw`, `getBalance`, `getRemainingDailyLimit`), hiding internal implementation details from callers. | [`AccountOperations.java`](file:///src/main/java/com/bank/model/AccountOperations.java). |
| **6. Object Composition** | The `Bank` class manages a collection of `BankAccount` objects using `Map<String, BankAccount>`, encapsulating storage and ID generation. | [`Bank.java`](file:///src/main/java/com/bank/model/Bank.java) lines 20–25. |

---

## 5. Project Directory Structure

```text
OOP PROJECT/
│
├── .vscode/
│   ├── launch.json                   # One-click VS Code debugger configuration (F5)
│   ├── tasks.json                    # VS Code build and test tasks
│   └── settings.json                 # Java and UTF-8 workspace settings
│
├── src/
│   ├── main/
│   │   ├── java/com/bank/
│   │   │   ├── BankingApplication.java      # Spring Boot application main entry point
│   │   │   │
│   │   │   ├── model/
│   │   │   │   ├── AccountOperations.java   # Interface demonstrating ABSTRACTION
│   │   │   │   ├── BankAccount.java         # Base class demonstrating ENCAPSULATION
│   │   │   │   ├── SavingsAccount.java      # Subclass demonstrating INHERITANCE & POLYMORPHISM
│   │   │   │   └── Bank.java                # Manages account collection & unique IDs
│   │   │   │
│   │   │   ├── service/
│   │   │   │   └── BankService.java         # Business logic layer (validation, limits, transactions)
│   │   │   │
│   │   │   └── controller/
│   │   │       ├── BankController.java      # REST API endpoints (/api/accounts/*)
│   │   │       └── dto/
│   │   │           ├── AccountDto.java             # Account response data transfer object
│   │   │           ├── ApiResponse.java            # Standard API response envelope
│   │   │           ├── CreateAccountRequest.java   # Account creation request payload
│   │   │           └── TransactionRequest.java     # Deposit/Withdraw request payload
│   │   │
│   │   └── resources/
│   │       ├── application.properties       # Server configuration (port 8080)
│   │       └── static/                      # Web Frontend served at root http://localhost:8080/
│   │           ├── index.html               # Main dashboard web page
│   │           ├── css/
│   │           │   └── style.css            # Clean modern banking theme stylesheet
│   │           └── js/
│   │               └── script.js            # Frontend interactions and API communications
│   │
│   └── test/
│       └── java/com/bank/
│           └── BankServiceTest.java         # Complete JUnit 5 test suite (10 test cases)
│
├── pom.xml                                  # Maven project configuration
├── run.bat                                  # Double-click Windows startup batch script
└── README.md                                # Complete project documentation
```

---

## 6. Windows + VS Code Setup Guide

Follow these steps to run the project on Windows:

### Step 1: Check or Install Java JDK 17
1. Open PowerShell or Command Prompt and verify:
   ```powershell
   java -version
   ```
2. If Java is not installed, install OpenJDK 17 via Winget or Adoptium:
   ```powershell
   winget install --id Microsoft.OpenJDK.17 -e
   ```
3. Ensure `JAVA_HOME` points to your JDK directory (e.g. `C:\Program Files\Microsoft\jdk-17...` or `C:\Users\<username>\jdk-17`).

### Step 2: Install VS Code and the Java Extension Pack
1. Download and install [VS Code](https://code.visualstudio.com/).
2. Open VS Code, press `Ctrl + Shift + X` (Extensions), and search for **"Extension Pack for Java"** by Microsoft.
3. Click **Install**.

### Step 3: Open the Project in VS Code
1. Open VS Code.
2. Click **File** > **Open Folder...** (or press `Ctrl + K, Ctrl + O`).
3. Select the `OOP PROJECT` folder.

### Step 4: Run the Application

You have three convenient ways to run the project:

#### Option A: One-Click in VS Code (Recommended)
1. Open `src/main/java/com/bank/BankingApplication.java`.
2. Click the **"Run"** button above the `main` method, or simply press **`F5`** (configured via `.vscode/launch.json`).

#### Option B: Double-Click Batch File on Windows
1. In File Explorer, navigate to the project directory.
2. Double-click **`run.bat`**.
3. It will automatically detect Java and Maven, launch the application, and start the web server.

#### Option C: Terminal Command (Maven)
Open the VS Code built-in terminal (`Ctrl + ~`) and execute:
```powershell
mvn spring-boot:run
```
Or to run the pre-built JAR:
```powershell
java -jar target/simple-banking-system-1.0.0.jar
```

### Step 5: Open the Web Application
Once the terminal displays:
```text
=================================================
 Simple Banking System Web Application Started! 
 Open your browser and navigate to:              
 http://localhost:8080                           
=================================================
```
Open your browser (Google Chrome, Edge, Firefox) and navigate to:
👉 **`http://localhost:8080`**

---

## 7. How to Use the Banking Dashboard

1. **Create Account**:
   - Click **Create Account** in the sidebar.
   - Enter Account Holder Name (e.g., `Rahul`).
   - Enter Initial Deposit (e.g., `5000` — must be ≥ ₹500).
   - Click **Create Account**. Your 6-digit Account Number (e.g., `100001`) will be displayed and set as the active account.

2. **Check Balance**:
   - Click **Check Balance** in the sidebar.
   - The active account number is automatically prefilled.
   - Click **Check Balance** to see current available funds.

3. **Deposit Money**:
   - Click **Deposit Money** in the sidebar.
   - Enter deposit amount (e.g., `2000`).
   - Click **Deposit Funds**. The updated balance (e.g., `₹7,000.00`) is displayed instantly.

4. **Withdraw Money**:
   - Click **Withdraw Money** in the sidebar.
   - Enter withdrawal amount (e.g., `2000`).
   - Click **Withdraw Funds**.
   - The system checks available balance and verifies the ₹10,000 daily withdrawal limit.
   - Updated balance (`₹5,000.00`) and remaining daily limit (`₹8,000.00`) are shown.

5. **Account Information**:
   - Click **Account Information** in the sidebar.
   - Click **View Information** to view a full card statement.

6. **Daily Withdrawal Limit Tracker**:
   - Click **Daily Withdrawal Limit** in the sidebar.
   - Displays real-time progress bar and percentage of the ₹10,000 daily limit used.

7. **Switch Accounts**:
   - Use the **Switch Account** dropdown in the bottom of the sidebar to jump between different created accounts.

---

## 8. Verification & Test Cases

The project includes 10 automated unit tests in [`BankServiceTest.java`](file:///src/test/java/com/bank/BankServiceTest.java) covering all test scenarios:

To run all tests:
```powershell
mvn test
```

| # | Test Scenario | Inputs | Expected Output | Status |
| :---: | :--- | :--- | :--- | :---: |
| **1** | Create Account | Name: `Rahul`, Deposit: `₹5,000` | Account created successfully; Initial balance ₹5,000; Limit ₹10,000 | **PASSED** |
| **2** | Deposit Below Minimum | Deposit: `₹100` | Rejected: `"Initial deposit must be at least ₹500."` | **PASSED** |
| **3** | Valid Deposit | Deposit: `₹2,000` | Balance increases to ₹7,000 | **PASSED** |
| **4** | Valid Withdrawal | Balance: `₹10,000`, Withdraw: `₹2,000` | Balance ₹8,000; Withdrawn Today ₹2,000; Remaining Limit ₹8,000 | **PASSED** |
| **5** | Insufficient Balance | Balance: `₹2,000`, Withdraw: `₹5,000` | Rejected: `"Insufficient balance."`; Balance remains ₹2,000 | **PASSED** |
| **6** | Daily Limit Exceeded | Withdraw: `₹8,000`, then withdraw `₹3,000` | Second withdrawal rejected: `"Daily withdrawal limit exceeded."` | **PASSED** |
| **7** | Daily Limit Exact Quota | Withdraw exactly `₹10,000` in total | Remaining limit becomes ₹0.00; Next withdrawal of ₹100 rejected | **PASSED** |
| **8** | Invalid Amounts | Amount `₹0` or negative `-₹200` | Rejected: `"Amount must be greater than ₹0."` | **PASSED** |
| **9** | Nonexistent Account | Account `#999999` | Rejected: `"Account not found."` | **PASSED** |
| **10**| Refresh / Date Tracking | Perform withdrawal, refresh / query today | Limit does NOT reset until calendar date changes (`LocalDate`) | **PASSED** |

---

## 9. College Review Viva Questions & Answers

1. **Q: Why did you use `BigDecimal` instead of `double` or `float` for money?**
   - **A:** `float` and `double` are binary floating-point numbers subject to rounding errors (e.g., `0.1 + 0.2 = 0.30000000000000004`). In banking applications, rounding errors can accumulate into financial discrepancies. `BigDecimal` provides exact decimal representation and precise control over rounding (`RoundingMode.HALF_UP`).

2. **Q: Where is Encapsulation applied in your project?**
   - **A:** In `BankAccount.java`, all fields like `balance`, `dailyWithdrawalLimit`, and `amountWithdrawnToday` are marked `private`. There are no raw setters. Balance changes can only happen through validated methods (`deposit()` and `withdraw()`).

3. **Q: How does Polymorphism work here?**
   - **A:** In `BankService.java`, we declare a reference of base class `BankAccount` and instantiate the child class `SavingsAccount` (`BankAccount acc = new SavingsAccount(...)`). When `getAccountType()` or `withdraw()` is invoked, Java's dynamic method dispatch executes the overridden method in `SavingsAccount`.

4. **Q: How does the Daily Withdrawal Limit work across browser refreshes?**
   - **A:** The tracking state is stored in the Java `BankAccount` instance (`amountWithdrawnToday` and `lastWithdrawalDate`) on the backend server. When the browser reloads, it queries the backend API, which preserves the state. The counter resets only when `LocalDate.now()` is different from `lastWithdrawalDate`.
