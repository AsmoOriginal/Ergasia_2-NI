package backend.ui;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import backend.manager.AccountManager;
import backend.manager.BillManager;
import backend.manager.StandingOrderManager;
import backend.manager.StatementManager;
import backend.manager.UserManager;
import backend.model.account.Account;
import backend.model.bill.Bill;
import backend.model.order.StandingOrder;
import backend.model.transaction.Payment;
import backend.model.user.Customer;

public class AdminMenuUI {

    private static UserManager userManager;
    private static AccountManager accountManager;
    private static StandingOrderManager standingOrderManager;
    private static StatementManager statementManager;
    private static List<Account> accounts;
    private static BillManager billManager= BillManager.getInstance();
    private static LocalDate systemDate = LocalDate.of(2025, 4, 1);
    
    public static void initialize(UserManager userM, AccountManager accountM, StandingOrderManager standingOrderM, StatementManager statementM) {
        userManager = userM;
        accountManager = accountM;
        standingOrderManager = standingOrderM;
        accounts = accountManager.getAccounts();
        setStatementManager(statementM);
        
    }

    
    
    public static void show() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== Admin Menu =====");
            System.out.println("1. Customers");
            System.out.println("2. Bank Accounts");
            System.out.println("3. Company Bills");
            System.out.println("4. List Standing Orders");
            System.out.println("5. Pay Customer’s Bill");
            System.out.println("6. Simulate Time Passing");
            System.out.println("0. Logout");
            System.out.print("Select option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    CustomerAdminMenu menu = new CustomerAdminMenu(userManager.getCustomers(), userManager);
                    menu.show();
                    break;
                case "2":
				
                	BankAccountAdminMenu bankMenu = new BankAccountAdminMenu(accounts, AccountManager.getInstance(), StatementManager.getInstance());
                	bankMenu.show();
                    break;
                case "3":
				new CompanyBillAdminMenu(BillManager.getInstance(), UserManager.getInstance());
                	CompanyBillAdminMenu.show();
                    break;
                case "4":
                	listStandingOrders(scanner);
                    break;
                case "5":
                  payCustomerBill(scanner);
                    break;
                case "6":
                    simulateTimePassing(scanner);
                    break;
                case "0":
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void simulateTimePassing(Scanner scanner) {
        System.out.print("Enter target date (yyyy-MM-dd): ");
        String input = scanner.nextLine();
        try {
            LocalDate targetDate = LocalDate.parse(input, DateTimeFormatter.ISO_LOCAL_DATE);
            if (targetDate.isBefore(systemDate)) {
                System.out.println("Target date is before current simulation date: " + systemDate);
                return;
            }

            TimeSimulator simulator = new TimeSimulator(systemDate, accountManager, standingOrderManager);
            simulator.simulateUntil(targetDate);
            systemDate = targetDate; // ενημέρωση της systemDate

            System.out.println("Time simulation finished.");
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd.");
        }
    }
    
    private static void listStandingOrders(Scanner scanner) {
        System.out.print("Enter customer's VAT number: ");
        String vat = scanner.nextLine().trim();

        List<StandingOrder> orders = standingOrderManager.listStandingOrdersForCustomer(vat);

        if (orders.isEmpty()) {
            System.out.println("No standing orders found for this customer.");
            return;
        }

        System.out.println("Standing Orders:");
        for (StandingOrder order : orders) {
            System.out.println("- " + order.marshal());
        }
    }
    
    private static void payCustomerBill(Scanner scanner) {
        System.out.print("Enter customer's VAT number: ");
        String vat = scanner.nextLine().trim();

        Customer customer = UserManager.getInstance().findUserByVat(vat);
        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }

        List<Bill> allBills = billManager.loadBillsByCustomerVat("data/bills", customer);
        List<Bill> unpaidBills = allBills.stream()
                                         .filter(bill -> !bill.isPaid())
                                         .collect(Collectors.toList());

        if (unpaidBills.isEmpty()) {
            System.out.println("No unpaid bills for this customer.");
            return;
        }

        System.out.println("Unpaid Bills:");
        for (int i = 0; i < unpaidBills.size(); i++) {
            System.out.printf("[%d] %s\n", i + 1, unpaidBills.get(i).marshal());
        }

        System.out.print("Select bill number to pay: ");
        int billChoice;
        try {
            billChoice = Integer.parseInt(scanner.nextLine().trim());
            if (billChoice < 1 || billChoice > unpaidBills.size()) {
                System.out.println("Invalid choice.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }

        Bill selectedBill = unpaidBills.get(billChoice - 1);
        BigDecimal amount = selectedBill.getAmount();

        List<Account> customerAccounts = customer.getAccounts();
        if (customerAccounts == null || customerAccounts.isEmpty()) {
            System.out.println("No accounts found for this customer.");
            return;
        }

        System.out.println("Customer's Accounts:");
        for (int i = 0; i < customerAccounts.size(); i++) {
            Account acc = customerAccounts.get(i);
            System.out.printf("[%d] %s (Balance: %s)\n", i + 1, acc.getIban(), acc.getBalance().toPlainString());
        }

        System.out.print("Select account number to charge: ");
        int accChoice;
        try {
            accChoice = Integer.parseInt(scanner.nextLine().trim());
            if (accChoice < 1 || accChoice > customerAccounts.size()) {
                System.out.println("Invalid choice.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }

        Account chargeAccount = customerAccounts.get(accChoice - 1);
        Account toAccount = selectedBill.getIssuer();

        if (chargeAccount == null) {
            System.out.println("Charge account not set for this bill.");
            return;
        }
        if (chargeAccount.getBalance().compareTo(amount) < 0) {
            System.out.println("Insufficient funds to pay the bill.");
            return;
        }

        Payment payment = new Payment(chargeAccount, toAccount, amount, selectedBill);
        boolean success = payment.execute();

        if (success) {
            billManager.markBillAsPaid(selectedBill, customer);
            accountManager.saveAccountsToFile("data/accounts/accounts.csv");

            // Προσθήκη της συναλλαγής στο statement και αποθήκευση
            StatementManager sm = StatementManager.getInstance();
            sm.addTransactionToStatement(payment);
            sm.saveStatements(
                sm.getStatementsForAccount(chargeAccount.getIban()),
                "data/statements/" + chargeAccount.getIban() + ".csv"
            );

            System.out.println("Bill paid successfully.");
        } else {
            System.out.println("Payment failed.");
        }
    }
    








	public static StatementManager getStatementManager() {
		return statementManager;
	}



	public static void setStatementManager(StatementManager statementManager) {
		AdminMenuUI.statementManager = statementManager;
	}



	public static BillManager getBillManager() {
		return billManager;
	}



	public static void setBillManager(BillManager billManager) {
		AdminMenuUI.billManager = billManager;
	}

}