// FileStorage.java
package bank;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles reading and writing the bank data to simple text files.
 * Keeps the Bank class free from file I/O details.
 */
public class FileStorage {

    private final String dataDir;

    public FileStorage(String dataDir) {
        this.dataDir = dataDir;
        new File(dataDir).mkdirs();
    }

    private File accountsFile() {
        return new File(dataDir, "accounts.txt");
    }

    private File customersFile() {
        return new File(dataDir, "customers.txt");
    }

    private File transactionsFile() {
        return new File(dataDir, "transactions.txt");
    }

    public Map<String, Account> loadAccounts() throws IOException {
        Map<String, Account> accounts = new LinkedHashMap<>();
        File f = accountsFile();
        if (!f.exists()) return accounts;

        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                Account acc = parseAccountLine(line);
                accounts.put(acc.getAccountNumber(), acc);
            }
        }
        return accounts;
    }

    private Account parseAccountLine(String line) {
        String[] p = line.split("\\|", -1);

        String type = p[0];
        String accNum = p[1];
        String custId = p[2];
        String name = p[3];
        String pin = p[4];
        double balance = Double.parseDouble(p[5]);
        Account.Status status = Account.Status.valueOf(p[6]);

        if (type.equals("SAVINGS")) {
            double minBalance = Double.parseDouble(p[7]);
            double rate = Double.parseDouble(p[8]);
            return new SavingsAccount(accNum, custId, name, pin, balance, status, minBalance, rate);
        } else if (type.equals("CURRENT")) {
            double overdraft = Double.parseDouble(p[7]);
            return new CurrentAccount(accNum, custId, name, pin, balance, status, overdraft);
        }

        throw new IllegalArgumentException("Unknown account type in data file: " + type);
    }

    public void saveAccounts(Map<String, Account> accounts) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(accountsFile()))) {
            for (Account acc : accounts.values()) {
                writer.write(acc.toDataLine());
                writer.newLine();
            }
        }
    }

    public Map<String, Customer> loadCustomers() throws IOException {
        Map<String, Customer> customers = new LinkedHashMap<>();
        File f = customersFile();
        if (!f.exists()) return customers;

        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split("\\|", -1);
                Customer c = new Customer(p[0], p[1], p[2]);
                if (p.length > 3 && !p[3].isBlank()) {
                    for (String accNum : p[3].split(",")) {
                        c.linkAccount(accNum);
                    }
                }
                customers.put(c.getCustomerId(), c);
            }
        }
        return customers;
    }

    public void saveCustomers(Map<String, Customer> customers) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(customersFile()))) {
            for (Customer c : customers.values()) {
                writer.write(c.toDataLine());
                writer.newLine();
            }
        }
    }

    public List<Transaction> loadTransactions() throws IOException {
        List<Transaction> transactions = new ArrayList<>();
        File f = transactionsFile();
        if (!f.exists()) return transactions;

        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                transactions.add(Transaction.fromDataLine(line));
            }
        }
        return transactions;
    }

    // just append, don't rewrite the whole file every time
    public void appendTransaction(Transaction t) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(transactionsFile(), true))) {
            writer.write(t.toDataLine());
            writer.newLine();
        }
    }
}