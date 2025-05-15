package backend.model.transaction;

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
	public Map<String, String> marshal() {
	    Map<String, String> data = new HashMap<>();
	    data.put("id", getId());
	    data.put("type", getType());
	    data.put("fromIban", getFromAccount() != null ? getFromAccount().getIban() : "null");
	    data.put("toIban", getToAccount() != null ? getToAccount().getIban() : "null");
	    data.put("amount", getAmount().toPlainString());
	    data.put("dateTime", getDateTime().toString());
	    data.put("transactor", getTransactor() != null ? getTransactor() : "null");
	    data.put("senderNote", senderNote != null ? senderNote : "null");
	    data.put("receiverNote", receiverNote != null ? receiverNote : "null");
	    return data;
	}

	@Override
	public Transaction unmarshal(Map<String, String> data) {
	    // Δημιουργία του αντικειμένου Transfer (ή άλλης συναλλαγής αν είναι διαφορετική)
	    Transfer transfer = new Transfer(
	        backend.manager.AccountManager.getInstance().getAccountByIban(data.get("fromIban")),
	        backend.manager.AccountManager.getInstance().getAccountByIban(data.get("toIban")),
	        new BigDecimal(data.get("amount")),
	        !"null".equals(data.get("senderNote")) ? data.get("senderNote") : null,
	        !"null".equals(data.get("receiverNote")) ? data.get("receiverNote") : null
	    );

	    transfer.setId(data.get("id"));
	    transfer.setDateTime(LocalDateTime.parse(data.get("dateTime")));
	    transfer.setTransactor(!"null".equals(data.get("transactor")) ? data.get("transactor") : null);

	    // Επιστροφή του αντικειμένου Transfer
	    return transfer;
	}

    }
    
    
