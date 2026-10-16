/**
 * Simple Banking System - Frontend Logic
 * Demonstrates clean client-side interaction with Java OOP Backend API
 */

// Global State
let currentAccount = null;

// Initialize on DOM Ready
document.addEventListener('DOMContentLoaded', () => {
    setupNavigation();
    setupMobileToggle();
    setupAccountSwitcher();
    loadAllAccounts();
    updateDashboardUI();
});

/* -------------------------------------------------------------
 * NAVIGATION & UI TAB SWITCHING
 * ------------------------------------------------------------- */
function setupNavigation() {
    const navItems = document.querySelectorAll('.nav-item');
    navItems.forEach(item => {
        item.addEventListener('click', () => {
            const sectionId = item.getAttribute('data-section');
            navigateToSection(sectionId);
        });
    });

    // Close notification on click
    const notifClose = document.getElementById('notification-close');
    if (notifClose) {
        notifClose.addEventListener('click', hideNotification);
    }
}

function navigateToSection(sectionId) {
    // Update active nav button
    document.querySelectorAll('.nav-item').forEach(btn => {
        if (btn.getAttribute('data-section') === sectionId) {
            btn.classList.add('active');
        } else {
            btn.classList.remove('active');
        }
    });

    // Show target section, hide others
    document.querySelectorAll('.section-pane').forEach(pane => {
        pane.classList.remove('active');
    });
    const targetPane = document.getElementById(sectionId);
    if (targetPane) {
        targetPane.classList.add('active');
    }

    // Auto-close sidebar on mobile
    const sidebar = document.getElementById('app-sidebar');
    if (sidebar && sidebar.classList.contains('open')) {
        sidebar.classList.remove('open');
    }

    // If an account is active, prefill the form in the visited section
    if (currentAccount) {
        prefillAccountInputs(currentAccount.accountNumber);
    }
}

function setupMobileToggle() {
    const toggle = document.getElementById('mobile-nav-toggle');
    const sidebar = document.getElementById('app-sidebar');
    if (toggle && sidebar) {
        toggle.addEventListener('click', () => {
            sidebar.classList.toggle('open');
        });
    }
}

function setupAccountSwitcher() {
    const select = document.getElementById('active-account-select');
    if (select) {
        select.addEventListener('change', (e) => {
            const accNum = e.target.value;
            if (accNum) {
                fetchAccountDetails(accNum, true);
            } else {
                currentAccount = null;
                updateDashboardUI();
                showNotification('info', 'No account selected.');
            }
        });
    }
}

/* -------------------------------------------------------------
 * NOTIFICATION / ALERT BANNER
 * ------------------------------------------------------------- */
