package backend.model.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
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
	public boolean shouldExecute(LocalDate date) {
	    

	    if (date.isBefore(startDate) || date.isAfter(endDate)) {
	       
	        return false;
	    }

	    if (date.getDayOfMonth() != this.dayOfMonth) {
	    	return false;
	    }

	    long monthsBetween = ChronoUnit.MONTHS.between(startDate.withDayOfMonth(1), date.withDayOfMonth(1));
	    boolean execute = monthsBetween % frequencyInMonths == 0;
	   

	    return execute;
	}



	@Override
	public List<Transaction> execute(LocalDate currentDate) {
	    if (shouldExecute(currentDate)) {
	       

	        Account creditAccountObj = AccountManager.getInstance().getAccountByIban(getCreditAccount().getIban());

	        Transfer transferTransaction = new Transfer(
	            getChargeAccount(),
	            creditAccountObj,
	            amount,
	            senderNote,
	            receiverNote
	        );

	        List<Transaction> transactions = new ArrayList<>();
	        transactions.add(transferTransaction);

	        return transactions;
	    } else {
	        return new ArrayList<>();
	    }
	}

}