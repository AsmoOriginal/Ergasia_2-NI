package backend.model.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import backend.manager.AccountManager;
import backend.model.account.Account;
import backend.model.transaction.Transaction;
import backend.model.transaction.Transfer;
import backend.model.user.Customer;



public class TransferOrder extends StandingOrder {
	
	private BigDecimal amount;    //Ποσό μεταφοράς
    private int frequencyInMonths;  //Συχνότητα εκτέλεσης (π.χ. κάθε 2 μήνες)
    private int dayOfMonth;        //Ποια ημέρα του μήνα θα εκτελείται
	private String senderNote;
	private String receiverNote;
    

	

	

	public TransferOrder() {
		
	}

	public TransferOrder( String orderId, String title, String description, Customer customer,
			LocalDate startDate, LocalDate endDate, BigDecimal fee, Account chargeAccount, Account creditAccount,
			boolean isActive, BigDecimal amount, int frequencyInMonths, int dayOfMonth, String senderNote,
			String receiverNote) {
		super("TransferOrder", orderId, title, description, customer, startDate, endDate, fee, chargeAccount, creditAccount,
				isActive);
		this.amount = amount;
		this.frequencyInMonths = frequencyInMonths;
		this.dayOfMonth = dayOfMonth;
		this.senderNote = senderNote;
		this.receiverNote = receiverNote;
	}

	

	
	
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	

	public int getFrequencyInMonths() {
		return frequencyInMonths;
	}

	public void setFrequencyInMonths(int frequencyInMonths) {
		this.frequencyInMonths = frequencyInMonths;
	}

	public int getDayOfMonth() {
		return dayOfMonth;
	}

	public void setDayOfMonth(int dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}
	
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
	public String marshal() {
	    return super.marshal()
	        + " amount:" + amount.toPlainString()
	        + " creditAccount:" + getCreditAccount().getIban()
	        + " frequencyInMonths:" + frequencyInMonths
	        + " dayOfMonth:" + dayOfMonth;
	}

	@Override
	public void unmarshal(String line) {
	    // Κλήση της unmarshal της StandingOrder
	    super.unmarshal(line);

	    // Parse για να πιάσουμε τα δικά μας πεδία
	    Map<String, String> map = parseLine(line);

        this.amount =  new BigDecimal((map.get("amount")));
        this.frequencyInMonths = Integer.parseInt(map.get("frequencyInMonths"));
        this.dayOfMonth = Integer.parseInt(map.get("dayOfMonth"));


    }
    
	

	@Override
	public boolean shouldExecute(LocalDate currentDate) {
	    
	    return isActive() && (currentDate.isAfter(getStartDate()) || currentDate.isEqual(getStartDate())) 
	            && (currentDate.isBefore(getEndDate()) || currentDate.isEqual(getEndDate()));
	}

	@Override
	public List<Transaction> execute(LocalDate currentDate) {
	    // Ελέγχει αν η εντολή πρέπει να εκτελεστεί
	    if (shouldExecute(currentDate)) {
	        // Ανακτά τον λογαριασμό με βάση το IBAN
	        Account creditAccountObj = AccountManager.getInstance().getAccountByIban(getCreditAccount().getIban());
	        
	        // Δημιουργία μιας νέας συναλλαγής για τη μεταφορά
	        Transfer transferTransaction = new Transfer(
	        		getChargeAccount(),        // Λογαριασμός από
                    creditAccountObj,        // Λογαριασμός προς
                    amount,                  // Ποσό
                    senderNote,              // Σημείωση αποστολέα
                    receiverNote             // Σημείωση παραλήπτη
	        );
	        
	        // Επιστρέφει μια λίστα με τις συναλλαγές που δημιουργήθηκαν
	        List<Transaction> transactions = new ArrayList<>();
	        transactions.add(transferTransaction);
	        
	        
	        
	        return transactions;
	    } else {
	        return new ArrayList<>(); // Αν δεν πρέπει να εκτελεστεί, επιστρέφει κενή λίστα
	    }
	}	
}