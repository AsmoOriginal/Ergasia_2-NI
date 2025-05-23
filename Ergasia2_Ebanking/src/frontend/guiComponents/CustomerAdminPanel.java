package frontend.guiComponents;

import backend.manager.UserManager;
import backend.model.account.Account;
import backend.model.user.Customer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CustomerAdminPanel extends JPanel {

    private MainWindow mainWindow;
    private UserManager userManager;
    private List<Customer> customers;

    private JTextArea outputArea;
    private JTextField searchField;

    public CustomerAdminPanel(MainWindow mainWindow, UserManager userManager) {
        this.mainWindow = mainWindow;
        this.userManager = userManager;
        this.customers = userManager.getCustomers();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Customer Administration", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

      
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));

        JButton showCustomersBtn = new JButton("Show All Customers");
        JButton backBtn = new JButton("Back");

        showCustomersBtn.setPreferredSize(new Dimension(180, 30));
        backBtn.setPreferredSize(new Dimension(120, 30));

        buttonRow.add(showCustomersBtn);
        buttonRow.add(backBtn);

       
        JPanel searchRow = new JPanel(new BorderLayout(5, 5));
        searchField = new JTextField();
        JButton searchBtn = new JButton("Show Customer Details");

        searchBtn.setPreferredSize(new Dimension(180, 30));
        searchRow.add(searchField, BorderLayout.CENTER);
        searchRow.add(searchBtn, BorderLayout.EAST);

        bottomPanel.add(buttonRow);
        bottomPanel.add(Box.createVerticalStrut(10)); 
        bottomPanel.add(searchRow);

        add(bottomPanel, BorderLayout.SOUTH);

      
        showCustomersBtn.addActionListener(e -> showCustomers());
        searchBtn.addActionListener(e -> showCustomerDetails());
        backBtn.addActionListener(e -> mainWindow.showPanel("adminMenu"));
    }

    private void showCustomers() {
        StringBuilder sb = new StringBuilder();
        if (customers.isEmpty()) {
            sb.append("No customers found.\n");
        } else {
            sb.append("List of Customers:\n");
            for (Customer customer : customers) {
                String userName = "";
                String vatNumber = "";

                String[] parts = customer.marshal().split(",");
                for (String part : parts) {
                    String[] keyValue = part.split(":", 2);
                    if (keyValue.length == 2) {
                        String key = keyValue[0].trim().toLowerCase();
                        String value = keyValue[1].trim();
                        if (key.equals("username")) {
                            userName = value;
                        } else if (key.equals("vatnumber")) {
                            vatNumber = value;
                        }
                    }
                }

                sb.append(String.format("- Username: %-15s | VAT: %s%n", userName, vatNumber));
            }
        }
        outputArea.setText(sb.toString());
    }

    private void showCustomerDetails() {
        String input = searchField.getText().trim();
        if (input.isEmpty()) {
            outputArea.setText("Please enter a username or VAT number.");
            return;
        }

        Customer customer = userManager.findCustomerByUsernameOrVat(input, customers);

        if (customer == null) {
            outputArea.setText("Customer not found.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Customer Details:\n");
        for (String field : customer.marshal().split(",")) {
            String[] keyValue = field.split(":", 2);
            if (keyValue.length == 2) {
                sb.append(String.format("%-15s: %s%n", keyValue[0].trim(), keyValue[1].trim()));
            }
        }

        List<Account> accounts = customer.getAccounts();
        if (accounts == null || accounts.isEmpty()) {
            sb.append("\nNo accounts found for this customer.\n");
        } else {
            sb.append("\nAccounts:\n");
            for (Account acc : accounts) {
                sb.append(String.format("- IBAN: %s | Balance: %.2f | Type: %s%n",
                        acc.getIban(), acc.getBalance(), acc.getType()));
            }
        }

        outputArea.setText(sb.toString());
    }
}
