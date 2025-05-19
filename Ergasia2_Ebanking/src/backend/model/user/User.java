package backend.model.user;

import java.util.HashMap;
import java.util.Map;

import backend.storage.Storable;

public abstract class User implements Storable{
	 
	
	private String legalName;   // ονοματεπώνυμο ή επωνυμία πελάτη 
	private String userName;
	private String password;     //  κωδικός 
	private  String type;  // "Individual", "Company", "Admin"
	
	
	
	public User() {
		
	}
	
	
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
    @Override
    public  String marshal() {
    	 return "type:"+ getType() + ",legalName:" + getLegalName() +
	               ", userName:" + getUserName() +
	               ", password:" + getPassword();
    }
    
@Override
    public  void unmarshal(String data) {
    	 Map<String, String> map = new HashMap<>();
	     String[] parts = data.split(",");

	     for (String part : parts) {
	         String[] keyValue = part.split(":", 2); // split μόνο στο πρώτο ':'
	         if (keyValue.length == 2) {
	             map.put(keyValue[0].trim(), keyValue[1].trim());
	         }
	     }

	     this.setLegalName(map.get("legalName"));
	     this.setUserName(map.get("userName"));
	     this.setPassword(map.get("password"));
	 
    }
}

	
	

