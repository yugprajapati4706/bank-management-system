// CurrentAccount.java
package bank;

/**
 * Current account – no interest, but you can go into overdraft.
 */
public class CurrentAccount extends Account {

    public static final double DEFAULT_OVERDRAFT_LIMIT = 5000.0;

    private final double overdraftLimit;

    public CurrentAccount(String accountNumber, String customerId, String holderName,
                          String pin, double balance, Status status, double overdraftLimit) {
        super(accountNumber, customerId, holderName, pin, balance, status);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    protected boolean canWithdraw(double amount) {
        // allow going negative up to the overdraft limit
        return getBalance() - amount >= -overdraftLimit;
    }

    @Override
    public double applyMonthlyInterest() {
        // current accounts don't get interest
        return 0.0;
    }

    @Override
    public String getAccountType() {
        return "CURRENT";
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    @Override
    public String toDataLine() {
        // TYPE|ACCNUM|CUSTID|NAME|PIN|BALANCE|STATUS|OVERDRAFT
        return String.join("|",
                "CURRENT",
                getAccountNumber(),
                getCustomerId(),
                getHolderName(),
                getPinRaw(),
                String.valueOf(getBalance()),
                getStatus().name(),
                String.valueOf(overdraftLimit));
    }
}