function showNotification(type, message) {
    const container = document.getElementById('notification-container');
    const box = document.getElementById('notification-box');
    const content = document.getElementById('notification-content');
    const icon = document.getElementById('notification-icon');

    if (!container || !box || !content) return;

    box.className = 'notification-box ' + type;
    content.textContent = message;

    if (type === 'success') {
        icon.innerHTML = '✓ ';
    } else if (type === 'error') {
        icon.innerHTML = '✕ ';
    } else {
        icon.innerHTML = 'ℹ ';
    }

    container.style.display = 'block';
    container.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

function hideNotification() {
    const container = document.getElementById('notification-container');
    if (container) {
        container.style.display = 'none';
    }
}

/* -------------------------------------------------------------
 * FORMATTING HELPERS
 * ------------------------------------------------------------- */
function formatCurrency(amount) {
    if (amount === undefined || amount === null) return '₹0.00';
    const num = parseFloat(amount);
    if (isNaN(num)) return '₹0.00';
    return '₹' + num.toLocaleString('en-IN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}

function prefillAccountInputs(accNum) {
    const ids = [
        'check-account-number',
        'deposit-account-number',
        'withdraw-account-number',
        'info-account-number',
        'limit-account-number'
    ];
    ids.forEach(id => {
        const el = document.getElementById(id);
        if (el && !el.value) {
            el.value = accNum;
        }
    });
}

/* -------------------------------------------------------------
 * API CALL: LOAD ALL ACCOUNTS FOR SELECTOR
 * ------------------------------------------------------------- */
async function loadAllAccounts() {
    try {
        const response = await fetch('/api/accounts');
        const resData = await response.json();
        if (resData.success && Array.isArray(resData.data)) {
            const select = document.getElementById('active-account-select');
            if (!select) return;

            select.innerHTML = '<option value="">-- Switch Account --</option>';
            resData.data.forEach(acc => {
                const opt = document.createElement('option');
                opt.value = acc.accountNumber;
                opt.textContent = `#${acc.accountNumber} - ${acc.accountHolderName}`;
                select.appendChild(opt);
            });

            if (currentAccount) {
                select.value = currentAccount.accountNumber;
            }
        }
    } catch (err) {
        console.error('Failed to load accounts list:', err);
    }
}

/* -------------------------------------------------------------
 * UPDATE DASHBOARD KPI & PROGRESS BAR UI
 * ------------------------------------------------------------- */
function updateDashboardUI() {
    const badge = document.getElementById('active-account-display');
    const balEl = document.getElementById('kpi-balance');
    const hintEl = document.getElementById('kpi-account-hint');
    const withEl = document.getElementById('kpi-withdrawn-today');
    const remEl = document.getElementById('kpi-remaining-limit');

    const visualBadge = document.getElementById('visual-status-badge');
    const progressFill = document.getElementById('dashboard-progress-fill');
    const progressUsedText = document.getElementById('progress-used-text');
    const progressRemText = document.getElementById('progress-remaining-text');

    if (!currentAccount) {
        if (badge) badge.textContent = 'None Selected';
        if (balEl) balEl.textContent = '₹0.00';
        if (hintEl) hintEl.textContent = 'No account selected';
        if (withEl) withEl.textContent = '₹0.00';
        if (remEl) remEl.textContent = '₹10,000.00';

        if (visualBadge) visualBadge.textContent = 'No account selected';
        if (progressFill) progressFill.style.width = '0%';
        if (progressUsedText) progressUsedText.textContent = 'Used Today: ₹0.00';
        if (progressRemText) progressRemText.textContent = 'Remaining: ₹10,000.00';
        return;
    }

    if (badge) badge.textContent = `#${currentAccount.accountNumber} (${currentAccount.accountHolderName})`;
    if (balEl) balEl.textContent = formatCurrency(currentAccount.balance);
    if (hintEl) hintEl.textContent = `${currentAccount.accountHolderName} • ${currentAccount.accountType || 'Savings'}`;
    if (withEl) withEl.textContent = formatCurrency(currentAccount.amountWithdrawnToday);
    if (remEl) remEl.textContent = formatCurrency(currentAccount.remainingDailyLimit);

    // Calculate limit progress
    const limitTotal = parseFloat(currentAccount.dailyWithdrawalLimit) || 10000;
    const withdrawn = parseFloat(currentAccount.amountWithdrawnToday) || 0;
    const remaining = parseFloat(currentAccount.remainingDailyLimit) || (limitTotal - withdrawn);

    const percentage = Math.min(100, Math.max(0, (withdrawn / limitTotal) * 100));

    if (visualBadge) {
        visualBadge.textContent = `${formatCurrency(withdrawn)} / ${formatCurrency(limitTotal)} Used`;
    }
    if (progressFill) {
        progressFill.style.width = `${percentage}%`;
        // Color transition
        if (percentage >= 100) {
            progressFill.style.backgroundColor = 'var(--error-red)';
        } else if (percentage >= 75) {
            progressFill.style.backgroundColor = 'var(--warning-orange)';
        } else {
            progressFill.style.backgroundColor = 'var(--primary-blue)';
        }
    }
    if (progressUsedText) progressUsedText.textContent = `Used Today: ${formatCurrency(withdrawn)} (${percentage.toFixed(0)}%)`;
    if (progressRemText) progressRemText.textContent = `Remaining: ${formatCurrency(remaining)}`;

    // Update Daily Withdrawal Limit Pane if loaded
    updateLimitPaneUI(currentAccount);
}

function updateLimitPaneUI(account) {
    const accBadge = document.getElementById('limit-selected-acc-badge');
    const statLimit = document.getElementById('stat-daily-limit');
    const statWith = document.getElementById('stat-withdrawn-today');
    const statRem = document.getElementById('stat-remaining-limit');
    const pctText = document.getElementById('limit-percentage-text');
    const fill = document.getElementById('limit-progress-fill');
    const usedLbl = document.getElementById('limit-used-label');
    const remLbl = document.getElementById('limit-remaining-label');

    if (!account) return;

    if (accBadge) accBadge.textContent = `#${account.accountNumber} (${account.accountHolderName})`;
    if (statLimit) statLimit.textContent = formatCurrency(account.dailyWithdrawalLimit);
    if (statWith) statWith.textContent = formatCurrency(account.amountWithdrawnToday);
    if (statRem) statRem.textContent = formatCurrency(account.remainingDailyLimit);

    const limitTotal = parseFloat(account.dailyWithdrawalLimit) || 10000;
    const withdrawn = parseFloat(account.amountWithdrawnToday) || 0;
    const pct = Math.min(100, Math.max(0, (withdrawn / limitTotal) * 100));

    if (pctText) pctText.textContent = `${pct.toFixed(0)}%`;
    if (fill) {
        fill.style.width = `${pct}%`;
        fill.style.backgroundColor = pct >= 100 ? 'var(--error-red)' : (pct >= 75 ? 'var(--warning-orange)' : 'var(--primary-blue)');
    }
    if (usedLbl) usedLbl.textContent = `${formatCurrency(withdrawn)} / ${formatCurrency(limitTotal)} used`;
    if (remLbl) remLbl.textContent = `Remaining: ${formatCurrency(account.remainingDailyLimit)}`;
}

/* -------------------------------------------------------------
 * OPERATION 1: CREATE ACCOUNT
 * ------------------------------------------------------------- */
async function handleCreateAccount(event) {
    event.preventDefault();
    hideNotification();

    const nameInput = document.getElementById('create-holder-name');
    const depositInput = document.getElementById('create-initial-deposit');
    const btn = document.getElementById('btn-create-account');

    const name = nameInput.value.trim();
    const initialDeposit = parseFloat(depositInput.value);

    // Client-side quick check
    if (!name) {
        showNotification('error', 'Account holder name cannot be empty.');
        return;
    }
    if (isNaN(initialDeposit) || initialDeposit <= 0) {
        showNotification('error', 'Initial deposit cannot be negative or zero.');
        return;
    }
    if (initialDeposit < 500) {
        showNotification('error', 'Initial deposit must be at least ₹500.');
        return;
    }

    try {
        btn.disabled = true;
        btn.innerHTML = '<span>Creating...</span>';

        const response = await fetch('/api/accounts/create', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                accountHolderName: name,
                initialDeposit: initialDeposit
            })
        });

        const res = await response.json();

        if (res.success && res.data) {
            currentAccount = res.data;
            showNotification('success', 'Account created successfully.');

            // Display result card
            document.getElementById('res-account-number').textContent = res.data.accountNumber;
            document.getElementById('res-holder-name').textContent = res.data.accountHolderName;
            document.getElementById('res-initial-balance').textContent = formatCurrency(res.data.balance);
            document.getElementById('res-account-type').textContent = res.data.accountType || 'Savings Account';
            document.getElementById('res-daily-limit').textContent = formatCurrency(res.data.dailyWithdrawalLimit);
            document.getElementById('create-result-card').style.display = 'block';

            // Reset form inputs
            nameInput.value = '';
            depositInput.value = '';

            // Update state & UI
            loadAllAccounts();
            updateDashboardUI();
            prefillAccountInputs(res.data.accountNumber);
        } else {
            showNotification('error', res.message || 'Failed to create account.');
        }
    } catch (err) {
        showNotification('error', 'Network or server error: ' + err.message);
    } finally {
        btn.disabled = false;
        btn.innerHTML = '<span>Create Account</span>';
    }
}

