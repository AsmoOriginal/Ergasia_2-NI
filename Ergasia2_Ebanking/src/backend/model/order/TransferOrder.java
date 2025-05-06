package backend.model.order;

import java.math.BigDecimal;
import java.time.LocalDate;

import backend.model.account.Account;

public class TransferOrder extends StandingOrder {
	private Account targetAccount;        // Σε ποιον λογαριασμό μεταφέρεται το ποσό

	public TransferOrder(String orderId, Account sourceAccount, BigDecimal amount, LocalDate startDate,
			LocalDate endDate, int intervalDays, boolean active, Account targetAccount) {
		super(orderId, sourceAccount, amount, startDate, endDate, intervalDays, active);
		this.targetAccount = targetAccount;
	}

	private Account getTargetAccount() {
		return targetAccount;
	}

	private void setTargetAccount(Account targetAccount) {
		this.targetAccount = targetAccount;
	}

//
	
}
