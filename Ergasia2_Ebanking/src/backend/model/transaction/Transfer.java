package backend.model.transaction;

import backend.model.account.Account; 
import java.math.BigDecimal; 
import java.time.LocalDateTime;

public class Transfer extends Transaction {
	private String transferTitle;   // Περιγραφή ή τίτλος μεταφοράς (π.χ. "Ενοίκιο", "Μεταφορά σε αποταμιευτικό")

	public Transfer(String id, LocalDateTime dateTime, BigDecimal amount, Account fromAccount, Account toAccount,
			String transferTitle) {
		super(id, dateTime, amount, fromAccount, toAccount);
		this.transferTitle = transferTitle;
	}

	private String getTransferTitle() {
		return transferTitle;
	}

	private void setTransferTitle(String transferTitle) {
		this.transferTitle = transferTitle;
	}
	
	
}
