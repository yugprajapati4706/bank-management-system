// Transaction.java
package bank;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * One completed transaction. Immutable so we don't accidentally change history.
 */
public final class Transaction {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String transactionId;
    private final String accountNumber;
    private final TransactionType type;
    private final double amount;
    private final double balanceAfter;
    private final LocalDateTime timestamp;
    private final String description;

    public Transaction(String transactionId, String accountNumber, TransactionType type,
                       double amount, double balanceAfter, LocalDateTime timestamp, String description) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = timestamp;
        this.description = description;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    public String toDataLine() {
        return String.join("|",
                transactionId,
                accountNumber,
                type.name(),
                String.valueOf(amount),
                String.valueOf(balanceAfter),
                timestamp.format(FMT),
                description);
    }

    public static Transaction fromDataLine(String line) {
        String[] p = line.split("\\|", -1);
        return new Transaction(
                p[0], p[1],
                TransactionType.valueOf(p[2]),
                Double.parseDouble(p[3]),
                Double.parseDouble(p[4]),
                LocalDateTime.parse(p[5], FMT),
                p[6]);
    }

    @Override
    public String toString() {
        return String.format("%-19s | %-12s | %-6s | %10.2f | Bal: %10.2f | %s",
                timestamp.format(FMT), accountNumber, type, amount, balanceAfter, description);
    }
}
