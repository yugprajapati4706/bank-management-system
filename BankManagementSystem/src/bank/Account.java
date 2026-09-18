// Account.java
package bank;

/**
 * Base class for every account in the system.
 * Savings and Current both extend this.
 */
public abstract class Account {

    public enum Status { ACTIVE, FROZEN }

    private final String accountNumber;
    private final String customerId;
    private String holderName;
    private String pin;
    private double balance;
    private Status status;

    protected Account(String accountNumber, String customerId, String holderName,
                      String pin, double balance, Status status) {
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.holderName = holderName;
        this.pin = pin;
        this.balance = balance;
        this.status = status;
    }

    // ---------- getters ----------
    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public double getBalance() {
        return balance;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean checkPin(String candidate) {
        return pin != null && pin.equals(candidate);
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    // only used when writing to file
    String getPinRaw() {
        return pin;
    }

    // Bank is allowed to change the balance directly
    void credit(double amount) {
        balance += amount;
    }

    void debit(double amount) throws InsufficientFundsException {
        if (!canWithdraw(amount)) {
            throw new InsufficientFundsException(
                    "Insufficient funds in account " + accountNumber
                    + ". Available: " + String.format("%.2f", balance));
        }
        balance -= amount;
    }

    // each subclass decides its own withdrawal rules
    protected abstract boolean canWithdraw(double amount);

    // monthly interest / fee logic lives in the subclass
    public abstract double applyMonthlyInterest();

    public abstract String getAccountType();

    public abstract String toDataLine();

    @Override
    public String toString() {
        return String.format("[%s] %s | Holder: %s | Balance: %.2f | Status: %s",
                getAccountType(), accountNumber, holderName, balance, status);
    }
}