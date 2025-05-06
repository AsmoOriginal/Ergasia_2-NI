package backend.model.transaction;

import java.math.BigDecimal; 
import java.time.LocalDateTime; 
import backend.model.account.Account; 

public abstract class Transaction {
	private String id;                       // Μοναδικό αναγνωριστικό συναλλαγής 
	private LocalDateTime dateTime;   // Ημερομηνία και ώρα συναλλαγής        
	private BigDecimal amount;         // Ποσό συναλλαγής      
	private Account fromAccount;          // Λογαριασμός χρέωσης (εάν υπάρχει)    
	private Account toAccount;             // Λογαριασμός πίστωσης (εάν υπάρχει)  
	 
}
