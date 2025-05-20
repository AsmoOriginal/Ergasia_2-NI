package backend.ui;

import backend.model.account.Account;
import backend.model.statement.AccountStatement;
import backend.manager.AccountManager;
import backend.manager.StatementManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Scanner;

public class BankAccountAdminMenu {

    private List<Account> accounts;
    private AccountManager accountManager;
    private StatementManager accountStatementManager;
    
    public BankAccountAdminMenu(List<Account> accounts, AccountManager accountManager, StatementManager accountStatementManager) {
        this.accounts = accounts;
        this.accountManager = accountManager;
        
       
    }

    public void show() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Bank Accounts Admin Menu ===");
            System.out.println("1. Show Bank Accounts");
            System.out.println("2. Show Bank Account Info");
            System.out.println("3. Show Bank Account Statements");
            System.out.println("0. Back");
            System.out.print("Select option: ");
            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    showBankAccounts();
                    break;
                case "2":
                    showBankAccountInfo(scanner);
                    break;
                case "3":
                    showBankAccountStatements(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void showBankAccounts() {
        if (accounts == null || accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }

        System.out.println("\nList of Bank Accounts:");
        for (Account account : accounts) {
            String[] fields = account.marshal().split(",");
            String iban = "", owner = "", balance = "", type = "";

            for (String field : fields) {
                String[] keyValue = field.split(":", 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().toLowerCase();
                    String value = keyValue[1].trim();

                    switch (key) {
                        case "iban" -> iban = value;
                        case "primaryowner" -> owner = value;
                        case "balance" -> balance = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toString();
                        case "type" -> type = value;
                    }
                }
            }

            System.out.printf("- IBAN: %-22s | Balance: %-10s | Owner VAT: %-10s | Type: %s%n",
                              iban, balance, owner, type);
        }
    }


    private void showBankAccountInfo(Scanner scanner) {
        System.out.print("Enter IBAN: ");
        String iban = scanner.nextLine().trim();

        Account acc = accountManager.getAccountByIban(iban);
        if (acc == null) {
            System.out.println("Account not found.");
            return;
        }

        System.out.println("\nAccount Info:");
        printKeyValueCSV(acc.marshal());
    }

    private void showBankAccountStatements(Scanner scanner) {
        System.out.print("Enter IBAN: ");
        String iban = scanner.nextLine().trim();

        Account acc = accountManager.getAccountByIban(iban);
        if (acc == null) {
            System.out.println("Account not found.");
            return;
        }

        List<AccountStatement> statements = accountStatementManager.findStatementsByIban(iban);

        if (statements == null || statements.isEmpty()) {
            System.out.println("No statements found for this account.");
            return;
        }

        System.out.println("\nAccount Statements for IBAN: " + iban);
        for (AccountStatement stmt : statements) {
            System.out.println(stmt.marshal());  // ή ό,τι μέθοδο έχεις για εμφάνιση
        }
    }
    
    private void printKeyValueCSV(String csvLine) {
        String[] fields = csvLine.split(",");
        for (String field : fields) {
            String[] keyValue = field.split(":", 2);
            if (keyValue.length == 2) {
                System.out.printf("%-15s: %s%n", keyValue[0].trim(), keyValue[1].trim());
            }
        }
    }
}
