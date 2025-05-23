package frontend.guiComponents;

import backend.manager.StandingOrderManager;
import backend.model.order.StandingOrder;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StandingOrderListPanel extends JPanel {
    private StandingOrderManager standingOrderManager;
    private JTextField vatField;
    private JTextArea resultArea;
    private MainWindow mainWindow;

    public StandingOrderListPanel(MainWindow mainWindow, StandingOrderManager standingOrderManager) {
        this.mainWindow = mainWindow;
        this.standingOrderManager = standingOrderManager;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

      
        JLabel titleLabel = new JLabel("Standing Order Lookup", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        
        resultArea = new JTextArea(15, 50);
        resultArea.setEditable(false);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        vatField = new JTextField(15);
        JButton searchButton = new JButton("Search");
        JButton backButton = new JButton("Back to Admin Menu");

        bottomPanel.add(new JLabel("Enter customer's VAT number:"));
        bottomPanel.add(vatField);
        bottomPanel.add(searchButton);
        bottomPanel.add(backButton);

        add(bottomPanel, BorderLayout.SOUTH);

        
        searchButton.addActionListener(e -> listStandingOrders());
        backButton.addActionListener(e -> mainWindow.showPanel("adminMenu"));
    }

    private void listStandingOrders() {
        String vat = vatField.getText().trim();

        List<StandingOrder> orders = standingOrderManager.listStandingOrdersForCustomer(vat);

        resultArea.setText(""); 

        if (orders.isEmpty()) {
            resultArea.setText("No standing orders found for this customer.");
            return;
        }

        StringBuilder sb = new StringBuilder("Standing Orders:\n");
        for (StandingOrder order : orders) {
            sb.append("- ").append(order.marshal()).append("\n");
        }

        resultArea.setText(sb.toString());
    }
}
