package backend.ui;

import backend.model.user.Customer;

import java.util.List;
import java.util.Scanner;

public class CustomerAdminMenu {
	
	private static List<Customer> customers;

    public static void setCustomers(List<Customer> loadedCustomers) {
        customers = loadedCustomers;
    }
    
    public static void show() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Customer Admin Menu ===");
            System.out.println("1. Show Customers");
            System.out.println("2. Show Customer Details");
            System.out.println("0. Back");
            System.out.print("Select option: ");
            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    showAllCustomers();
                    break;
                case "2":
                    showCustomerDetails(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void showAllCustomers() {
        if (customers == null || customers.isEmpty()) {
            System.out.println("No customers available.");
            return;
        }
        System.out.println("\n--- All Customers ---");
        for (Customer c : customers) {
            System.out.println("- " + c.getLegalName() + " (VAT: " + c.getVatNumber() + ")");
        }
    }

    private static void showCustomerDetails(Scanner scanner) {
        System.out.print("Enter VAT number: ");
        String vat = scanner.nextLine();

        Customer found = null;
        for (Customer c : customers) {
            if (c.getVatNumber().equals(vat)) {
                found = c;
                break;
            }
        }

        if (found != null) {
            System.out.println("\n--- Customer Details ---");
            System.out.println("Type: " + found.getClass().getSimpleName());
            System.out.println("Name: " + found.getLegalName());
            System.out.println("Username: " + found.getUserName());
            System.out.println("VAT Number: " + found.getVatNumber());
        } else {
            System.out.println("Customer not found.");
        }
    }

}
