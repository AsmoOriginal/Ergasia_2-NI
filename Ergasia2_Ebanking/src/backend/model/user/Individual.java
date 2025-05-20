package backend.model.user;


public class Individual extends Customer {
    private Customer coOwner;
	public Individual() {
		
	}

	public Individual(String legalName, String userName, String password, String vatNumber) {
        super("Individual", legalName, userName, password, vatNumber);
    }

    @Override
    public String marshal() {
        return super.marshal();
    }

    @Override
    public void unmarshal(String data) {
        super.unmarshal(data);
    }

	public Customer getCoOwner() {
		return coOwner;
	}

	public void setCoOwner(Customer coOwner) {
		this.coOwner = coOwner;
	}
}
