package frontend.guiComponents;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import backend.manager.AccountManager;
import backend.manager.UserManager;
import backend.model.account.Account;
import backend.model.user.Customer;

public class CustomerMenuPanel extends JPanel {

    private JComboBox<Account> accountComboBox;

    public CustomerMenuPanel(MainWindow mainWindow, Customer customer) {
        setLayout(new BorderLayout());

        // Welcome label at the top
        JLabel welcomeLabel = new JLabel("Welcome, " + customer.getUserName(), SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(welcomeLabel, BorderLayout.NORTH);

        // Get customer accounts
        AccountManager accountManager = AccountManager.getInstance();
        UserManager userManager = UserManager.getInstance();
        List<Account> allAccounts = accountManager.getAllAccounts();
        userManager.bindAccountsToCustomers(List.of(customer), allAccounts);
        List<Account> customerAccounts = customer.getAccounts();

        // Selection bar (South): buttons + account selector
        JPanel selectionBar = new JPanel(new FlowLayout());

        JButton transactionsButton = new JButton("Transactions");
        JButton billsButton = new JButton("Bills");
        JButton logoutButton = new JButton("Log Out");

        accountComboBox = new JComboBox<>(customerAccounts.toArray(new Account[0]));
        accountComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Account acc) {
                    setText(acc.getIban() + " - " + String.format("%.2f€", acc.getBalance()));
                }
                return c;
            }
        });

        selectionBar.add(new JLabel("Selected Account:"));
        selectionBar.add(accountComboBox);
        selectionBar.add(transactionsButton);
        selectionBar.add(billsButton);
        selectionBar.add(logoutButton);
        add(selectionBar, BorderLayout.SOUTH);

        // Center: account information
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Your Accounts"));

        if (customerAccounts.isEmpty()) {
            centerPanel.add(new JLabel("You have no accounts linked to your profile."));
        } else {
            int index = 1;
            for (Account acc : customerAccounts) {
                String type = acc.getClass().getSimpleName().replace("Account", " Account");
                JLabel accLabel = new JLabel(String.format("%d. %s: %s | Balance: %.2f€",
                        index++, type, acc.getIban(), acc.getBalance()));
                centerPanel.add(accLabel);
            }
        }

        add(centerPanel, BorderLayout.CENTER);

        // Left & Right placeholders
        add(new JPanel(), BorderLayout.EAST);
        add(new JPanel(), BorderLayout.WEST);

        // Action listeners
        transactionsButton.addActionListener(e -> {
            Account selected = (Account) accountComboBox.getSelectedItem();
            if (selected != null) {
                mainWindow.addPanel("transactionMenu", new TransactionMenuPanel(mainWindow, customer, selected));
                mainWindow.showPanel("transactionMenu");
            } else {
                JOptionPane.showMessageDialog(this, "Please select an account.");
            }
        });

        billsButton.addActionListener(e -> {
             mainWindow.addPanel("billMenu", new )
            JOptionPane.showMessageDialog(this, "Bills functionality coming soon!");
        });

        logoutButton.addActionListener(e -> {
            mainWindow.showPanel("start");
        });
    }
}
