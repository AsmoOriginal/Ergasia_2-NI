package frontend.guiComponents;

import backend.manager.BillManager;
import backend.manager.UserManager;
import backend.model.bill.Bill;
import backend.model.user.Customer;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class CompanyBillAdminPanel extends JPanel {

    private MainWindow mainWindow;
    private BillManager billManager;
    private UserManager userManager;

    private JTextField vatInput;
    private JTextArea outputArea;

    public CompanyBillAdminPanel(MainWindow mainWindow, BillManager billManager, UserManager userManager) {
        this.mainWindow = mainWindow;
        this.billManager = billManager;
        this.userManager = userManager;

        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Company Bills Administration", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        
        JPanel vatPanel = new JPanel(new BorderLayout(5, 5));
        JLabel vatLabel = new JLabel("Company VAT:");
        vatInput = new JTextField();
        vatInput.setToolTipText("Enter Company VAT");
        vatPanel.add(vatLabel, BorderLayout.WEST);
        vatPanel.add(vatInput, BorderLayout.CENTER);
        bottomPanel.add(vatPanel);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));

      
        JButton showIssuedBtn = new JButton("Show Issued Bills");
        showIssuedBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        showIssuedBtn.addActionListener(e -> showIssuedBills());
        bottomPanel.add(showIssuedBtn);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JButton showPaidBtn = new JButton("Show Paid Bills");
        showPaidBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        showPaidBtn.addActionListener(e -> showPaidBills());
        bottomPanel.add(showPaidBtn);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JButton loadAllBtn = new JButton("Load All Company Bills");
        loadAllBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadAllBtn.addActionListener(e -> loadCompanyBills());
        bottomPanel.add(loadAllBtn);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton backBtn = new JButton("Back");
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> mainWindow.showPanel("adminMenu"));
        bottomPanel.add(backBtn);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void showIssuedBills() {
        List<Bill> issuedBills = billManager.loadAllBillsFromFolder("data/bills/issued");

        if (issuedBills.isEmpty()) {
            outputArea.setText("No issued bills found.");
            return;
        }

        StringBuilder sb = new StringBuilder("Issued Bills:\n");
        for (Bill bill : issuedBills) {
            sb.append(String.format("Bill ID: %s, RF Code: %s, Amount: %s, Issue Date: %s, Due Date: %s, Paid: %s%n",
                    bill.getBillId(), bill.getRfCode(), bill.getAmount().toPlainString(),
                    bill.getIssueDate(), bill.getDueDate(), bill.isPaid() ? "Yes" : "No"));
        }

        outputArea.setText(sb.toString());
    }

    private void showPaidBills() {
        List<Bill> allPaid = billManager.loadAllBillsFromFolder("data/bills/payed")
                .stream()
                .filter(Bill::isPaid)
                .collect(Collectors.toList());

        if (allPaid.isEmpty()) {
            outputArea.setText("No paid bills found.");
            return;
        }

        StringBuilder sb = new StringBuilder("Paid Bills:\n");
        for (Bill bill : allPaid) {
            sb.append(String.format("Bill ID: %s, RF Code: %s, Amount: %s, Paid: Yes%n",
                    bill.getBillId(), bill.getRfCode(), bill.getAmount().toPlainString()));
        }

        outputArea.setText(sb.toString());
    }

    private void loadCompanyBills() {
        String vat = vatInput.getText().trim();

        if (vat.isEmpty()) {
            outputArea.setText("Please enter a company VAT.");
            return;
        }

        Customer issuer = userManager.getCustomers().stream()
                .filter(c -> c.getVatNumber().equals(vat))
                .findFirst()
                .orElse(null);

        if (issuer == null) {
            outputArea.setText("Company with VAT " + vat + " not found.");
            return;
        }

        List<Bill> companyBills = billManager.loadBillsByIssuerVat("data/bills", issuer);
        if (companyBills.isEmpty()) {
            outputArea.setText("No bills found for this company.");
            return;
        }

        StringBuilder sb = new StringBuilder("All Company Bills:\n");
        for (Bill bill : companyBills) {
            sb.append(String.format("Bill ID: %s, RF Code: %s, Amount: %s, Paid: %s%n",
                    bill.getBillId(),
                    bill.getRfCode(),
                    bill.getAmount().toPlainString(),
                    bill.isPaid() ? "Yes" : "No"));
        }

        outputArea.setText(sb.toString());
    }
}
