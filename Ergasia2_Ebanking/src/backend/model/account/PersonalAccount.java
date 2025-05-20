package backend.model.account;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import backend.model.user.*;
import backend.manager.UserManager;




public class PersonalAccount extends Account {
	private List<Customer> secondaryHolders; // Δευτερεύοντες κάτοχοι (μόνο φυσικά πρόσωπα)

	//default constructor
	public PersonalAccount() {
	    super("PersonalAccount", "", null, LocalDate.now(), BigDecimal.ZERO, BigDecimal.ZERO);
	    this.secondaryHolders = new ArrayList<>();
	}

	public PersonalAccount(Customer primaryOwner, BigDecimal interestRate,  List<Customer> secondaryHolders) {
		super("PersonalAccount", generateIban("100"), primaryOwner, LocalDate.now(), interestRate, BigDecimal.ZERO);
		this.secondaryHolders = (secondaryHolders != null) ? secondaryHolders : new ArrayList<>();
	}

	public List<Customer> getSecondaryHolders() {
		return secondaryHolders;
	}

	public void setSecondaryHolders(List<Customer> secondaryHolders) {
		this.secondaryHolders = secondaryHolders;
	}

	public boolean add(Customer holderId) {
		return secondaryHolders.add(holderId);
	}

	public boolean remove(Customer holderId) {
		return secondaryHolders.remove(holderId);
	}

	 @Override
	    public String marshal() {
	        StringBuilder sb = new StringBuilder();
	        sb.append("type:PersonalAccount");
	        sb.append(",iban:").append(iban);
	        sb.append(",primaryOwner:").append(primaryOwner.getVatNumber());
	        sb.append(",dateCreated:").append(dateCreated.format(DATE_FORMAT));
	        sb.append(",rate:").append(interestRate);
	        sb.append(",balance:").append(balance);
	         
	            for (Customer holder : secondaryHolders) {
	                sb.append(",coOwner:").append(holder.getVatNumber());
	            }
	        
	        return sb.toString();
	    }
	
	 @Override
	 public void unmarshal(String data) {
		    String[] parts = data.split(","); // Διαχωρίζουμε με κόμμα τις τιμές

		    String iban = "", vat = "";
	        BigDecimal rate = BigDecimal.ZERO;
	        BigDecimal balance = BigDecimal.ZERO;
	        LocalDate date = null;
	        List<Customer> coOwners = new ArrayList<>();
	        
	        for (String part : parts) {
	            String[] keyVal = part.split(":", 2);
	            if (keyVal.length != 2) continue;
		
	            switch (keyVal[0]) {
                case "iban": iban = keyVal[1]; break;
                case "primaryOwner": vat = keyVal[1]; break;
                case "dateCreated": date = LocalDate.parse(keyVal[1], DATE_FORMAT); break;
                case "rate": rate = new BigDecimal(keyVal[1]); break;
                case "balance": balance = new BigDecimal(keyVal[1]); break;
                case "coOwner":  Customer c = UserManager.getInstance().findUserByVat(keyVal[1]);
                if (c != null) {
                    coOwners.add(c);
                }
                break;
               }
	        }
	        this.iban = iban;
	        this.primaryOwner = UserManager.getInstance().findUserByVat(vat); 
	        this.dateCreated = date;
	        this.interestRate = rate;
	        this.balance = balance;
	        this.secondaryHolders = coOwners;
	    }
	

	

}