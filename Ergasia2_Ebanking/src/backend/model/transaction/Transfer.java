package backend.model.transaction;

import backend.manager.AccountManager;
import backend.model.account.Account; 
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.logging.Logger;
import java.util.*;
public class Transfer extends Transaction {
	private String senderNote;
    private String receiverNote;

    
    public Transfer(Account fromAccount, Account toAccount, BigDecimal amount,String senderNote, String receiverNote) {
		super("Tranfer",fromAccount, toAccount, amount);
		this.senderNote = senderNote;
		this.receiverNote = receiverNote;
	}

    private static final Logger logger = Logger.getLogger(Transfer.class.getName());

	public String getSenderNote() {
		return senderNote;
	}
	public void setSenderNote(String senderNote) {
		this.senderNote = senderNote;
	}
	public String getReceiverNote() {
		return receiverNote;
	}
	public void setReceiverNote(String receiverNote) {
		this.receiverNote = receiverNote;
	}



	

	
	@Override
	public boolean execute() {
	    // Ελέγχει αν η συναλλαγή είναι έγκυρη
	    if (!isValidTransaction()) {
	        return false;
	    }

	    // Επεξεργάζεται τη συναλλαγή
	    try {
	        processTransaction();
	        return true;
	    } catch (Exception e) {
	        logger.severe("Transaction failed: " + e.getMessage());
	        return false;
	    }
	}

	// Βοηθητική μέθοδος για την επαλήθευση εγκυρότητας της συναλλαγής
	private boolean isValidTransaction() {
	    // Έλεγχοι για εγκυρότητα των δεδομένων συναλλαγής
	    if (fromAccount == null || toAccount == null || amount == null) {
	        logger.severe("Invalid transaction data: One or more required fields are missing.");
	        return false;
	    }

	    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
	        logger.severe("The amount must be positive.");
	        return false;
	    }

	    if (fromAccount.getBalance().compareTo(amount) < 0) {
	        logger.severe("Insufficient funds in the from account.");
	        return false;
	    }

	    return true;
	}

	// Βοηθητική μέθοδος για την εκτέλεση της συναλλαγής
	private void processTransaction() {
	    fromAccount.setBalance(fromAccount.getBalance().subtract(amount));  // Μείωση του ποσού από τον λογαριασμό χρέωσης
	    toAccount.setBalance(toAccount.getBalance().add(amount));           // Προσθήκη του ποσού στον λογαριασμό πίστωσης
	}

	@Override
	public String marshal() {
	    return String.join(",",
	        "id:" + getId(),
	        "type:" + getType(),
	        "fromIban:" + (getFromAccount() != null ? getFromAccount().getIban() : "null"),
	        "toIban:" + (getToAccount() != null ? getToAccount().getIban() : "null"),
	        "amount:" + getAmount().toPlainString(),
	        "dateTime:" + getDateTime().toString(),
	        "transactor:" + (getTransactor() != null ? getTransactor() : "null"),
	        "senderNote:" + (senderNote != null ? senderNote : "null"),
	        "receiverNote:" + (receiverNote != null ? receiverNote : "null")
	    );
	}


	@Override
	public void unmarshal(String data) {
	    Map<String, String> map = parseStringToMap(data);

	    Transfer transfer = new Transfer(
	        AccountManager.getInstance().getAccountByIban(map.get("fromIban")),
	        AccountManager.getInstance().getAccountByIban(map.get("toIban")),
	        new BigDecimal(map.get("amount")),
	        !"null".equals(map.get("senderNote")) ? map.get("senderNote") : null,
	        !"null".equals(map.get("receiverNote")) ? map.get("receiverNote") : null
	    );

	    transfer.setId(map.get("id"));
	    transfer.setDateTime(LocalDateTime.parse(map.get("dateTime")));
	    transfer.setTransactor(!"null".equals(map.get("transactor")) ? map.get("transactor") : null);

	    // Αν δεν επιστρέφεις αντικείμενο αλλά γεμίζεις το τρέχον instance
	    this.setId(transfer.getId());
	    this.setFromAccount(transfer.getFromAccount());
	    this.setToAccount(transfer.getToAccount());
	    this.setAmount(transfer.getAmount());
	    this.setDateTime(transfer.getDateTime());
	    this.setTransactor(transfer.getTransactor());
	    this.setSenderNote(transfer.getSenderNote());
	    this.setReceiverNote(transfer.getReceiverNote());
	}

    }