package backend.model.account;

import java.math.BigDecimal;
import java.time.LocalDate;

import backend.manager.UserManager;
import backend.model.user.Customer;



public class BusinessAccount extends Account{
	
     private BigDecimal maintenanceFee; // Μηνιαίο τέλος διατήρησης 
     
     //default constructor
     public BusinessAccount() {
    	    super("BusinessAccount", "", null, LocalDate.now(), BigDecimal.ZERO, BigDecimal.ZERO);
    	    this.maintenanceFee = BigDecimal.ZERO;
    	}
     
     public BusinessAccount(String primaryOwner, BigDecimal interestRate) {
    	    super("BusinessAccount", generateIban("200"), primaryOwner, LocalDate.now(), interestRate, BigDecimal.ZERO);
    	}
     
	public BigDecimal getMaintenanceFee() {
		return maintenanceFee;
	}

	public void setMaintenanceFee(BigDecimal maintenanceFee) {
		this.maintenanceFee = maintenanceFee;
	}
	

	
	 @Override
	    public String marshal() {
	        StringBuilder sb = new StringBuilder();
	        sb.append("type:BusinessAccount");
	        sb.append(",iban:").append(iban);
	        sb.append(",primaryOwner:").append(primaryOwner);
	        sb.append(",dateCreated:").append(dateCreated);
	        sb.append(",rate:").append(interestRate);
	        sb.append(",balance:").append(balance);
	       
	        return sb.toString();
	    }

	 @Override
	 public void unmarshal(String data) {
	     String[] parts = data.split(",");

	     if (parts.length < 6) {
	         throw new IllegalArgumentException("Invalid data format");
	     }

	     String iban = parts[1].split(":")[1];
	     String vat = parts[2].split(":")[1];
	     LocalDate dateCreated = LocalDate.parse(parts[3].split(":")[1]);
	     BigDecimal rate = new BigDecimal(parts[4].split(":")[1]);
	     BigDecimal balance = new BigDecimal(parts[5].split(":")[1]);
	     BigDecimal fee = new BigDecimal(parts[6].split(":")[1]);

	     this.iban = iban;
	     this.primaryOwner = vat; 
	     this.dateCreated = dateCreated;
	     this.interestRate = rate;
	     this.balance = balance;
	     this.maintenanceFee = fee;
	 }
	 
	

     
}