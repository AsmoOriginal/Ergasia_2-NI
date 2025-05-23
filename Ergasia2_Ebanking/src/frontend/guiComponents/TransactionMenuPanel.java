package frontend.guiComponents;

import backend.manager.AccountManager;
import backend.manager.BillManager;
import backend.manager.StatementManager;
import backend.manager.TransactionManager;
import backend.model.account.Account;
import backend.model.bill.Bill;
import backend.model.transaction.Deposit;
import backend.model.transaction.Payment;
import backend.model.transaction.Transaction;
import backend.model.transaction.Transfer;
import backend.model.transaction.Withdrawal;
import backend.model.user.Customer;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

public class TransactionMenuPanel extends JPanel {

    private final MainWindow mainWindow;
    private final Customer customer;
    private final Account account;
    private final TransactionManager transactionManager = TransactionManager.getInstance();
    private final AccountManager accountManager = AccountManager.getInstance();
    private final BillManager billManager = BillManager.getInstance();
    private final StatementManager statementManager = StatementManager.getInstance();

    public TransactionMenuPanel(MainWindow mainWindow, Customer customer, Account account) {
        this.mainWindow = mainWindow;
        this.customer = customer;
        this.account = account;

        setLayout(new BorderLayout());

        // title
        JLabel titleLabel = new JLabel("choose a transaction method", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);//helps have the buttons 

        // panel with buttons in horizontal orientation
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); 

        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdrawal");
        JButton transferButton = new JButton("Transfer");
        JButton payBillButton = new JButton("Pay Bill");
        JButton backButton = new JButton("Back");

        // Action Listeners
        depositButton.addActionListener(e -> handleDeposit(account));
        withdrawButton.addActionListener(e -> handleWithdraw(account));
        transferButton.addActionListener(e -> handleTransfer(account));
        payBillButton.addActionListener(e -> handlePayBill(account, customer));
        backButton.addActionListener(e -> goBackToCustomerMenu());

