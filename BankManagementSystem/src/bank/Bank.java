// Bank.java
package bank;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Main engine of the bank.
 * Keeps everything in memory and talks to FileStorage for persistence.
 * Main class only talks to this.
 */
public class Bank {

    private final Map<String, Account> accounts = new LinkedHashMap<>();
    private final Map<String, Customer> customers = new LinkedHashMap<>();
    private final List<Transaction> transactionLog = new ArrayList<>();
    private final FileStorage storage;

    // simple counters for generating IDs
    private final AtomicInteger customerSeq = new AtomicInteger(1000);
    private final AtomicInteger accountSeq = new AtomicInteger(100000);
    private final AtomicInteger txnSeq = new AtomicInteger(1);

    public Bank(String dataDir) {
        this.storage = new FileStorage(dataDir);
    }

    // ---------- load / save ----------

    public void loadAll() throws IOException {
        accounts.clear();
        accounts.putAll(storage.loadAccounts());

        customers.clear();
        customers.putAll(storage.loadCustomers());

        transactionLog.clear();
        transactionLog.addAll(storage.loadTransactions());

        // make sure the sequence numbers continue from where we left off
        recalibrateSequences();
    }

    public void saveAll() throws IOException {
        storage.saveAccounts(accounts);
        storage.saveCustomers(customers);
        // transactions are already written one-by-one
    }

    private void recalibrateSequences() {
        for (String id : customers.keySet()) {
            tryBumpSeq(customerSeq, id, "CUST");
        }
        for (String id : accounts.keySet()) {
            tryBumpSeq(accountSeq, id, "ACC");
        }
        for (Transaction t : transactionLog) {
            tryBumpSeq(txnSeq, t.getTransactionId(), "TXN");
        }
    }

    private void tryBumpSeq(AtomicInteger seq, String id, String prefix) {
        if (id.startsWith(prefix)) {
            try {
                int n = Integer.parseInt(id.substring(prefix.length()));
                seq.updateAndGet(cur -> Math.max(cur, n + 1));
            } catch (NumberFormatException ignored) {
                // ignore weird IDs
            }
        }
    }

    // ---------- customers ----------

    public Customer createCustomer(String name, String phone) {
        String id = "CUST" + customerSeq.getAndIncrement();
        Customer c = new Customer(id, name, phone);
        customers.put(id, c);
        return c;
    }

    public Customer findCustomer(String customerId) throws InvalidAccountException {
        Customer c = customers.get(customerId);
        if (c == null) {
            throw new InvalidAccountException("No customer found with ID " + customerId);
        }
        return c;
    }

    public List<Customer> listCustomers() {
        return new ArrayList<>(customers.values());
    }

    // ---------- accounts ----------

    public Account openSavingsAccount(String customerId, String pin, double openingDeposit)
            throws InvalidAccountException, InvalidAmountException {

        Customer c = findCustomer(customerId);

        if (openingDeposit < SavingsAccount.DEFAULT_MIN_BALANCE) {
            throw new InvalidAmountException(
                    "Opening deposit must be at least "
                    + SavingsAccount.DEFAULT_MIN_BALANCE + " for a savings account.");
        }

        String accNum = "ACC" + accountSeq.getAndIncrement();
        Account acc = new SavingsAccount(
                accNum, customerId, c.getName(), pin, openingDeposit,
                Account.Status.ACTIVE,
                SavingsAccount.DEFAULT_MIN_BALANCE,
                SavingsAccount.DEFAULT_INTEREST_RATE);

        accounts.put(accNum, acc);
        c.linkAccount(accNum);
        return acc;
    }

    public Account openCurrentAccount(String customerId, String pin, double openingDeposit)
            throws InvalidAccountException, InvalidAmountException {

        Customer c = findCustomer(customerId);

        if (openingDeposit < 0) {
            throw new InvalidAmountException("Opening deposit cannot be negative.");
        }

        String accNum = "ACC" + accountSeq.getAndIncrement();
        Account acc = new CurrentAccount(
                accNum, customerId, c.getName(), pin, openingDeposit,
                Account.Status.ACTIVE,
                CurrentAccount.DEFAULT_OVERDRAFT_LIMIT);

        accounts.put(accNum, acc);
        c.linkAccount(accNum);
        return acc;
    }

    public Account findAccount(String accountNumber) throws InvalidAccountException {
        Account acc = accounts.get(accountNumber);
        if (acc == null) {
            throw new InvalidAccountException("No account found with number " + accountNumber);
        }
        return acc;
    }

