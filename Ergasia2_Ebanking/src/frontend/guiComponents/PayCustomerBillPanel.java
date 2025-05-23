package frontend.guiComponents;

import backend.manager.*;
import backend.model.account.Account;
import backend.model.bill.Bill;
import backend.model.transaction.Payment;
import backend.model.user.Customer;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class PayCustomerBillPanel extends JPanel {
    private MainWindow mainWindow;
    private UserManager userManager;
    private AccountManager accountManager;
    private StatementManager statementManager;
    private BillManager billManager;

    private JTextField vatField;
    private JTextArea outputArea;
    private JComboBox<String> billCombo;
    private JComboBox<String> accountCombo;

    private List<Bill> unpaidBills;
    private List<Account> customerAccounts;
    private Customer customer;

    public PayCustomerBillPanel(MainWindow mainWindow, UserManager userManager,
                                 AccountManager accountManager, StatementManager statementManager) {
        this.mainWindow = mainWindow;
        this.userManager = userManager;
        this.accountManager = accountManager;
        this.statementManager = statementManager;
        this.billManager = BillManager.getInstance();

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top Panel: VAT Input
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel vatLabel = new JLabel("Enter Customer VAT:");
        vatLabel.setFont(new Font("Arial", Font.BOLD, 14));
        vatField = new JTextField(15);
        JButton searchBtn = new JButton("Search Bills");

        topPanel.add(vatLabel);
        topPanel.add(vatField);
        topPanel.add(searchBtn);

        // Bill & Account Selection
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel billLabel = new JLabel("Select Unpaid Bill:");
        billLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        billCombo = new JComboBox<>();

        JLabel accountLabel = new JLabel("Select Account to Charge:");
        accountLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        accountCombo = new JComboBox<>();

        JButton payBtn = new JButton("Pay Selected Bill");

        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(billLabel, gbc);
        gbc.gridy = 1;
        centerPanel.add(billCombo, gbc);

        gbc.gridy = 2;
        centerPanel.add(accountLabel, gbc);
        gbc.gridy = 3;
        centerPanel.add(accountCombo, gbc);

        gbc.gridy = 4;
        centerPanel.add(payBtn, gbc);

       
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        outputArea = new JTextArea(8, 60);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setBorder(BorderFactory.createTitledBorder("System Output"));

        JButton backBtn = new JButton("Back to Admin Menu");
        backBtn.setFont(new Font("Arial", Font.PLAIN, 13));
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backPanel.add(backBtn);

        bottomPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        bottomPanel.add(backPanel, BorderLayout.SOUTH);

        // Add all to main panel
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

       
        searchBtn.addActionListener(e -> fetchBills());
        payBtn.addActionListener(e -> executePayment());
        backBtn.addActionListener(e -> mainWindow.showPanel("adminMenu"));
    }

    private void fetchBills() {
        String vat = vatField.getText().trim();
        customer = userManager.findUserByVat(vat);
        billCombo.removeAllItems();
        accountCombo.removeAllItems();
        outputArea.setText("");

        if (customer == null) {
            outputArea.setText("Customer not found.");
            return;
        }

        unpaidBills = billManager.loadBillsByCustomerVat("data/bills", customer).stream()
                .filter(bill -> !bill.isPaid())
                .collect(Collectors.toList());

        if (unpaidBills.isEmpty()) {
            outputArea.setText("No unpaid bills for this customer.");
            return;
        }

        for (Bill bill : unpaidBills) {
            billCombo.addItem(bill.marshal());
        }

        customerAccounts = customer.getAccounts();
        if (customerAccounts == null || customerAccounts.isEmpty()) {
            outputArea.append("\n No accounts found for this customer.");
            return;
        }

        for (Account acc : customerAccounts) {
            accountCombo.addItem(acc.getIban() + " | Balance: " + acc.getBalance());
        }
    }

    private void executePayment() {
        int billIndex = billCombo.getSelectedIndex();
        int accIndex = accountCombo.getSelectedIndex();

        if (billIndex < 0 || accIndex < 0) {
            outputArea.setText("Please select both a bill and an account.");
            return;
        }

        Bill selectedBill = unpaidBills.get(billIndex);
        Account chargeAccount = customerAccounts.get(accIndex);
        Account toAccount = selectedBill.getIssuer();
        BigDecimal amount = selectedBill.getAmount();

        if (chargeAccount.getBalance().compareTo(amount) < 0) {
            outputArea.setText("Insufficient funds to pay the bill.");
            return;
        }

        Payment payment = new Payment(chargeAccount, toAccount, amount, selectedBill);
        boolean success = payment.execute();

        if (success) {
            billManager.markBillAsPaid(selectedBill, customer);
            accountManager.saveAccountsToFile("data/accounts/accounts.csv");

            statementManager.addTransactionToStatement(payment);
            statementManager.saveStatements(
                statementManager.getStatementsForAccount(chargeAccount.getIban()),
                "data/statements/" + chargeAccount.getIban() + ".csv"
            );

            outputArea.setText("Bill paid successfully.");
        } else {
            outputArea.setText("Payment failed.");
        }
    }
}
