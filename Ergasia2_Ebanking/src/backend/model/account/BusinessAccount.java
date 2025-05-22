package backend.model.account;

import java.math.BigDecimal;
import java.time.LocalDate;

import backend.manager.UserManager;
import backend.model.user.Customer;



public class BusinessAccount extends Account{
	
      
     
     //default constructor
     public BusinessAccount() {
    	    super("BusinessAccount", "", null, LocalDate.now(), BigDecimal.ZERO, BigDecimal.ZERO);
    	    
    	}
     
     public BusinessAccount(Customer primaryOwner, BigDecimal interestRate) {
    	    super("BusinessAccount", generateIban("200"), primaryOwner, LocalDate.now(), interestRate, BigDecimal.ZERO);
    	}
     
	 
	

	
	 @Override
	    public String marshal() {
	        StringBuilder sb = new StringBuilder();
	        sb.append("type:BusinessAccount");
	        sb.append(",iban:").append(iban);
	        sb.append(",primaryOwner:").append(primaryOwner.getVatNumber());
	        sb.append(",dateCreated:").append(dateCreated);
	        sb.append(",rate:").append(interestRate);
	        sb.append(",balance:").append(balance);
	       
	        return sb.toString();
	    }

	 @Override
	 public void unmarshal(String data) {
		    String[] parts = data.split(","); // Διαχωρίζουμε σε key:value ζευγάρια

		    String iban = "";
		    String vat = "";
		    BigDecimal rate = BigDecimal.ZERO;
		    BigDecimal balance = BigDecimal.ZERO;
		    LocalDate date = null;

		    for (String part : parts) {
		        String[] keyVal = part.split(":", 2); // Πρώτο μέρος key, δεύτερο value
		        if (keyVal.length != 2) continue;

		        switch (keyVal[0]) {
		            case "iban":iban = keyVal[1]; break;
		            case "primaryOwner":vat = keyVal[1];  break;
		            case "dateCreated": date = LocalDate.parse(keyVal[1], DATE_FORMAT); break;
		            case "rate": rate = new BigDecimal(keyVal[1]); break;
		            case "balance": balance = new BigDecimal(keyVal[1]); break;
		        }
		    }

		    this.iban = iban;
		    this.primaryOwner =UserManager.getInstance().findUserByVat(vat);;
		    this.dateCreated = date;
		    this.interestRate = rate;
		    this.balance = balance;
		}
	 
	

     
}