package backend.model.user;

public abstract class User {
	private String id;               
	// μοναδικό username ή login ID 
	private String passwordHash;     // αποθηκευμένο hash του κωδικού 
	private String role;            
	 // ρόλος χρήστη (π.χ. "admin", "individual", "company") 
	

	public User(String id, String passwordHash, String role) {
	
		this.id = id;
		this.passwordHash = passwordHash;
		this.role = role;
	}	
	
	private String getId() {
		return id;
	}
	private void setId(String id) {
		this.id = id;
	}
	private String getPasswordHash() {
		return passwordHash;
	}
	private void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	private String getRole() {
		return role;
	}
	private void setRole(String role) {
		this.role = role;
	}
	
	
}