/* -------------------------------------------------------------
 * OPERATION 2: CHECK BALANCE
 * ------------------------------------------------------------- */
async function handleCheckBalance(event) {
    event.preventDefault();
    hideNotification();

    const accInput = document.getElementById('check-account-number');
    const btn = document.getElementById('btn-check-balance');
    const accNum = accInput.value.trim();

    if (!accNum) {
        showNotification('error', 'Account number cannot be empty.');
        return;
    }

    try {
        btn.disabled = true;
        btn.innerHTML = '<span>Checking...</span>';

        const response = await fetch(`/api/accounts/${encodeURIComponent(accNum)}/balance`);
        const res = await response.json();

        if (res.success) {
            showNotification('success', `Current balance for Account #${accNum}: ${formatCurrency(res.data)}`);
            document.getElementById('bal-res-acc').textContent = `Account #${accNum}`;
            document.getElementById('bal-res-amount').textContent = formatCurrency(res.data);
            document.getElementById('bal-res-time').textContent = `Available Balance as of ${new Date().toLocaleTimeString()}`;
            document.getElementById('balance-result-card').style.display = 'block';

            // Refresh full account info in background
            fetchAccountDetails(accNum, false);
        } else {
            document.getElementById('balance-result-card').style.display = 'none';
            showNotification('error', res.message || 'Account not found.');
        }
    } catch (err) {
        showNotification('error', 'Network or server error: ' + err.message);
    } finally {
        btn.disabled = false;
        btn.innerHTML = '<span>Check Balance</span>';
    }
}

