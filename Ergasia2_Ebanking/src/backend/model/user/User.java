package backend.model.user;

public abstract class User {
	private String id;               
	// μοναδικό username ή login ID 
	private String passwordHash;     // αποθηκευμένο hash του κωδικού 
	private String role;            
	 // ρόλος χρήστη (π.χ. "admin", "individual", "company") 
}
