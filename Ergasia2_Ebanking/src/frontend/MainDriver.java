package frontend;

import backend.manager.*;
import backend.model.user.User;
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
	}
	
	// Φόρτωση δεδομένων από αρχεία
    userManager.loadUsers("./data/users/users.csv");
    accountManager.loadAccounts("./data/accounts/accounts.csv");
    billManager.loadBills("./data/bills.csv");
    standingOrderManager.loadOrders("./data/orders/active.csv");
    // + φόρτωση statements αν θέλουμε

 // Διαδικασία Ταυτοποίησης
    User loggedInUser = LoginUI.login(userManager);
    if (loggedInUser == null) {
        System.out.println("Αποτυχία ταυτοποίησης. Τερματισμός...");
        return;
    }
 }
}
