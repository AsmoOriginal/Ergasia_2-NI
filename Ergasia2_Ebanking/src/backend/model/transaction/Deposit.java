package backend.model.transaction;

import backend.model.account.Account; 
import java.math.BigDecimal; 
import java.time.LocalDateTime;

public class Deposit extends Transaction{
	private String depositorName;   // Όνομα καταθέτη (αν υπάρχει, π.χ. εξωτερικός)

	public Deposit(String id, LocalDateTime dateTime, BigDecimal amount, Account fromAccount, Account toAccount,
			String depositorName) {
		super(id, dateTime, amount, fromAccount, toAccount);
		this.depositorName = depositorName;
	}

	private String getDepositorName() {
		return depositorName;
	}

	private void setDepositorName(String depositorName) {
		this.depositorName = depositorName;
	}
	
	
}
