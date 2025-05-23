package frontend.guiComponents;

import backend.manager.*;
import backend.model.account.Account;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminMenuPanel extends JPanel {

    private MainWindow mainWindow;
    private UserManager userManager;
    private AccountManager accountManager;
    private StandingOrderManager standingOrderManager;
    private StatementManager statementManager;
    private List<Account> accounts;

    public AdminMenuPanel(MainWindow mainWindow, UserManager userManager,
                          AccountManager accountManager, StandingOrderManager standingOrderManager,
                          StatementManager statementManager) {

        this.mainWindow = mainWindow;
        this.userManager = userManager;
        this.accountManager = accountManager;
        this.standingOrderManager = standingOrderManager;
        this.statementManager = statementManager;
        this.accounts = accountManager.getAccounts();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40)); 

        JLabel title = new JLabel("Admin Menu", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        Dimension buttonSize = new Dimension(250, 40);

        JButton customersBtn = new JButton("Customers");
        JButton accountsBtn = new JButton("Bank Accounts");
        JButton billsBtn = new JButton("Company Bills");
        JButton standingOrdersBtn = new JButton("List Standing Orders");
        JButton payBillBtn = new JButton("Pay Customer’s Bill");
        JButton timeSimBtn = new JButton("Simulate Time Passing");
        JButton logoutBtn = new JButton("Logout");

        JButton[] buttons = {
            customersBtn, accountsBtn, billsBtn,
            standingOrdersBtn, payBillBtn, timeSimBtn, logoutBtn
        };

        for (JButton btn : buttons) {
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(buttonSize);
            btn.setFont(new Font("Arial", Font.PLAIN, 16));
            centerPanel.add(btn);
            centerPanel.add(Box.createRigidArea(new Dimension(0, 10))); 
        }

        add(centerPanel, BorderLayout.CENTER);

       
        customersBtn.addActionListener(e -> {
            mainWindow.addPanel("customers", new CustomerAdminPanel(mainWindow, userManager));
            mainWindow.showPanel("customers");
        });

       
        
        accountsBtn.addActionListener(e -> {
            mainWindow.addPanel("accounts", new BankAccountAdminPanel(mainWindow, accountManager, statementManager, accounts));
            mainWindow.showPanel("accounts");
        });

          billsBtn.addActionListener(e -> {
            mainWindow.addPanel("companyBills", new CompanyBillAdminPanel(mainWindow, BillManager.getInstance(), userManager));
            mainWindow.showPanel("companyBills");
        });

          standingOrdersBtn.addActionListener(e -> {
            mainWindow.addPanel("standingOrders", new StandingOrderListPanel(mainWindow, standingOrderManager));
            mainWindow.showPanel("standingOrders");
        });

        payBillBtn.addActionListener(e -> {
            mainWindow.addPanel("payBill", new PayCustomerBillPanel(mainWindow, userManager, accountManager, statementManager));
            mainWindow.showPanel("payBill");
        });

        timeSimBtn.addActionListener(e -> {
            mainWindow.addPanel("simulateTime", new TimeSimulationPanel(mainWindow, accountManager, standingOrderManager));
            mainWindow.showPanel("simulateTime");
        });
        

        logoutBtn.addActionListener(e -> mainWindow.showPanel("start"));
    }
}
