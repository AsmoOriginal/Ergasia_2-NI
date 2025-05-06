package backend.model.user;
import java.util.List; 
import backend.model.account.Account;

public class Individual extends Customer {
	 
	private List<Account> accounts;             
	 // λογαριασμοί όπου είναι κύριος κάτοχος 
	private List<Account> secondaryAccounts;     // λογαριασμοί όπου είναι δευτερεύων κάτοχος
	
public Individual(String id, String passwordHash, String role, String vat, String legalName, String userName,
			List<Account> accounts, List<Account> secondaryAccounts) {
		super(id, passwordHash, role, vat, legalName, userName);
		this.accounts = accounts;
		this.secondaryAccounts = secondaryAccounts;
	}
	
	private List<Account> getAccounts() {
		return accounts;
	}
	private void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}
	private List<Account> getSecondaryAccounts() {
		return secondaryAccounts;
	}
	private void setSecondaryAccounts(List<Account> secondaryAccounts) {
		this.secondaryAccounts = secondaryAccounts;
	}
	
	
}
