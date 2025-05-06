package backend.model.transaction;

import backend.model.account.Account; 
import java.math.BigDecimal; 
import java.time.LocalDateTime;

public class Withdrawal extends Transaction{

	private String withdrawalMethod;  // Μέθοδος ανάληψης (π.χ. ATM, ταμείο)

	public Withdrawal(String id, LocalDateTime dateTime, BigDecimal amount, Account fromAccount, Account toAccount,
			String withdrawalMethod) {
		super(id, dateTime, amount, fromAccount, toAccount);
		this.withdrawalMethod = withdrawalMethod;
	}

	private String getWithdrawalMethod() {
		return withdrawalMethod;
	}

	private void setWithdrawalMethod(String withdrawalMethod) {
		this.withdrawalMethod = withdrawalMethod;
	}
	
	
}
