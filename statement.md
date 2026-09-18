## Problem Statement

Customer accounts and transactions recorded in a manual or spreadsheet system
Is prone to error and lacks validation, audit trail and reporting ability.
A small bank (or a project in a course to simulate a bank) must have a simple and reliable
Social media system to handle consumer accounts, process transactions safely and securely, and
Keep a record of transactions – not relying on third-party services.
Infrastructure such as a database server.

## Scope of the Project

This project is a “Bank Account Management System” with console (CLI) interface.
in pure Java. It covers:

- Creating and managing customers and their accounts (Savings and Current)
- Funding with validation, withdrawal and transfer of funds.
- PIN authentication for sensitive operations: (withdraw, transfer)
- Freezing/unfreezing accounts
Interest applied monthly on savings accounts.
- Making a note of each transaction and issuing a statement per account
- Account activity by basic bank level (total balance, most active account)
- State persistence in plain text files (all data persists after a restart)

Out of scope: graphical or web UI, multi user concurrent access, real time.
banking network integration, and real currency/regulatory compliance —
This is an academic representation of fundamental banking traits.

## Target Users

The system is designed to be used by bank staff/tellers on behalf of bank customers (as
  (Modelled after the console menu)
- Educators/instructors assessing against the course's
  Functional vs. non-functional requirements

## High-Level Features

This involves the ability to open, view, freeze/unfreeze Savings and.
   Each of the following types of current account has different rules, such as minimum balance versus
   overdraft limit)
2. Transaction Processing (2) — deposit, withdraw and transfer money with
   full validation and custom exception handling
3. Transaction History & Reporting — per account statements, interest
   The software includes the tools for crediting, as well as for bank-wide analytics.
