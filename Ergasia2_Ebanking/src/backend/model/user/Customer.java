package backend.model.user;

public abstract class Customer extends User {
	
	private String vat;    // ΑΦΜ - μοναδικός αναγνωριστικός αριθμός πελάτη 

	public Customer(String id, String passwordHash, String role, String legalName, String userName, String vat) {
		super(id, passwordHash, role, legalName, userName);
		this.vat = vat;
	}

	public String getVat() {
		return vat;
	}
	
	
	
	
	
	
	
}
