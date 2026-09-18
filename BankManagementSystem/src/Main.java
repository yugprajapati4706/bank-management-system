import bank.Account;
import bank.Bank;
import bank.BankException;
import bank.Customer;
import bank.Transaction;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Console menu for the banking system.
 * This is just the front-end. All logic is in the bank package.
 */
public class Main {

    static Scanner sc = new Scanner(System.in);
    static Bank bank = new Bank("data");

    public static void main(String[] args) {

        // load previous data if available
        try {
            bank.loadAll();
            System.out.println("Loaded existing bank data from ./data");
        } catch (IOException e) {
            System.out.println("No existing data found, starting fresh.");
        }

        boolean running = true;

        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();

            try {
                if (choice.equals("1")) {
                    createCustomer();
                } else if (choice.equals("2")) {
                    openAccount();
                } else if (choice.equals("3")) {
                    deposit();
                } else if (choice.equals("4")) {
                    withdraw();
                } else if (choice.equals("5")) {
                    transfer();
                } else if (choice.equals("6")) {
                    viewStatement();
                } else if (choice.equals("7")) {
                    listAllAccounts();
                } else if (choice.equals("8")) {
                    showAnalytics();
                } else if (choice.equals("9")) {
                    applyInterest();
                } else if (choice.equals("10")) {
                    freezeAccount(true);
                } else if (choice.equals("11")) {
                    freezeAccount(false);
                } else if (choice.equals("0")) {
                    saveAndExit();
                    running = false;
                } else {
                    System.out.println("Invalid option, try again.");
                }
            } catch (BankException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Error: please enter a valid number.");
            }
        }

        sc.close();
    }

    static void printMenu() {
        System.out.println();
        System.out.println("===== Bank Account Management System =====");
        System.out.println(" 1. Create customer");
        System.out.println(" 2. Open account (Savings/Current)");
        System.out.println(" 3. Deposit");
        System.out.println(" 4. Withdraw");
        System.out.println(" 5. Transfer");
        System.out.println(" 6. View account statement");
        System.out.println(" 7. List all accounts");
        System.out.println(" 8. Bank analytics");
        System.out.println(" 9. Apply monthly interest");
        System.out.println("10. Freeze account");
        System.out.println("11. Unfreeze account");
        System.out.println(" 0. Save & exit");
        System.out.print("Choose an option: ");
    }

    static void createCustomer() {
        System.out.print("Customer name: ");
        String name = sc.nextLine().trim();

        System.out.print("Phone number: ");
        String phone = sc.nextLine().trim();

        Customer c = bank.createCustomer(name, phone);
        System.out.println("Created " + c);
    }

    static void openAccount() throws BankException {
        System.out.print("Customer ID (e.g. CUST1000): ");
        String custId = sc.nextLine().trim();

        System.out.print("Account type (S = Savings, C = Current): ");
        String type = sc.nextLine().trim().toUpperCase();

        System.out.print("4-digit PIN: ");
        String pin = sc.nextLine().trim();

        System.out.print("Opening deposit amount: ");
        double amount = Double.parseDouble(sc.nextLine().trim());

        Account acc;
        if (type.equals("S")) {
            acc = bank.openSavingsAccount(custId, pin, amount);
        } else {
            // treat anything else as Current for simplicity
            acc = bank.openCurrentAccount(custId, pin, amount);
        }

        System.out.println("Opened: " + acc);
    }

    static void deposit() throws BankException {
        System.out.print("Account number: ");
        String accNo = sc.nextLine().trim();

        System.out.print("Amount: ");
        double amount = Double.parseDouble(sc.nextLine().trim());

        System.out.print("Description (optional): ");
        String desc = sc.nextLine().trim();

        Transaction t = bank.deposit(accNo, amount, desc);
        System.out.println("Deposit successful. " + t);
    }

    static void withdraw() throws BankException {
        System.out.print("Account number: ");
        String accNo = sc.nextLine().trim();

        System.out.print("PIN: ");
        String pin = sc.nextLine().trim();

        System.out.print("Amount: ");
        double amount = Double.parseDouble(sc.nextLine().trim());

        System.out.print("Description (optional): ");
        String desc = sc.nextLine().trim();

        Transaction t = bank.withdraw(accNo, pin, amount, desc);
        System.out.println("Withdrawal successful. " + t);
    }

    static void transfer() throws BankException {
        System.out.print("From account: ");
        String from = sc.nextLine().trim();

        System.out.print("PIN: ");
        String pin = sc.nextLine().trim();

        System.out.print("To account: ");
        String to = sc.nextLine().trim();

        System.out.print("Amount: ");
        double amount = Double.parseDouble(sc.nextLine().trim());

        bank.transfer(from, pin, to, amount);
        System.out.println("Transfer successful.");
    }

    static void viewStatement() throws BankException {
        System.out.print("Account number: ");
        String accNo = sc.nextLine().trim();

        // check if account exists first
        bank.findAccount(accNo);

        List<Transaction> list = bank.getStatement(accNo);

        if (list.isEmpty()) {
            System.out.println("No transactions yet for this account.");
            return;
        }

        System.out.println("---- Statement for " + accNo + " ----");
        for (Transaction t : list) {
            System.out.println(t);
        }
    }

    static void listAllAccounts() {
        List<Account> accounts = bank.listAccounts();

        if (accounts.isEmpty()) {
            System.out.println("No accounts yet.");
            return;
        }

        for (Account a : accounts) {
            System.out.println(a);
        }
    }

    static void showAnalytics() {
        System.out.println("Total accounts: " + bank.listAccounts().size());
        System.out.printf("Total bank balance: %.2f%n", bank.getTotalBankBalance());
        System.out.println("Total transactions recorded: " + bank.getTotalTransactionCount());

        Account mostActive = bank.getMostActiveAccount();
        if (mostActive == null) {
            System.out.println("Most active account: N/A");
        } else {
            System.out.println("Most active account: " + mostActive);
        }
    }

    static void applyInterest() {
        Map<String, Double> result = bank.applyMonthlyInterestToAll();

        if (result.isEmpty()) {
            System.out.println("No savings accounts to credit interest to.");
            return;
        }

        for (Map.Entry<String, Double> entry : result.entrySet()) {
            System.out.printf("Credited %.2f interest to %s%n", entry.getValue(), entry.getKey());
        }
    }

    static void freezeAccount(boolean freeze) throws BankException {
        System.out.print("Account number: ");
        String accNo = sc.nextLine().trim();

        if (freeze) {
            bank.freezeAccount(accNo);
            System.out.println("Account " + accNo + " frozen.");
        } else {
            bank.unfreezeAccount(accNo);
            System.out.println("Account " + accNo + " unfrozen.");
        }
    }

    static void saveAndExit() {
        try {
            bank.saveAll();
            System.out.println("Data saved. Goodbye!");
        } catch (IOException e) {
            System.out.println("Warning: failed to save data — " + e.getMessage());
        }
    }
}
