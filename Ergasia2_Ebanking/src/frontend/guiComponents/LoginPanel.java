package frontend.guiComponents;

import javax.swing.*;
import java.awt.*;

import backend.context.AppContext;
import backend.manager.UserManager;
import backend.model.user.Admin;
import backend.model.user.Customer;
import backend.model.user.User;

public class LoginPanel extends JPanel {

    private final JTextField userField;
    private final JPasswordField passField;

    public LoginPanel(MainWindow mainWindow, UserManager userManager) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel titleLabel = new JLabel("Log In");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel userLabel = new JLabel("Username:");
        userField = new JTextField(20);

        JLabel passLabel = new JLabel("Password:");
        passField = new JPasswordField(20);

        JButton loginButton = new JButton("Log In");
        JButton backButton = new JButton("Back");

        loginButton.addActionListener(e -> attemptLogin(userManager, mainWindow));

        backButton.addActionListener(e -> mainWindow.showPanel("start"));
        
        

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        add(userLabel, gbc);
        gbc.gridx = 1;
        add(userField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(passLabel, gbc);
        gbc.gridx = 1;
        add(passField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        add(loginButton, gbc);
        gbc.gridx = 1;
        add(backButton, gbc);
    }

    private void attemptLogin(UserManager userManager, MainWindow mainWindow) {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword());

        User user = userManager.getUserByUsername(username);

        if (user == null) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Username not found. Do you want to retrieve it using your VAT number?",
                    "User Not Found", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                String vat = JOptionPane.showInputDialog(this, "Enter your VAT number:");
                user = userManager.findUserByVat(vat);
                if (user != null) {
                    JOptionPane.showMessageDialog(this, "Your username is: " + user.getUserName());
                } else {
                    JOptionPane.showMessageDialog(this, "User not found with this VAT.");
                }
            }
            return;
        }

        if (!user.getPassword().equals(password)) {
            int retry = JOptionPane.showConfirmDialog(this,
                    "Wrong password. Do you want to change it?",
                    "Authentication Failed", JOptionPane.YES_NO_OPTION);

            if (retry == JOptionPane.YES_OPTION) {
                resetPassword(userManager, user);
            }
            return;
        }

        JOptionPane.showMessageDialog(this, "Login successful! Welcome, " + user.getUserName());

        //Load the correct menu depending on the user type
        if (user instanceof Customer) {
            mainWindow.addPanel("customerMenu", new CustomerMenuPanel(mainWindow, (Customer) user));
            mainWindow.showPanel("customerMenu");
        } else if (user instanceof Admin) {      
         //  mainWindow.addPanel("adminMenu", new AdminMenuPanel(mainWindow, (Admin) user));
          // mainWindow.showPanel("adminMenu");
        	
        }
        mainWindow.revalidate();
    }

    private void resetPassword(UserManager userManager, User user) {
        String legalName = JOptionPane.showInputDialog(this, "Enter your legal name:");
        String vat = JOptionPane.showInputDialog(this, "Enter your VAT number:");

        if (!user.getLegalName().equalsIgnoreCase(legalName)) {
            JOptionPane.showMessageDialog(this, "Legal name does not match.");
            return;
        }

        if (user instanceof Customer customer) {
            if (!customer.getVatNumber().equals(vat)) {
                JOptionPane.showMessageDialog(this, "VAT number does not match.");
                return;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Only customers can recover using VAT.");
            return;
        }

        String newPassword = JOptionPane.showInputDialog(this, "Enter new password:");
        user.setPassword(newPassword);
        //save the new password

        JOptionPane.showMessageDialog(this, "Password reset successfully. Try logging in again.");
    }
}