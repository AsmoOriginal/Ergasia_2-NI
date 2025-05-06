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

}
