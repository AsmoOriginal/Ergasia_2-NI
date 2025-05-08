package backend.model.user;
import backend.model.account.BusinessAccount;
public class Company extends Customer {
	private BusinessAccount businessAccount;  // μοναδικός επιχειρηματικός λογαριασμός 	

	public Company(String id, String passwordHash, String role, String legalName, String userName, String vat,
			BusinessAccount businessAccount) {
		super(id, passwordHash, role, legalName, userName, vat);
		this.businessAccount = businessAccount;
	}

	public BusinessAccount getBusinessAccount() {
		return businessAccount;
	}

	public void setBusinessAccount(BusinessAccount businessAccount) {
		this.businessAccount = businessAccount;
	}


	
}
