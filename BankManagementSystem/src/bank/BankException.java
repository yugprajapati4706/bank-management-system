// BankException.java
package bank;

/**
 * Base exception for everything that can go wrong in the bank.
 * Made it checked so callers have to handle it.
 */
public class BankException extends Exception {
    public BankException(String message) {
        super(message);
    }
}
