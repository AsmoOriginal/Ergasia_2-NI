package backend.model.transaction;

import backend.model.account.Account;
import backend.model.bill.Bill; 
import java.math.BigDecimal; 
import java.time.LocalDateTime;

public class Payment extends Transaction {
	private Bill bill;  // Ο λογαριασμός που εξοφλείται με την πληρωμή 

	public Payment(String id, LocalDateTime dateTime, BigDecimal amount, Account fromAccount, Account toAccount,
			Bill bill) {
		super(id, dateTime, amount, fromAccount, toAccount);
		this.bill = bill;
	}

	private Bill getBill() {
		return bill;
	}

	private void setBill(Bill bill) {
		this.bill = bill;
	}

	
	
}
