package frontend.guiComponents;

import backend.manager.AccountManager;
import backend.manager.UserManager;
import backend.model.account.Account;
import backend.model.account.BusinessAccount;
import backend.model.account.PersonalAccount;
import backend.model.user.Company;
import backend.model.user.Customer;
import backend.model.user.Individual;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class SignupPanel extends JPanel {

    private final UserManager userManager;
    private final AccountManager accountManager;

    public SignupPanel(MainWindow mainWindow, UserManager userManager, AccountManager accountManager) {
        this.userManager = userManager;
        this.accountManager = accountManager;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // title of the label
        JLabel title = new JLabel("Sign Up");
        title.setFont(new Font("Arial", Font.BOLD, 24));

        // text fields
        JTextField legalNameField = new JTextField(20);
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField vatNumberField = new JTextField(20);

        // choose the account type
        String[] userTypes = {"Individual", "Company"};
        JComboBox<String> typeComboBox = new JComboBox<>(userTypes);

        JButton submitButton = new JButton("Create user");
        JButton backButton = new JButton("Back");

        gbc.insets = new Insets(7, 7, 7, 7); 
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(title, gbc);

        // Labels & Fields 
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;
        gbc.gridy++;

        add(new JLabel("Account Type:"), gbc);
        gbc.gridx = 1;
        add(typeComboBox, gbc);

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Legal Name:"), gbc);
        gbc.gridx = 1;
        add(legalNameField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("VAT Number:"), gbc);
        gbc.gridx = 1;
        add(vatNumberField, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy++;
        add(backButton, gbc);
        gbc.gridx = 1;
        add(submitButton, gbc);

        // go back to startPanel
        backButton.addActionListener(e -> mainWindow.showPanel("start"));

        // submit button that creates a user
        submitButton.addActionListener(e -> {
            String type = (String) typeComboBox.getSelectedItem();
            String legalName = legalNameField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String vatNumber = vatNumberField.getText().trim();

            // validation
            if (username.isEmpty() || password.isEmpty() || vatNumber.isEmpty() || legalName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            if (userManager.getUserByUsername(username) != null) {
                JOptionPane.showMessageDialog(this, "Username already exists.");
                return;
            }

            if (userManager.findUserByVat(vatNumber) != null) {
                JOptionPane.showMessageDialog(this, "VAT number already exists.");
                return;
            }

            Customer newUser;
            if ("Individual".equals(type)) {
                newUser = new Individual(legalName, username, password, vatNumber);
            } else {
                newUser = new Company(legalName, username, password, vatNumber);
            }

            // Save user
            userManager.addUser(newUser);
            userManager.saveUsersToFile("data/users/users.csv");

            // Ask for account type
            String[] options = {"Personal Account", "Business Account"};
            int accChoice = JOptionPane.showOptionDialog(this, "Select Account Type", "Account Type",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            Account newAccount = null;

            if (accChoice == 0) {
                PersonalAccount account = new PersonalAccount(newUser, new BigDecimal("0.02"));
               
                newAccount = account;
            } else if (accChoice == 1) {
                newAccount = new BusinessAccount(newUser, new BigDecimal("0.03"));
            }

            if (newAccount != null) {
                accountManager.addAccount(newAccount);
                accountManager.saveAccountsToFile("data/accounts/accounts.csv");

                JOptionPane.showMessageDialog(this, "User created successfully!\nIBAN: " + newAccount.getIban());
                mainWindow.showPanel("login");
            }
        });
    }
}
