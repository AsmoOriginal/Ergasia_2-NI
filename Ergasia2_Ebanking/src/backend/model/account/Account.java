package backend.model.account;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import backend.storage.Storable;
import backend.model.transaction.*;
import backend.model.user.Customer;


public abstract class Account implements Storable {
	private  String type;  // "BusinessAccount" , "PrivateAccount"
	protected String iban;    // Μοναδικός 20ψήφιος κωδικός (παράγεται από το σύστημα) 
	protected Customer primaryOwner; // Κύριος κάτοχος (VatNumber για αυτο ειναι Customer και οχι String) 
	protected LocalDate dateCreated;
	protected BigDecimal interestRate;
	protected BigDecimal balance;           // Τρέχον υπόλοιπο     
	protected BigDecimal accruedInterest;
	private static  long nextId = 100000000000000L;
	private List<Transaction> transactions;
	
	
	public Account() {
	
	}

	public Account(String type, String iban, Customer primaryOwner, LocalDate dateCreated, BigDecimal interestRate,
			BigDecimal balance) {
		super();
		this.type = type;
		this.iban = iban;
		this.primaryOwner = primaryOwner;
		this.dateCreated = dateCreated;
		this.interestRate = interestRate;
		this.balance = balance;
		this.accruedInterest = BigDecimal.ZERO;
		 
	}
	
	public Customer getPrimaryOwner() {
		return primaryOwner;
	}
	public void setPrimaryOwner(Customer primaryOwner) {
		this.primaryOwner = primaryOwner;
	}
	public BigDecimal getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(BigDecimal interestRate) {
		this.interestRate = interestRate;
	}

	public String getIban() {
		return iban;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public LocalDate getDateCreated() {
		return dateCreated;
	}
	
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public String getType() {
		return type;
	}
	
	public BigDecimal getAccruedInterest() {
	    return accruedInterest;
	}
	
	public void setIban(String iban) {
		this.iban = iban;
	}

	public static void setNextId(long nextId) {
		Account.nextId = nextId;
	}

	// Αφηρημένες μέθοδοι για marshal/unmarshal 
    public abstract String marshal();

    public abstract void unmarshal(String data);
	
    protected static String generateIban(String accountTypeCode) {
        String countryCode = "GR";
        String accountId = String.format("%015d", nextId++); // αύξων αριθμός 15 ψηφίων
        return countryCode + accountTypeCode + accountId;
    }
    
    

    protected static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void accrueDailyInterest() {
    	
	    BigDecimal dailyRate = interestRate.divide(BigDecimal.valueOf(365), MathContext.DECIMAL64);
	    BigDecimal interestForToday = balance.multiply(dailyRate);
	   
	    		setBalance(getBalance()
	    		    .add(accruedInterest)
	    		    .setScale(2, RoundingMode.HALF_UP));

	    accruedInterest = accruedInterest.add(interestForToday);
	}
	
    public void applyMonthlyInterest() {
        balance = balance.add(accruedInterest);           // Προσθέτει τους τόκους στο υπόλοιπο
        accruedInterest = BigDecimal.ZERO;                // Μηδενίζει τους σωρευμένους τόκους
    }

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}
    
	
}