    public List<Account> listAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public List<Account> listAccountsForCustomer(String customerId) {
        return accounts.values().stream()
                .filter(a -> a.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    private void authenticate(Account acc, String pin) throws AuthenticationException, BankException {
        if (acc.getStatus() == Account.Status.FROZEN) {
            throw new BankException("Account " + acc.getAccountNumber() + " is frozen. Contact the bank.");
        }
        if (!acc.checkPin(pin)) {
            throw new AuthenticationException("Incorrect PIN for account " + acc.getAccountNumber());
        }
    }

    public void freezeAccount(String accountNumber) throws InvalidAccountException {
        findAccount(accountNumber).setStatus(Account.Status.FROZEN);
    }

    public void unfreezeAccount(String accountNumber) throws InvalidAccountException {
        findAccount(accountNumber).setStatus(Account.Status.ACTIVE);
    }

    // ---------- money movement ----------

    public Transaction deposit(String accountNumber, double amount, String description)
            throws BankException {

        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be positive.");
        }

        Account acc = findAccount(accountNumber);
        if (acc.getStatus() == Account.Status.FROZEN) {
            throw new BankException("Account " + accountNumber + " is frozen. Contact the bank.");
        }

        acc.credit(amount);
        return logTransaction(accountNumber, TransactionType.DEPOSIT, amount, acc.getBalance(), description);
    }

    public Transaction withdraw(String accountNumber, String pin, double amount, String description)
            throws BankException {

        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive.");
        }

        Account acc = findAccount(accountNumber);
        authenticate(acc, pin);
        acc.debit(amount);
        return logTransaction(accountNumber, TransactionType.WITHDRAW, amount, acc.getBalance(), description);
    }

    public void transfer(String fromAccountNumber, String pin, String toAccountNumber, double amount)
            throws BankException {

        if (amount <= 0) {
            throw new InvalidAmountException("Transfer amount must be positive.");
        }
        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new InvalidAccountException("Cannot transfer to the same account.");
        }

        Account from = findAccount(fromAccountNumber);
        Account to = findAccount(toAccountNumber);

        authenticate(from, pin);

        if (to.getStatus() == Account.Status.FROZEN) {
            throw new BankException("Destination account " + toAccountNumber + " is frozen.");
        }

        // debit first so we don't create money out of thin air
        from.debit(amount);
        to.credit(amount);

        logTransaction(fromAccountNumber, TransactionType.TRANSFER_OUT, amount, from.getBalance(),
                "Transfer to " + toAccountNumber);
        logTransaction(toAccountNumber, TransactionType.TRANSFER_IN, amount, to.getBalance(),
                "Transfer from " + fromAccountNumber);
    }

    public Map<String, Double> applyMonthlyInterestToAll() {
        Map<String, Double> credited = new LinkedHashMap<>();
        for (Account acc : accounts.values()) {
            double interest = acc.applyMonthlyInterest();
            if (interest > 0) {
                credited.put(acc.getAccountNumber(), interest);
                logTransaction(acc.getAccountNumber(), TransactionType.INTEREST_CREDIT,
                        interest, acc.getBalance(), "Monthly interest credit");
            }
        }
        return credited;
    }

    private Transaction logTransaction(String accountNumber, TransactionType type,
                                       double amount, double balanceAfter, String description) {

        String txnId = "TXN" + txnSeq.getAndIncrement();
        Transaction t = new Transaction(txnId, accountNumber, type, amount, balanceAfter,
                LocalDateTime.now(), description == null ? "" : description);

        transactionLog.add(t);

        try {
            storage.appendTransaction(t);
        } catch (IOException e) {
            // don't crash the whole transaction just because the log file failed
            System.err.println("Warning: could not write transaction to log file: " + e.getMessage());
        }
        return t;
    }

    // ---------- reports ----------

    public List<Transaction> getStatement(String accountNumber) {
        return transactionLog.stream()
                .filter(t -> t.getAccountNumber().equals(accountNumber))
                .collect(Collectors.toList());
    }

    public double getTotalBankBalance() {
        return accounts.values().stream().mapToDouble(Account::getBalance).sum();
    }

    public Account getMostActiveAccount() {
        Map<String, Long> counts = transactionLog.stream()
                .collect(Collectors.groupingBy(Transaction::getAccountNumber, Collectors.counting()));

        return counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> accounts.get(e.getKey()))
                .orElse(null);
    }

    public int getTotalTransactionCount() {
        return transactionLog.size();
    }
}