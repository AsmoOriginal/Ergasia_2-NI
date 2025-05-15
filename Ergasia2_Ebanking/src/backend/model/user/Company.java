package backend.model.user;

public class Company extends Customer {
	
	// Οι εταιρείες επιτρέπεται να έχουν μόνο έναν λογαριασμό (ελέγχεται στον AccountManager).
	
	public Company( String legalName, String userName, String password, String vatNumber) {
		super("Company", legalName, userName, password, vatNumber);
	}
	
	
	@Override
	public String marshal() {
		// Χρήση της υπερκλάσης marshal
		return super.marshal();
	}

	@Override
	public void unmarshal(String data) {
		// Χρήση της υπερκλάσης unmarshal
		super.unmarshal(data);
	}
	
}
