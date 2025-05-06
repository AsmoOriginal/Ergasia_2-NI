package backend.model.account;

import java.math.BigDecimal; 
import backend.model.user.Company;
import backend.model.user.Customer;

public class BusinessAccount extends Account{

	private BigDecimal maintenanceFee; // Μηνιαίο τέλος διατήρησης 
	
	public BusinessAccount(String iban, Customer primaryHolder, BigDecimal balance, BigDecimal interestRate, BigDecimal maintenanceFee ) {
		
		super(iban, primaryHolder, balance, interestRate);
		this.maintenanceFee = maintenanceFee;
		
	}
	

	public BigDecimal getMaintenanceFee() {
		return maintenanceFee;
	}

	public void setMaintenanceFee(BigDecimal maintenanceFee) {
		this.maintenanceFee = maintenanceFee;
	}
	
	

}
