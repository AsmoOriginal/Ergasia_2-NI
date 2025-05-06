package backend.model.user;

public abstract class Customer extends User {
	private String vat;    // ΑΦΜ - μοναδικός αναγνωριστικός αριθμός πελάτη 
	private String name;   // ονοματεπώνυμο ή επωνυμία πελάτη 
	private String email;  // email επικοινωνίας
}
