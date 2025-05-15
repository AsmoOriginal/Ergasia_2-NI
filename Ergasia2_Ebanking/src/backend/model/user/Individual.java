package backend.model.user;


public class Individual extends Customer {

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
}
