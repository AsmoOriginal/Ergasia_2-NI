package frontend.guiComponents;

import backend.manager.AccountManager;
import backend.manager.StandingOrderManager;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TimeSimulationPanel extends JPanel {

    private MainWindow mainWindow;
    private AccountManager accountManager;
    private StandingOrderManager standingOrderManager;

    private JTextField dateField;
    private JTextArea outputArea;

    private LocalDate systemDate = LocalDate.now(); // begin with today

    public TimeSimulationPanel(MainWindow mainWindow, AccountManager accountManager, StandingOrderManager standingOrderManager) {
        this.mainWindow = mainWindow;
        this.accountManager = accountManager;
        this.standingOrderManager = standingOrderManager;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel title = new JLabel("Simulate Time Passing", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new FlowLayout());

        inputPanel.add(new JLabel("Enter target date (yyyy-MM-dd):"));
        dateField = new JTextField(10);
        inputPanel.add(dateField);

        JButton simulateBtn = new JButton("Simulate");
        inputPanel.add(simulateBtn);

        add(inputPanel, BorderLayout.CENTER);

        outputArea = new JTextArea(15, 50);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.SOUTH);

        simulateBtn.addActionListener(e -> simulateTime());

        //back button 
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> mainWindow.showPanel("adminMenu"));
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.PAGE_END);
    }

    private void simulateTime() {
        String input = dateField.getText().trim();
        try {
            LocalDate targetDate = LocalDate.parse(input, DateTimeFormatter.ISO_LOCAL_DATE);

            if (targetDate.isBefore(systemDate)) {
                outputArea.setText("Target date is before current simulation date: " + systemDate);
                return;
            }

            TimeSimulatorGui simulator = new TimeSimulatorGui(systemDate, accountManager, standingOrderManager);
            List<String> logLines = simulator.simulateUntil(targetDate);

            outputArea.setText(String.join("\n", logLines));

            systemDate = simulator.getCurrentDate(); // Update the systemDate

        } catch (DateTimeParseException e) {
            outputArea.setText("Invalid date format. Please use yyyy-MM-dd.");
        }
    }
}
