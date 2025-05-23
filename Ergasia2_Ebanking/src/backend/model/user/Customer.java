package backend.model.user;

import backend.model.account.*;
import java.util.*;

public abstract class Customer extends User {

    private String vatNumber;    // ΑΦΜ - μοναδικός αναγνωριστικός αριθμός πελάτη 
    private List<Account> accounts = new ArrayList<>() ;



    public Customer() {

    }

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



    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

        // Υλοποίηση της μεθόδου marshal
        @Override
        public String marshal() {
             return super.marshal() + ",vatNumber:" + vatNumber;
        }

        // Υλοποίηση της μεθόδου unmarshal
        @Override
        public void unmarshal(String data) {
             super.unmarshal(data); // φορτώνει κοινά πεδία

                // Φόρτωση επιπλέον πεδίου για Customer
                String[] parts = data.split(",");
                for (String part : parts) {
                    String[] keyValue = part.split(":", 2);
                    if (keyValue.length == 2 && keyValue[0].trim().equalsIgnoreCase("vatNumber")) {
                        this.vatNumber = keyValue[1].trim();
                    }
                }
        }





}