package backend.model.order;

import backend.model.account.Account;
import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class StandingOrder {
	private String orderId;               // Μοναδικός κωδικός εντολής
    private Account sourceAccount;        // Λογαριασμός χρέωσης
    private BigDecimal amount;            // Ποσό πάγιας εντολής
    private LocalDate startDate;          // Ημερομηνία έναρξης
    private LocalDate endDate;            // Ημερομηνία λήξης
    private int intervalDays;             // Κάθε πόσες ημέρες εκτελείται
    private boolean active;               // Αν η εντολή είναι ενεργή
	public StandingOrder(String orderId, Account sourceAccount, BigDecimal amount, LocalDate startDate,
			LocalDate endDate, int intervalDays, boolean active) {
		super();
		this.orderId = orderId;
		this.sourceAccount = sourceAccount;
		this.amount = amount;
		this.startDate = startDate;
		this.endDate = endDate;
		this.intervalDays = intervalDays;
		this.active = active;
	}

	
    
}
