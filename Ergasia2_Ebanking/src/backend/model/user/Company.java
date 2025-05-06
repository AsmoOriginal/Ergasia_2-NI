package backend.model.user;
import backend.model.account.BusinessAccount;
public class Company extends Customer {
	private BusinessAccount businessAccount;  // μοναδικός επιχειρηματικός λογαριασμός 	

	private BusinessAccount getBusinessAccount() {
		return businessAccount;
	}

	private void setBusinessAccount(BusinessAccount businessAccount) {
		this.businessAccount = businessAccount;
	}

	public Company(String id, String passwordHash, String role, String vat, String legalName, String userName,
			BusinessAccount businessAccount) {
		super(id, passwordHash, role, vat, legalName, userName);
		this.businessAccount = businessAccount;
	}
	
	
}
