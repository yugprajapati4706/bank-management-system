# Bank Account Management System

Bank Account Management System developed using pure java (without any external library/frameworks) as part of the project for the Java Course, VITyarthi "Build Your Own Project".
## Description

This project is a simulation of a real time banking system of a small bank. It enables a bank to interact with the customer and interact with their accounts, to accept deposits and withdrawals, and to transfer funds between accounts, and keeps a record of transactions.

## Key Features

- Creation and management of customers
- Establishment of S/C accounts and keeping data on S/C accounts
- Deposit Processing, Withdrawal Processing (PIN Protected) and transfers between accounts
The number of months to calculate the interest on a savings account.The frequency with which interest is calculated on a savings account.
Create account statements.Make statements of accounts.
- Total balance of the bank, most frequent account and total number of transactions calculation.
Custom checked exceptions for insufficient funds, invalid accounts, invalid amounts and authentication errors.
- Current account overdraft protection
- Minimum balance requirement for savings accounts

- History of accounts and customers and transactions persisted between runs by storing in files
## Technologies / Tools Used
The Java 21 edition builds on the previous standard edition with support for the java.io, java.util, java.util.concurrent.atomic, java.time, and java.util.stream libraries.Java 21 is the edition focusing on the java.io, java.util, java.util.concurrent.atomic, java.time, and java.util.stream libraries of the standard library.
- No external dependencies
## Project Structure
```
BankManagementSystem/
├── src/

│  ├── Main.java
│  └── bank/
│    ├── Account.java
│    ├── SavingsAccount.java
│    ├── CurrentAccount.java
│    ├── Customer.java
│    ├── Transaction.java
│    ├── TransactionType.java
│    ├── Bank.java
│    ├── FileStorage.java
│    ├── BankException.java
│    ├── InsufficientFundsException.java
│    ├── InvalidAccountException.java
│    ├── InvalidAmountException.java
│    └── AuthenticationException.java
├── data/
├── README.md
└── statement.md
```
## How to Install and Run
Install Java 21+ (JDK): See which version of java is installed by running java -version in terminal/command prompt.
2. Go into the project folder:
```
cd BankManagementSystem
```
3. Compile:
```
javac -d out src/bank/.java src/Main.java
```
4. Run:
```
java -cp out Main
```
5. The directory 'data' will be created on the first run.
## How to Test
The project was hand-tested with a series of commands being executed in the console to ensure that all features are functioning properly. The following steps have been carried out:
Opened two accounts for customers (1 Savings, 1 Current account)
2. Deposited some money into the Savings account and confirmed that the money appears in the account.
3. Tried to take out more money from the Current account than there was and checked that the Current account had insufficient funds to cover the withdrawal.
3. Transferred funds from one account to the other and ensured that the amounts were correctly updated
6. Checked the statement and made sure transactions were listed
7. Checked that the monthly interest was applied on the Savings account
Froze Account and tried to withdraw money from account (to confirm that this was not successful because the account was frozen)
Unfrozen the account and then went back to withdraw money again and saw if the withdrawal was successful.
Closed program and re-opened to ensure all accounts, customers and transaction history were populated properly
10. Ensured new accounts opened after last run had unique account number
The application can be tested using a script that contains a set of commands.
Example:
```
java -cp out Main < test_session.txt
```


