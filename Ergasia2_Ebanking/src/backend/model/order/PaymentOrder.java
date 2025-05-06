package backend.model.order;

import java.math.BigDecimal;
import java.time.LocalDate;

import backend.model.account.Account;
import backend.model.bill.Bill;

public class PaymentOrder extends StandingOrder {
	private Bill targetBill;              // Προς ποιον λογαριασμό πληρωμής γίνεται η πάγια

	public PaymentOrder(String orderId, Account sourceAccount, BigDecimal amount, LocalDate startDate,
			LocalDate endDate, int intervalDays, boolean active, Bill targetBill) {
		super(orderId, sourceAccount, amount, startDate, endDate, intervalDays, active);
		this.targetBill = targetBill;
	}

	private Bill getTargetBill() {
		return targetBill;
	}

	private void setTargetBill(Bill targetBill) {
		this.targetBill = targetBill;
	}
	
	//
	
}
