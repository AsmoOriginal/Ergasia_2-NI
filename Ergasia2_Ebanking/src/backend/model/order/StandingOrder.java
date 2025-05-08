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
	private String getOrderId() {
		return orderId;
	}
	private void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	private Account getSourceAccount() {
		return sourceAccount;
	}
	private void setSourceAccount(Account sourceAccount) {
		this.sourceAccount = sourceAccount;
	}
	private BigDecimal getAmount() {
		return amount;
	}
	private void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	private LocalDate getStartDate() {
		return startDate;
	}
	private void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	private LocalDate getEndDate() {
		return endDate;
	}
	private void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	private int getIntervalDays() {
		return intervalDays;
	}
	private void setIntervalDays(int intervalDays) {
		this.intervalDays = intervalDays;
	}
	private boolean isActive() {
		return active;
	}
	private void setActive(boolean active) {
		this.active = active;
	}
	
	
    
}
