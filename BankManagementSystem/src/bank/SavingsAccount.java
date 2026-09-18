// SavingsAccount.java
package bank;

/**
 * Savings account – keeps a minimum balance and earns monthly interest.
 */
public class SavingsAccount extends Account {

    public static final double DEFAULT_MIN_BALANCE = 500.0;
    public static final double DEFAULT_INTEREST_RATE = 0.035; // 3.5% annual

    private final double minBalance;
    private final double annualInterestRate;

    public SavingsAccount(String accountNumber, String customerId, String holderName,
                          String pin, double balance, Status status,
                          double minBalance, double annualInterestRate) {
        super(accountNumber, customerId, holderName, pin, balance, status);
        this.minBalance = minBalance;
        this.annualInterestRate = annualInterestRate;
    }

    @Override
    protected boolean canWithdraw(double amount) {
        return getBalance() - amount >= minBalance;
    }

    @Override
    public double applyMonthlyInterest() {
        double monthlyRate = annualInterestRate / 12.0;
        double interest = getBalance() * monthlyRate;
        credit(interest);
        return interest;
    }

    @Override
    public String getAccountType() {
        return "SAVINGS";
    }

    public double getMinBalance() {
        return minBalance;
    }

    public double getAnnualInterestRate() {
        return annualInterestRate;
    }

    @Override
    public String toDataLine() {
        // TYPE|ACCNUM|CUSTID|NAME|PIN|BALANCE|STATUS|MINBALANCE|RATE
        return String.join("|",
                "SAVINGS",
                getAccountNumber(),
                getCustomerId(),
                getHolderName(),
                getPinRaw(),
                String.valueOf(getBalance()),
                getStatus().name(),
                String.valueOf(minBalance),
                String.valueOf(annualInterestRate));
    }
}
