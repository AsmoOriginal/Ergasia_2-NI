package backend.model.user;
import java.util.List; 
import backend.model.account.Account;

public class Individual extends Customer {
	 
	private List<Account> accounts;             
	 // λογαριασμοί όπου είναι κύριος κάτοχος 
	private List<Account> secondaryAccounts;     // λογαριασμοί όπου είναι δευτερεύων κάτοχος
}
