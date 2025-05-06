package backend.model.transaction;

import java.math.BigDecimal; 
import java.time.LocalDateTime; 
import backend.model.account.Account; 

public abstract class Transaction {
	private String id;                       // Μοναδικό αναγνωριστικό συναλλαγής 
	private LocalDateTime dateTime;   // Ημερομηνία και ώρα συναλλαγής        
	private BigDecimal amount;         // Ποσό συναλλαγής      
	private Account fromAccount;          // Λογαριασμός χρέωσης (εάν υπάρχει)    
	private Account toAccount; 
	// Λογαριασμός πίστωσης (εάν υπάρχει)  
	
	public Transaction(String id, LocalDateTime dateTime, BigDecimal amount, Account fromAccount, Account toAccount) {
		super();
		this.id = id;
		this.dateTime = dateTime;
		this.amount = amount;
		this.fromAccount = fromAccount;
		this.toAccount = toAccount;
	}
	
	private String getId() {
		return id;
	}
	private void setId(String id) {
		this.id = id;
	}
	private LocalDateTime getDateTime() {
		return dateTime;
	}
	private void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}
	private BigDecimal getAmount() {
		return amount;
	}
	private void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	private Account getFromAccount() {
		return fromAccount;
	}
	private void setFromAccount(Account fromAccount) {
		this.fromAccount = fromAccount;
	}
	private Account getToAccount() {
		return toAccount;
	}
	private void setToAccount(Account toAccount) {
		this.toAccount = toAccount;
	}
	 
	
}
