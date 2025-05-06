package backend.model.user;

public abstract class Customer extends User {
	
	private String vat;    // ΑΦΜ - μοναδικός αναγνωριστικός αριθμός πελάτη 
	private String legalName;   // ονοματεπώνυμο ή επωνυμία πελάτη 
	private String userName; // ονοματεπώνυμο ή επωνυμία πελάτη μέσα στην εφαρμογή
	
	
	
	
	
	public Customer(String id, String passwordHash, String role, String vat, String legalName, String userName) {
		super(id, passwordHash, role);
		this.vat = vat;
		this.legalName = legalName;
		this.userName = userName;
	}
	//
	private String getVat() {
		return vat;
	}
	private void setVat(String vat) {
		this.vat = vat;
	}
	public String getLegalName() {
		return legalName;
	}
	public void setLegalName(String legalName) {
		this.legalName = legalName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	
	
}
