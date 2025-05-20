package backend.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import backend.manager.AccountManager;
import backend.manager.BillManager;
import backend.manager.StandingOrderManager;
import backend.manager.StatementManager;
import backend.manager.UserManager;
import backend.model.account.Account;

public class AdminMenuUI {

    private static UserManager userManager;
    private static AccountManager accountManager;
    private static StandingOrderManager standingOrderManager;
    private static StatementManager statementManager;
    private static List<Account> accounts;
    private static BillManager billManager;
    
    public static void initialize(UserManager userM, AccountManager accountM, StandingOrderManager standingOrderM, StatementManager statementM) {
        userManager = userM;
        accountManager = accountM;
        standingOrderManager = standingOrderM;
        accounts = accountManager.getAccounts();
        statementManager = statementM;
        
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
               //     StandingOrderMenu.show();
                    break;
                case "5":
                  //  BillPaymentMenu.payCustomerBill();
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
            LocalDate startDate = LocalDate.now(); // ή από κάποιο system date αν το έχετε αλλού
            TimeSimulator simulator = new TimeSimulator(startDate, accountManager, standingOrderManager);
            simulator.simulateUntil(targetDate);
            System.out.println("Time simulation finished.");
        } catch (Exception e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd.");
        }
    }
}
