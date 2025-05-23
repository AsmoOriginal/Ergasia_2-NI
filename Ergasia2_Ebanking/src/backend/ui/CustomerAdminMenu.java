package backend.ui;

import java.util.List;
import java.util.Scanner;

import backend.manager.AccountManager;
import backend.manager.UserManager;
import backend.model.account.Account;
import backend.model.user.Customer;

public class CustomerAdminMenu {
	
	private   List<Customer> customers;
	private  UserManager userManager;
	private AccountManager accountManager;
	
	
	
    

	public CustomerAdminMenu(List<Customer> customers, UserManager userManager) {
		super();
		this.customers = customers;
		this.userManager = userManager;
	}
	

	
	public List<Customer> getCustomers() {
		return customers;
	}

	public  void setCustomers(List<Customer> loadedCustomers) {
        customers = loadedCustomers;
    }
    
    public  void show() {
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
                    showCustomers();
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

    public void showCustomers() {
        if (customers.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }

        System.out.println("List of Customers:");
        for (Customer customer : customers) {
            String userName = "";
            String vatNumber = "";

            String[] parts = customer.marshal().split(",");
            

            
            for (String part : parts) {
                String[] keyValue = part.split(":", 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().toLowerCase();
                    String value = keyValue[1].trim();
                    if (key.equalsIgnoreCase("userName")) {
                        userName = value;
                    }
                    if (key.equalsIgnoreCase("vatNumber")) {
                        vatNumber = value;
                    }
                }
            }

            System.out.printf("- Username: %-15s | VAT: %s%n", userName, vatNumber);
        }
    }

    public void showCustomerDetails(Scanner scanner) {
        System.out.print("Enter customer's username or VAT: ");
        String input = scanner.nextLine().trim();

        Customer customer = userManager.findCustomerByUsernameOrVat(input, customers); 
        
		customer.getAccounts();
        
        
        System.out.println("\nCustomer Details:");
        printKeyValueCSV(customer.marshal());

        List<Account> accounts = customer.getAccounts();
        if (accounts == null || accounts.isEmpty()) {
            System.out.println("No accounts found for this customer.");
        } else {
            System.out.println("\nAccounts:");
            for (Account acc : accounts) {
                System.out.printf("- IBAN: %s | Balance: %.2f | Type: %s%n",
                    acc.getIban(), acc.getBalance(), acc.getType());
            }
        }
    }


    public void printKeyValueCSV(String csvLine) {
        String[] fields = csvLine.split(",");
        for (String field : fields) {
            String[] keyValue = field.split(":", 2);
            if (keyValue.length == 2) {
                System.out.printf("%-15s: %s%n", keyValue[0].trim(), keyValue[1].trim());
            }
        }
    }

    
}