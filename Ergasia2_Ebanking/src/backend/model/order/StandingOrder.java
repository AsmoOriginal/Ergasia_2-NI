package backend.model.order;

import backend.manager.AccountManager;
import backend.manager.UserManager;
import backend.model.account.Account;
import backend.model.transaction.Transaction;
import backend.storage.Storable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import backend.model.user.Customer;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

public abstract class StandingOrder implements Storable{
	private String type;            
    private String orderId;
    private String title;
    private String description;
    private Customer customer;
    private LocalDate startDate;        // Ημερομηνία έναρξης
    private LocalDate endDate;           // Ημερομηνία λήξης
    private BigDecimal fee;
    private Account chargeAccount; //account that gets charged
    private Account creditAccount;
    private boolean isActive;
    private String rawData;
    
    
    public StandingOrder() {
		
	}

	//Constructor
	public StandingOrder(String type, String orderId, String title, String description, Customer customer,
			LocalDate startDate, LocalDate endDate, BigDecimal fee, Account chargeAccount,Account creditAccount, boolean isActive) {
		super();
		this.type = type;
		this.orderId = orderId;
		this.title = title;
		this.description = description;
		this.customer = customer;
		this.startDate = startDate;
		this.endDate = endDate;
		this.fee = fee;
		this.chargeAccount = chargeAccount;
		this.creditAccount= creditAccount;
		this.isActive = isActive;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public Account getChargeAccount() {
		return chargeAccount;
	}

	public void setChargeAccount(Account chargeAccount) {
		this.chargeAccount = chargeAccount;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	
	public Account getCreditAccount() {
		return creditAccount;
	}

	public void setCreditAccount(Account creditAccount) {
		this.creditAccount = creditAccount;
	}

	@Override
	public String marshal() {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	    return "type=" + type
	        + ",orderId=" + orderId
	        + ",title=" + title
	        + ",description=" + description
	        + ",customerVat=" + (customer != null ? customer.getVatNumber() : "null")
	        + ",startDate=" + (startDate != null ? startDate.format(formatter) : "null")
	        + ",endDate=" + (endDate != null ? endDate.format(formatter) : "null")
	        + ",fee=" + (fee != null ? fee.toPlainString() : "0.00")
	        + ",chargeAccount=" + (chargeAccount != null ? chargeAccount.getIban() : "null");
	}




	
	//create a map to split the value form : type and link them so it doesn't matter the way it is written in the csv file 
	public static Map<String, String> parseLine(String line) {
        Map<String, String> map = new HashMap<>();
        
        if (line.startsWith("PaymentOrder") || line.startsWith("TransferOrder")) {
            line = line.substring(2);
        }
        
        String[] keyPairs = line.split(",");
        for (String pair : keyPairs) {
            String[] entryPair = pair.split(":", 2);
            if (entryPair.length == 2) {
                map.put(entryPair[0], entryPair[1]);
            }
        }
        return map;
    }
	
	//Unmarshal the common variables of TransferOrder and PaymentOrder
	@Override
	public void unmarshal(String line) {
	    Map<String, String> map = parseLine(line);

	    this.type = map.get("type");
	    this.orderId = map.get("orderId");
	    this.title = map.get("title");
	    this.description = map.get("description");

	    // Ανάκτηση πελάτη με VAT μέσω UserManager
	    String vat = map.get("customer");
	    this.customer = UserManager.getInstance().findUserByVat(vat);

	    // Ανάκτηση λογαριασμών μέσω IBAN
	    String chargeIban = map.get("chargeAccount");
	    this.chargeAccount = AccountManager.getInstance().getAccountByIban(chargeIban);

	    String creditIban = map.get("creditAccount");
	    this.creditAccount = AccountManager.getInstance().getAccountByIban(creditIban);

	    
	    
	    this.fee = new BigDecimal(map.get("fee"));
	    this.startDate = LocalDate.parse(map.get("startDate"));
	    this.endDate = LocalDate.parse(map.get("endDate"));
	    this.rawData = line; // Φυλάμε το αρχείο ως string
	}

	
	public abstract boolean shouldExecute(LocalDate currentDate);

    public abstract List<Transaction> execute(LocalDate currentDate);
	
	
    public String getRawData() {
        return rawData;
    }
    
    public String getCustomerVat() {
        if (customer != null) {
            return customer.getVatNumber();
        } else {
            return null;
        }
    }

    
}