package frontend;

import java.util.Scanner;

public class AdminMenuUI {

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
                    CustomerAdminMenu.show();
                    break;
                case "2":
                    BankAccountAdminMenu.show();
                    break;
                case "3":
                    CompanyBillAdminMenu.show();
                    break;
                case "4":
                    StandingOrderMenu.show();
                    break;
                case "5":
                    BillPaymentMenu.payCustomerBill();
                    break;
                case "6":
                    TimeSimulator.simulate();
                    break;
                case "0":
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }
}
