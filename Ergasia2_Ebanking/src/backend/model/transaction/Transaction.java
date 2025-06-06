package backend.model.transaction;

import java.math.BigDecimal; 
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import backend.model.account.Account;
import backend.storage.Storable; 

public abstract class Transaction implements Storable{
	private final String type; 
    protected Account fromAccount;          // Λογαριασμός χρέωσης (εάν υπάρχει)    
	protected Account toAccount; // Λογαριασμός πίστωσης (εάν υπάρχει)
    private String id;                       // Μοναδικό αναγνωριστικό συναλλαγής     
    protected BigDecimal amount;         // Ποσό συναλλαγής      
    protected LocalDateTime dateTime;   // Ημερομηνία και ώρα συναλλαγής    
    
    private String transactor; // π.χ. username ή "system"
    
    
   
    
    
   
	public Transaction(String type, Account fromAccount, Account toAccount,  BigDecimal amount) {
		this.fromAccount = fromAccount;
		this.toAccount = toAccount;
		this.id = UUID.randomUUID().toString();
		this.amount = amount;
		this.dateTime = LocalDateTime.now();
		this.type= type;
			
		
	}


	public String getTransactor() {
		return transactor;
	}


	public void setTransactor(String transactor) {
		this.transactor = transactor;
	}


	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Account getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(Account fromAccount) {
        this.fromAccount = fromAccount;
    }

    public Account getToAccount() {
        return toAccount;
    }

    public void setToAccount(Account toAccount) {
        this.toAccount = toAccount;
    }
    
    public String getType() {
		return type;
	}


	public List<Account> getInvolvedAccounts() {
        List<Account> accounts = new ArrayList<>();
        if (fromAccount != null) accounts.add(fromAccount);
        if (toAccount != null && !toAccount.equals(fromAccount)) accounts.add(toAccount);
        return accounts;
    }

    //method that will be used in all subclasses
    public abstract boolean execute();
    
 // Μέθοδος που επιστρέφει τη μορφοποιημένη ημερομηνία και ώρα της συναλλαγής
    public String getFormattedDateTime() {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
 // Αφηρημένες μέθοδοι για marshal/unmarshal 
    public abstract String marshal();

    public abstract void unmarshal(String data);
    
    protected Map<String, String> parseStringToMap(String data) {
        Map<String, String> map = new HashMap<>();
        String[] entries = data.split(",");

        for (String entry : entries) {
            String[] keyValue = entry.split(":", 2); // μόνο το πρώτο :
            if (keyValue.length == 2) {
                map.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }

        return map;
    }

}