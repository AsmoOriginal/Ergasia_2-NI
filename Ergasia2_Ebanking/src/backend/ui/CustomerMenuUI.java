package backend.ui;


import java.util.List;
import java.util.Scanner;

import backend.manager.AccountManager;
import backend.manager.UserManager;
import backend.model.account.Account;
import backend.model.user.Customer;

public class CustomerMenuUI {
    public static void menu(Customer customer) {
    	Scanner scanner = new Scanner(System.in);
    	boolean running = true;
    
        AccountManager accountManager = AccountManager.getInstance();
        UserManager userManager = UserManager.getInstance();
        //get all the accounts that exist 
        List<Account> allAccounts = accountManager.getAllAccounts();
        // use List.of(customer) to create a list with this customer
        userManager.bindAccountsToCustomers(List.of(customer), allAccounts);
        
        //get the accounts of this user
        List<Account> customerAccounts = customer.getAccounts();
        
        
        System.out.println("\nWelcome, " + customer.getUserName());
        
        if (customerAccounts.isEmpty()) {
            System.out.println("You have no accounts linked to your profile.");
        } else {
        	int index = 1;
        	for (Account acc : customerAccounts) {
        		System.out.printf("%d. ", index++);
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
                        handleTransactions(customer, customerAccounts);
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

        private static void handleTransactions(Customer customer, List<Account> accounts) {
        	Scanner scanner = new Scanner(System.in);
        	
        	//select the account you want to do a transaction 
        	System.out.println("\nSelect an account for transactions: ");
        	System.out.print("Enter account number (or 0 to cancel): ");
        	//select the user
        	try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice > 0 && choice <= accounts.size()) {
                    Account selectedAccount = accounts.get(choice - 1);
                    //go to transaction menu
                    TransactionMenuUI.transactionShowMenu(customer, selectedAccount);
                    scanner.close();
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
        	
            
 }