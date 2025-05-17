package frontend;

import java.util.Scanner;

import backend.manager.*;
import backend.model.user.User;
import backend.ui.LoginUI;
import backend.ui.SignInUI;


public class MainDriver {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		
		System.out.println("EBanking TUC");
		
		
		// Αρχικοποίηση όλων των Managers (singleton pattern)
        UserManager userManager = UserManager.getInstance();
        AccountManager accountManager = AccountManager.getInstance();
        BillManager billManager = BillManager.getInstance();
        StatementManager statementManager = StatementManager.getInstance();
        TransactionManager transactionManager = TransactionManager.getInstance();
        StandingOrderManager standingOrderManager = StandingOrderManager.getInstance();
        
	
	   // Φόρτωση δεδομένων από αρχεία
       userManager.loadUsersFromFile();
       accountManager.loadAccountsFromFile();
       billManager.loadBillsFromFile("bills.csv");
       // standingOrderManager.loadInitialData("orders/active.csv");
       // + φόρτωση statements αν θέλουμε
    
       // Ταυτοποίηση
       User currentUser = null;
       
       while (true) {
    	   //menu to Sing in or Log In
    	   menuForSignOrLogIn();

    	   String choice = scanner.nextLine().trim();
           switch (choice) {
           case "1":
        	   //log in user και ταυτοποίηση του χρήστη
               currentUser = LoginUI.login(userManager);
               if (currentUser != null) {
                   System.out.println("Login successful. Welcome, " + currentUser.getUserName());
               }
               break;
           case "2":
        	   //Sign up ενός χρήστη μόνο individual ή Company
               User newUser = SignInUI.signInMenu(userManager, scanner);
               if (newUser != null) {
                   System.out.println("Account created successfully. You can now log in.");
               }
               break;
           case "3":
               System.out.println("Thank you for using Bank Of TUC.");
               scanner.close();
               System.exit(0);
               break;
           default:
               System.out.println("Invalid option. Please try again.");
       }

       }
   }

   public static void menuForSignOrLogIn() {
       System.out.println("\n==== Welcome to Bank Of TUC ====");
       System.out.println("(1) Log In");
       System.out.println("(2) Sign Up");
       System.out.println("(3) Exit");
       System.out.print("Choose an option (1-3): ");
   }
	
 }