package frontend.guiComponents;

import javax.swing.*;
import java.awt.*;


public class StartPanel extends JPanel {

    public StartPanel(MainWindow mainWindow) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome to Bank of TUC");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 26));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Buttons
        JButton loginButton = new JButton("Log In");
        JButton signupButton = new JButton("Sign Up");
        loginButton.setPreferredSize(new Dimension(200, 40));
        signupButton.setPreferredSize(new Dimension(200, 40));

        // GridBag setup
        gbc.insets = new Insets(10, 0, 10, 0); // Αποστάσεις ανάμεσα στα στοιχεία
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;

        // Welcome Label 
        gbc.gridy = 0;
        add(welcomeLabel, gbc);

        // Login Button
        gbc.gridy = 1;
        add(loginButton, gbc);

        // SignUp Button
        gbc.gridy = 2;
        add(signupButton, gbc);

        // Action listeners
        loginButton.addActionListener(e -> mainWindow.showPanel("login"));
        signupButton.addActionListener(e -> mainWindow.showPanel("signup"));
    }
}