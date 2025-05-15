package backend.model.user;

import java.util.HashMap;
import java.util.Map;

public abstract class Customer extends User {
	
	private String vatNumber;    // ΑΦΜ - μοναδικός αναγνωριστικός αριθμός πελάτη 


	public Customer(String type,String legalName, String userName, String password,  String vatNumber) {
		super(type, legalName, userName, password);
		this.vatNumber = vatNumber;	
	}

	public String getVatNumber() {
		return vatNumber;
	}

	public void setVatNumber(String vatNumber) {
		this.vatNumber = vatNumber;
	}

	// Υλοποίηση της μεθόδου marshal
		@Override
		public String marshal() {
			return "type:" + this.getType() + 
		      ", legalName:" + this.getLegalName() + 
		      ", userName:" +  this.getUserName() + 
		      ", password:" + this.getPassword() + 
				", vatNumber:" + this.vatNumber;
		}
		
		// Υλοποίηση της μεθόδου unmarshal
		@Override
		public void unmarshal(String data) {
		    Map<String, String> map = new HashMap<>();
		    String[] parts = data.split(",");

		    for (String part : parts) {
		        String[] keyValue = part.split(":");
		        if (keyValue.length == 2) {
		            map.put(keyValue[0].trim(), keyValue[1].trim());
		        }
		    }

		    this.setLegalName(map.get("legalName"));
		    this.setUserName(map.get("userName"));
		    this.setPassword(map.get("password"));
		    this.vatNumber = map.get("vatNumber");
		}

	
}