        // add buttons 
        buttonPanel.add(depositButton);
        buttonPanel.add(withdrawButton);
        buttonPanel.add(transferButton);
        buttonPanel.add(payBillButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.CENTER); 
    }

    private void handleDeposit(Account account) {
    	JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Deposit", true);
        dialog.setLayout(new GridLayout(3, 2, 10, 10));

        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField();

        JLabel depositorLabel = new JLabel("Depositor Name:");
        JTextField depositorField = new JTextField();

        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        submitButton.addActionListener(e -> {
            try {
                BigDecimal amount = new BigDecimal(amountField.getText().trim());
                String depositor = depositorField.getText().trim();

                Deposit deposit = new Deposit(account, account, amount, depositor);
                boolean success = TransactionManager.getInstance().executeDeposit(deposit);

                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Deposit successful!\nNew balance: " + account.getBalance() + "€");
                    StatementManager sm = StatementManager.getInstance();
                    sm.addTransactionToStatement(deposit);
                    sm.saveStatements(
                        sm.getStatementsForAccount(account.getIban()),
                        "data/statements/" + account.getIban() + ".csv"
                    );
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Deposit failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid amount.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(amountLabel);
        dialog.add(amountField);
        dialog.add(depositorLabel);
        dialog.add(depositorField);
        dialog.add(submitButton);
        dialog.add(cancelButton);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void handleWithdraw(Account account) {
    	JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Withdraw", true);
        dialog.setLayout(new GridLayout(3, 2, 30, 10));

        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField();

        JLabel methodLabel = new JLabel("Method:");
        JTextField methodField = new JTextField("ATM");

        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        submitButton.addActionListener(e -> {
            try {
                BigDecimal amount = new BigDecimal(amountField.getText().trim());
                String method = methodField.getText().trim();

                if (method.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please specify a withdrawal method.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Withdrawal withdrawal = new Withdrawal(account, amount, method);
                boolean success = TransactionManager.getInstance().executeWithdrawal(withdrawal);

                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Withdrawal successful!\nNew balance: " + account.getBalance() + "€");

                    StatementManager sm = StatementManager.getInstance();
                    sm.addTransactionToStatement(withdrawal);
                    sm.saveStatements(
                        sm.getStatementsForAccount(account.getIban()),
                        "data/statements/" + account.getIban() + ".csv"
                    );

                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Withdrawal failed. Check balance or data.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid amount.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(amountLabel);
        dialog.add(amountField);
        dialog.add(methodLabel);
        dialog.add(methodField);
        dialog.add(submitButton);
        dialog.add(cancelButton);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void handleTransfer(Account account) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Transfer Money", true);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));

        // Λήψη όλων των accounts εκτός του sender
        List<Account> allAccounts = AccountManager.getInstance().getAllAccounts();
        List<Account> possibleRecipients = allAccounts.stream()
                .filter(a -> !a.getIban().equals(account.getIban()))
                .collect(Collectors.toList());

        JLabel recipientLabel = new JLabel("Select Recipient:");
        JComboBox<String> recipientComboBox = new JComboBox<>();
        for (Account acc : possibleRecipients) {
            recipientComboBox.addItem(acc.getIban() + " - " + acc.getPrimaryOwner().getLegalName());
        }

        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField();

        JLabel senderNoteLabel = new JLabel("Sender Note:");
        JTextField senderNoteField = new JTextField();

        JLabel receiverNoteLabel = new JLabel("Receiver Note:");
        JTextField receiverNoteField = new JTextField();

        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        submitButton.addActionListener(e -> {
            String selectedItem = (String) recipientComboBox.getSelectedItem();
            if (selectedItem == null) {
                JOptionPane.showMessageDialog(dialog, "Please select a recipient.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String recipientIban = selectedItem.split(" - ")[0];
            String amountText = amountField.getText().trim();
            String senderNote = senderNoteField.getText().trim();
            String receiverNote = receiverNoteField.getText().trim();

            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter an amount.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Account recipientAccount = AccountManager.getInstance().getAccountByIban(recipientIban);
            if (recipientAccount == null) {
                JOptionPane.showMessageDialog(dialog, "Recipient account not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                BigDecimal amount = new BigDecimal(amountText);
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Amount must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Transfer transfer = new Transfer(account, recipientAccount, amount, senderNote, receiverNote);
                boolean success = TransactionManager.getInstance().executeTransfer(transfer);

                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Transfer successful!\nYour new balance: " + account.getBalance() + "€");

                    StatementManager sm = StatementManager.getInstance();
                    sm.addTransactionToStatement(transfer);
                    sm.saveStatements(sm.getStatementsForAccount(account.getIban()), "data/statements/" + account.getIban() + ".csv");
                    sm.saveStatements(sm.getStatementsForAccount(recipientAccount.getIban()), "data/statements/" + recipientAccount.getIban() + ".csv");

                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Transfer failed. Check balance or data.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid amount.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(recipientLabel);
        dialog.add(recipientComboBox);
        dialog.add(amountLabel);
        dialog.add(amountField);
        dialog.add(senderNoteLabel);
        dialog.add(senderNoteField);
        dialog.add(receiverNoteLabel);
        dialog.add(receiverNoteField);
        dialog.add(submitButton);
        dialog.add(cancelButton);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }



    private void handlePayBill(Account account, Customer customer) {
        List<Bill> allBills = billManager.loadBillsByCustomerVat("data/bills", customer);
        List<Bill> unpaidBills = new ArrayList<>();

        for (Bill bill : allBills) {
            if (!bill.isPaid()) unpaidBills.add(bill);
        }

        if (unpaidBills.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No unpaid bills found.");
            return;
        }

        String[] options = unpaidBills.stream()
            .map(b -> String.format("%s | %.2f€ | Due: %s", b.getIssuer().getIban(), b.getAmount(), b.getDueDate()))
            .toArray(String[]::new);

        String selection = (String) JOptionPane.showInputDialog(this,
                "Select a bill to pay:",
                "Unpaid Bills",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);

        if (selection != null) {
            int selectedIndex = -1;
            for (int i = 0; i < options.length; i++) {
                if (options[i].equals(selection)) {
                    selectedIndex = i;
                    break;
                }
            }

            if (selectedIndex != -1) {
                Bill selectedBill = unpaidBills.get(selectedIndex);
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Confirm payment of " + selectedBill.getAmount() + "€ to " + selectedBill.getIssuer().getIban() + "?",
                        "Confirm Payment",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    Payment payment = new Payment(account, selectedBill.getIssuer(), selectedBill.getAmount(), selectedBill);
                    if (transactionManager.executePayment(payment)) {
                        JOptionPane.showMessageDialog(this, "Payment successful! New balance: " + account.getBalance() + "€");
                        saveTransaction(payment);
                        billManager.markBillAsPaid(selectedBill, customer);
                    } else {
                        JOptionPane.showMessageDialog(this, "Payment failed!");
                    }
                }
            }
        }
    }

    private void saveTransaction(Transaction transaction) {
        StatementManager sm = StatementManager.getInstance();
        sm.addTransactionToStatement(transaction);
        sm.saveStatements(
                sm.getStatementsForAccount(account.getIban()),
                "data/statements/" + account.getIban() + ".csv"
        );
    }


    private void goBackToCustomerMenu() {
        mainWindow.showPanel("customerMenu");
    }
}
