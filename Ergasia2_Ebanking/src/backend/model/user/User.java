package backend.model.user;

public abstract class User {
	private String id;               // μοναδικό username ή login ID 
	private String passwordHash;     // αποθηκευμένο hash του κωδικού 
	private String role;            // ρόλος χρήστη (π.χ. "admin", "individual", "company") 
	private String legalName;   // ονοματεπώνυμο ή επωνυμία πελάτη 
	private String userName;
	public User(String id, String passwordHash, String role, String legalName, String userName) {
		this.id = id;
		this.passwordHash = passwordHash;
		this.role = role;
		this.legalName = legalName;
		this.userName = userName;
	}
	public String getId() {
		return id;
	}
	public String getPasswordHash() {
		return passwordHash;
	}
	public String getRole() {
		return role;
	}
	public String getLegalName() {
		return legalName;
	}
	public String getUserName() {
		return userName;
	}
	
	

	
	
}