/* -------------------------------------------------------------
 * OPERATION 3: DEPOSIT MONEY
 * ------------------------------------------------------------- */
async function handleDeposit(event) {
    event.preventDefault();
    hideNotification();

    const accInput = document.getElementById('deposit-account-number');
    const amountInput = document.getElementById('deposit-amount');
    const btn = document.getElementById('btn-deposit');

    const accNum = accInput.value.trim();
    const amount = parseFloat(amountInput.value);

    if (!accNum) {
        showNotification('error', 'Account number cannot be empty.');
        return;
    }
    if (isNaN(amount) || amount <= 0) {
        showNotification('error', 'Deposit amount must be greater than ₹0.');
        return;
    }

    try {
        btn.disabled = true;
        btn.innerHTML = '<span>Depositing...</span>';

        const response = await fetch('/api/accounts/deposit', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                accountNumber: accNum,
                amount: amount
            })
        });

        const res = await response.json();

        if (res.success && res.data) {
            currentAccount = res.data;
            showNotification('success', res.message);

            document.getElementById('dep-res-msg').textContent = res.message;
            document.getElementById('dep-res-acc').textContent = res.data.accountNumber;
            document.getElementById('dep-res-balance').textContent = formatCurrency(res.data.balance);
            document.getElementById('deposit-result-card').style.display = 'block';

            amountInput.value = '';
            updateDashboardUI();
        } else {
            showNotification('error', res.message || 'Deposit failed.');
        }
    } catch (err) {
        showNotification('error', 'Network or server error: ' + err.message);
    } finally {
        btn.disabled = false;
        btn.innerHTML = '<span>Deposit Funds</span>';
    }
}

/* -------------------------------------------------------------
 * OPERATION 4: WITHDRAW MONEY
 * ------------------------------------------------------------- */
async function handleWithdraw(event) {
    event.preventDefault();
    hideNotification();

    const accInput = document.getElementById('withdraw-account-number');
    const amountInput = document.getElementById('withdraw-amount');
    const btn = document.getElementById('btn-withdraw');

    const accNum = accInput.value.trim();
    const amount = parseFloat(amountInput.value);

    if (!accNum) {
        showNotification('error', 'Account number cannot be empty.');
        return;
    }
    if (isNaN(amount) || amount <= 0) {
        showNotification('error', 'Amount must be greater than ₹0.');
        return;
    }

    try {
        btn.disabled = true;
        btn.innerHTML = '<span>Withdrawing...</span>';

        const response = await fetch('/api/accounts/withdraw', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                accountNumber: accNum,
                amount: amount
            })
        });

        const res = await response.json();

        if (res.success && res.data) {
            currentAccount = res.data;
            showNotification('success', res.message);

            document.getElementById('with-res-msg').textContent = res.message;
            document.getElementById('with-res-acc').textContent = res.data.accountNumber;
            document.getElementById('with-res-balance').textContent = formatCurrency(res.data.balance);
            document.getElementById('with-res-today').textContent = formatCurrency(res.data.amountWithdrawnToday);
            document.getElementById('with-res-remaining').textContent = formatCurrency(res.data.remainingDailyLimit);
            document.getElementById('withdraw-result-card').style.display = 'block';

            amountInput.value = '';
            updateDashboardUI();
        } else {
            showNotification('error', res.message || 'Withdrawal rejected.');
        }
    } catch (err) {
        showNotification('error', 'Network or server error: ' + err.message);
    } finally {
        btn.disabled = false;
        btn.innerHTML = '<span>Withdraw Funds</span>';
    }
}

