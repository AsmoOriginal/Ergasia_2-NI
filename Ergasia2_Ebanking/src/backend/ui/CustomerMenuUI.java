package backend.ui;


import java.util.List;
import java.util.Scanner;

import backend.manager.AccountManager;
import backend.manager.BillManager;
import backend.model.account.Account;
import backend.model.bill.Bill;
import backend.model.user.Customer;

public class CustomerMenuUI {
    public static void menu(Customer customer) {
    	Scanner scanner = new Scanner(System.in);
    	boolean running = true;
        AccountManager accountManager = AccountManager.getInstance();
        System.out.println("\nWelcome, " + customer.getUserName());
        
        
        bindAccountsToCustomers(customer, accounts);
        
        if (accounts.isEmpty()) {
            System.out.println("You have no accounts linked to your profile.");
        } else {
        	for (Account acc : accounts) {
        	    String type = acc.getClass().getSimpleName().replace("Account", " Account");
        	    System.out.println(type + ":" + acc.getIban());
        	    System.out.printf("Balance: %.2f€%n", acc.getBalance());
        	}
        }

       
            

            while (running) {
                System.out.println("\n=== Customer Menu ===");
                System.out.println("1. Transactions");
                System.out.println("2. Bills");
                System.out.println("3. Log Out");
                System.out.print("Choose an option (1-3): ");
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        handleTransactions(customer);
                        break;
                    case "2":
                    	 BillMenuUi.show(customer);
                        break;
                    case "3":
                        System.out.println("Logging out...");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        }

        private static void handleTransactions(Customer customer) {
            System.out.println("\n[Transactions not implemented yet]");
            // TransactionManager.getInstance().showTransactionsFor(customer);
        }

      
      
      
        }

