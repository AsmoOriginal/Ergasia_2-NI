package backend.model.user;

public abstract class Customer extends User {
	
	private String vat;    // ΑΦΜ - μοναδικός αναγνωριστικός αριθμός πελάτη 
	private String legalName;   // ονοματεπώνυμο ή επωνυμία πελάτη 
	private String userName; 
	
	
	
	
	
	public Customer(String id, String passwordHash, String role, String vat, String legalName, String userName) {
		super(id, passwordHash, role);
		this.vat = vat;
		this.legalName = legalName;
		this.userName = userName;
	}
	
	private String getVat() {
		return vat;
	}
	private void setVat(String vat) {
		this.vat = vat;
	}

	private String getLegalName() {
		return legalName;
	}

	private void setLegalName(String legalName) {
		this.legalName = legalName;
	}

	private String getUserName() {
		return userName;
	}

	private void setUserName(String userName) {
		this.userName = userName;
	}
	
	
	
}
