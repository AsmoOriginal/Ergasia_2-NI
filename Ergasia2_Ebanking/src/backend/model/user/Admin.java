package backend.model.user;

import java.util.HashMap;
import java.util.Map;

public  class Admin extends User {

	public Admin( String legalName, String userName, String password) {
		super("Admin", legalName, userName, password);
	}

	 @Override
	    public String marshal() {
	        return "type:" + this.getType() +
	               ", legalName:" + this.getLegalName() +
	               ", userName:" + this.getUserName() +
	               ", password:" + this.getPassword();
	    }

	 @Override
	 public void unmarshal(String data) {
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
