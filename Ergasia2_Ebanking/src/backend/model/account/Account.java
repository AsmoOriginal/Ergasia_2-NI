package backend.model.account;
import java.math.BigDecimal;
import backend.model.user.Customer;
public class Account {
	private String iban;                              
	// Μοναδικός 20ψήφιος κωδικός (παράγεται από το σύστημα) 
	private Customer primaryHolder;       // Κύριος κάτοχος (Individual ή Company)              
	private BigDecimal balance;                    // Τρέχον υπόλοιπο     
	private BigDecimal interestRate;     
}
