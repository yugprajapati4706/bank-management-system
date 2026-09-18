// Customer.java
package bank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A customer can have multiple accounts.
 */
public class Customer {

    private final String customerId;
    private String name;
    private String phone;
    private final List<String> accountNumbers = new ArrayList<>();

    public Customer(String customerId, String name, String phone) {
        this.customerId = customerId;
        this.name = name;
        this.phone = phone;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getAccountNumbers() {
        return Collections.unmodifiableList(accountNumbers);
    }

    public void linkAccount(String accountNumber) {
        if (!accountNumbers.contains(accountNumber)) {
            accountNumbers.add(accountNumber);
        }
    }

    // format used when saving to file
    public String toDataLine() {
        return String.join("|", customerId, name, phone, String.join(",", accountNumbers));
    }

    @Override
    public String toString() {
        return String.format("Customer[%s] %s (%s) — %d account(s)",
                customerId, name, phone, accountNumbers.size());
    }
}