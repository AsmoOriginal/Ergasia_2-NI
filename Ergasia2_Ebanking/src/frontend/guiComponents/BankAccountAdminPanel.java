package frontend.guiComponents;

import backend.manager.AccountManager;
import backend.manager.StatementManager;
import backend.model.account.Account;
import backend.model.statement.AccountStatement;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class BankAccountAdminPanel extends JPanel {

    private MainWindow mainWindow;
    private AccountManager accountManager;
    private StatementManager statementManager;
    private List<Account> accounts;

    private JTextArea outputArea;
    private JTextField searchField;

    public BankAccountAdminPanel(MainWindow mainWindow,
                                  AccountManager accountManager,
                                  StatementManager statementManager,
                                  List<Account> accounts) {
        this.mainWindow = mainWindow;
        this.accountManager = accountManager;
        this.statementManager = statementManager;
        this.accounts = accounts;

        setLayout(new BorderLayout(10, 10));

        
        JLabel title = new JLabel("Bank Account Administration", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

       
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

      
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    
        JButton showAccountsBtn = new JButton("Show All Bank Accounts");
        showAccountsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        showAccountsBtn.addActionListener(e -> showBankAccounts());
        bottomPanel.add(showAccountsBtn);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));

     
        JPanel infoPanel = new JPanel(new BorderLayout(5, 5));
        searchField = new JTextField();
        JButton accountInfoBtn = new JButton("Show Account Info");
        accountInfoBtn.addActionListener(e -> showBankAccountInfo());
        infoPanel.add(searchField, BorderLayout.CENTER);
        infoPanel.add(accountInfoBtn, BorderLayout.EAST);
        bottomPanel.add(infoPanel);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));

      
        JButton statementsBtn = new JButton("Show Account Statements");
        statementsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        statementsBtn.addActionListener(e -> showBankAccountStatements());
        bottomPanel.add(statementsBtn);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));

      
        JButton backBtn = new JButton("Back");
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> mainWindow.showPanel("adminMenu"));
        bottomPanel.add(backBtn);

     
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void showBankAccounts() {
        StringBuilder sb = new StringBuilder();
        if (accounts == null || accounts.isEmpty()) {
            sb.append("No accounts found.\n");
        } else {
            sb.append("List of Bank Accounts:\n");
            for (Account acc : accounts) {
                String iban = "", owner = "", balance = "", type = "";

                for (String field : acc.marshal().split(",")) {
                    String[] keyValue = field.split(":", 2);
                    if (keyValue.length == 2) {
                        String key = keyValue[0].trim().toLowerCase();
                        String value = keyValue[1].trim();
                        switch (key) {
                            case "iban" -> iban = value;
                            case "primaryowner" -> owner = value;
                            case "balance" -> balance = new BigDecimal(value)
                                    .setScale(2, RoundingMode.HALF_UP).toString();
                            case "type" -> type = value;
                        }
                    }
                }

                sb.append(String.format("- IBAN: %-22s | Balance: %-10s | Owner VAT: %-10s | Type: %s%n",
                        iban, balance, owner, type));
            }
        }
        outputArea.setText(sb.toString());
    }

    private void showBankAccountInfo() {
        String iban = searchField.getText().trim();
        if (iban.isEmpty()) {
            outputArea.setText("Please enter an IBAN.");
            return;
        }

        Account acc = accountManager.getAccountByIban(iban);
        if (acc == null) {
            outputArea.setText("Account not found.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Account Info:\n");
        for (String field : acc.marshal().split(",")) {
            String[] keyValue = field.split(":", 2);
            if (keyValue.length == 2) {
                sb.append(String.format("%-15s: %s%n", keyValue[0].trim(), keyValue[1].trim()));
            }
        }

        outputArea.setText(sb.toString());
    }

    private void showBankAccountStatements() {
        String iban = searchField.getText().trim();
        if (iban.isEmpty()) {
            outputArea.setText("Please enter an IBAN.");
            return;
        }

        Account acc = accountManager.getAccountByIban(iban);
        if (acc == null) {
            outputArea.setText("Account not found.");
            return;
        }

        List<AccountStatement> statements = statementManager.findStatementsByIban(iban);
        if (statements == null || statements.isEmpty()) {
            outputArea.setText("No statements found for this account.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Statements for IBAN: ").append(iban).append("\n");
        for (AccountStatement stmt : statements) {
            sb.append(stmt.marshal()).append("\n");
        }

        outputArea.setText(sb.toString());
    }
}
