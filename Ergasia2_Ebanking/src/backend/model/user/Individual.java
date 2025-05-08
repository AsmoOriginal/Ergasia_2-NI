package backend.model.user;
import java.util.List; 
import backend.model.account.Account;

public class Individual extends Customer {
	 
	private List<Account> accounts;             
	 // λογαριασμοί όπου είναι κύριος κάτοχος 
	private List<Account> secondaryAccounts;     // λογαριασμοί όπου είναι δευτερεύων κάτοχος
	
	public Individual(String id, String passwordHash, String role, String legalName, String userName, String vat,
			List<Account> accounts, List<Account> secondaryAccounts) {
		super(id, passwordHash, role, legalName, userName, vat);
		this.accounts = accounts;
		this.secondaryAccounts = secondaryAccounts;
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

	public List<Account> getSecondaryAccounts() {
		return secondaryAccounts;
	}

	public void setSecondaryAccounts(List<Account> secondaryAccounts) {
		this.secondaryAccounts = secondaryAccounts;
	}
	

	
}
