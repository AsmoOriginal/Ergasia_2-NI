package frontend.guiComponents;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import backend.manager.BillManager;
import backend.model.bill.Bill;
import backend.model.user.Customer;

public class BillMenuPanel extends JPanel {
    private final Customer customer;
    private final MainWindow mainWindow;
    private final JTextArea billArea;

    public BillMenuPanel(MainWindow mainWindow, Customer customer) {
        this.customer = customer;
        this.mainWindow = mainWindow;

        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Bill Menu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        billArea = new JTextArea();
        billArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(billArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton issuedButton = new JButton("Load Issued Bills");
        JButton paidButton = new JButton("Show Paid Bills");
        JButton backButton = new JButton("Back");

        issuedButton.addActionListener(e -> loadIssuedBills());
        paidButton.addActionListener(e -> showPaidBills());
        backButton.addActionListener(e -> mainWindow.showPanel("customerMenu"));

        buttonPanel.add(issuedButton);
        buttonPanel.add(paidButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadIssuedBills() {
        billArea.setText("");
        List<Bill> bills = BillManager.getInstance().loadBillsByCustomerVat("data/bills", customer);
        List<Bill> activeBills = bills.stream()
                .filter(b -> !b.isPaid() && b.getDueDate().isAfter(LocalDate.now()))
                .toList();

        if (activeBills.isEmpty()) {
            billArea.setText("No active bills found.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Bill bill : activeBills) {
                sb.append(String.format(
                        "RF Code: %s, Bill ID: %s, Amount: %.2f€, Issue Date: %s, Due Date: %s\n",
                        bill.getRfCode(), bill.getBillId(), bill.getAmount(),
                        bill.getIssueDate(), bill.getDueDate()));
            }
            billArea.setText(sb.toString());
        }
    }

    private void showPaidBills() {
        billArea.setText("");
        List<Bill> bills = BillManager.getInstance().loadBillsByCustomerVat("data/bills", customer);
        List<Bill> paidBills = bills.stream().filter(Bill::isPaid).toList();

        if (paidBills.isEmpty()) {
            billArea.setText("No paid bills found.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Bill bill : paidBills) {
                sb.append(String.format(
                        "RF Code: %s, Bill ID: %s, Amount: %.2f€, Issue Date: %s, Due Date: %s\n",
                        bill.getRfCode(), bill.getBillId(), bill.getAmount(),
                        bill.getIssueDate(), bill.getDueDate()));
            }
            billArea.setText(sb.toString());
        }
    }
}
