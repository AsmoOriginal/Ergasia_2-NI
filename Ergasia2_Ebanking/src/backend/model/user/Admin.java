package backend.model.user;

public  class Admin extends User {

	public Admin( String legalName, String userName, String password) {
		super("Admin", legalName, userName, password);
	}
	
	@Override
	public String getType() {
	    return "Admin";
	}

	 @Override
	    public String marshal() {
		 return super.marshal(); 
	    }

	 @Override
	 public void unmarshal(String data) {
		 super.unmarshal(data);
	 }
	
	
}
