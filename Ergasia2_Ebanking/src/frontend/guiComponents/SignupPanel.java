package frontend.guiComponents;

import javax.swing.*;
import java.awt.*;

import backend.context.AppContext;

public class SignupPanel extends JPanel {

    public SignupPanel(MainWindow mainWindow) {
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

        // Action listeners 
        //go back to the startPanel
        backButton.addActionListener(e -> mainWindow.showPanel("start"));
        //submit button that creates a user
        submitButton.addActionListener(e -> {
            // user creation
            JOptionPane.showMessageDialog(this, "Sign up functionality coming soon!");
        });
    }
}