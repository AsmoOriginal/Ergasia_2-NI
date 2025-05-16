package frontend;

import backend.manager.*;
import backend.model.user.User;
import backend.ui.LoginUI;
public class MainDriver {

	public static void main(String[] args) {
		
		System.out.println("EBanking TUC");
		
		
		// Αρχικοποίηση όλων των Managers (singleton pattern)
        UserManager userManager = UserManager.getInstance();
        AccountManager accountManager = AccountManager.getInstance();
        BillManager billManager = BillManager.getInstance();
        StatementManager statementManager = StatementManager.getInstance();
        TransactionManager transactionManager = TransactionManager.getInstance();
        StandingOrderManager standingOrderManager = StandingOrderManager.getInstance();
        
       // Ταυτοποίηση
        User loggedInUser = LoginUI.login(userManager);
        
	
	// Φόρτωση δεδομένων από αρχεία
    userManager.loadUsersFromFile();
    accountManager.loadAccountsFromFile();
    billManager.loadBillsFromFile("bills.csv");
   // standingOrderManager.loadInitialData("orders/active.csv");
    // + φόρτωση statements αν θέλουμε

 
    }
 }