/* -------------------------------------------------------------
 * OPERATION 5: ACCOUNT INFORMATION
 * ------------------------------------------------------------- */
async function handleAccountInfo(event) {
    event.preventDefault();
    hideNotification();

    const accInput = document.getElementById('info-account-number');
    const btn = document.getElementById('btn-account-info');
    const accNum = accInput.value.trim();

    if (!accNum) {
        showNotification('error', 'Account number cannot be empty.');
        return;
    }

    try {
        btn.disabled = true;
        btn.innerHTML = '<span>Loading...</span>';

        const response = await fetch(`/api/accounts/${encodeURIComponent(accNum)}`);
        const res = await response.json();

        if (res.success && res.data) {
            currentAccount = res.data;
            showNotification('success', 'Account details retrieved successfully.');

            document.getElementById('card-account-type').textContent = res.data.accountType || 'Savings Account';
            document.getElementById('card-acc-number').textContent = res.data.accountNumber;
            document.getElementById('card-holder-name').textContent = res.data.accountHolderName;
            document.getElementById('card-balance').textContent = formatCurrency(res.data.balance);
            document.getElementById('card-daily-limit').textContent = formatCurrency(res.data.dailyWithdrawalLimit);
            document.getElementById('card-withdrawn-today').textContent = formatCurrency(res.data.amountWithdrawnToday);
            document.getElementById('card-remaining-limit').textContent = formatCurrency(res.data.remainingDailyLimit);

            document.getElementById('info-result-card').style.display = 'block';
            updateDashboardUI();
        } else {
            document.getElementById('info-result-card').style.display = 'none';
            showNotification('error', res.message || 'Account not found.');
        }
    } catch (err) {
        showNotification('error', 'Network or server error: ' + err.message);
    } finally {
        btn.disabled = false;
        btn.innerHTML = '<span>View Information</span>';
    }
}

/* -------------------------------------------------------------
 * OPERATION 6: DAILY WITHDRAWAL LIMIT LOOKUP
 * ------------------------------------------------------------- */
async function handleLimitLookup(event) {
    event.preventDefault();
    hideNotification();

    const accInput = document.getElementById('limit-account-number');
    const btn = document.getElementById('btn-limit-lookup');
    const accNum = accInput.value.trim();

    if (!accNum) {
        showNotification('error', 'Account number cannot be empty.');
        return;
    }

    try {
        btn.disabled = true;
        btn.innerHTML = '<span>Checking...</span>';

        const response = await fetch(`/api/accounts/${encodeURIComponent(accNum)}`);
        const res = await response.json();

        if (res.success && res.data) {
            currentAccount = res.data;
            showNotification('success', `Withdrawal limit status loaded for Account #${accNum}.`);
            updateLimitPaneUI(res.data);
            updateDashboardUI();
        } else {
            showNotification('error', res.message || 'Account not found.');
        }
    } catch (err) {
        showNotification('error', 'Network or server error: ' + err.message);
    } finally {
        btn.disabled = false;
        btn.innerHTML = '<span>Check Withdrawal Limit</span>';
    }
}

/* -------------------------------------------------------------
 * HELPER: FETCH ACCOUNT DETAILS SILENTLY OR NOTIFY
 * ------------------------------------------------------------- */
async function fetchAccountDetails(accNum, notify = false) {
    try {
        const response = await fetch(`/api/accounts/${encodeURIComponent(accNum)}`);
        const res = await response.json();
        if (res.success && res.data) {
            currentAccount = res.data;
            updateDashboardUI();
            prefillAccountInputs(accNum);
            if (notify) {
                showNotification('info', `Switched active account to #${accNum} (${res.data.accountHolderName}).`);
            }
        }
    } catch (err) {
        console.error('Failed to fetch account details:', err);
    }
}
