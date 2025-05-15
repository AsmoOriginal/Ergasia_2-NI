package backend.model.user;

import backend.storage.Storable;

public abstract class User implements Storable{
	 
	
	private String legalName;   // ονοματεπώνυμο ή επωνυμία πελάτη 
	private String userName;
	private String password;     //  κωδικός 
	private final String type;  // "Individual", "Company", "Admin"
	
	
	
	public User(String type,String legalName, String userName, String password) {
		super();
		this.legalName = legalName;
		this.userName = userName;
		this.password = password;
		this.type = type;
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
    public String getType() {
		return type;
	}
	
	// Αφηρημένες μέθοδοι για marshal/unmarshal 
    public abstract String marshal();

    public abstract void unmarshal(String data);
}

	
	

