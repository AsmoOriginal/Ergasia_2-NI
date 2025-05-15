package backend.model.order;

import backend.manager.AccountManager;
import backend.manager.UserManager;
import backend.model.account.Account;
import backend.model.transaction.Transaction;
import backend.storage.Storable;
import java.math.BigDecimal;
import java.time.LocalDate;

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

	//Marshal the common variables of TransferOrder and PaymentOrder
	public String commonMarshal() {
        return String.format("type:%s,orderId:%s,title:%s,description:%s,customer:%s,startDate:%s,endDate:%s,fee:,chargeAccount:%s",
                type, orderId, title, description, startDate.toString(), endDate.toString(), fee.toPlainString(), chargeAccount);
        //have to make startDate and endDate Strings 
    }
	
	//create a map to split the value form : type and link them so it doesn't matter the way it is written in the csv file 
	public static Map<String, String> parseLine(String line) {
        Map<String, String> map = new HashMap<>();
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
	public void commonUnmarshal(Map<String, String> map) {
        this.orderId = map.get("orderId");
        this.title = map.get("title");
        this.description = map.get("description");
        this.customer = UserManager.getInstance().findUserByVat(map.get("customer"));
        this.startDate = LocalDate.parse(map.get("startDate"));
        this.endDate = LocalDate.parse(map.get("endDate"));
        this.fee = new BigDecimal(map.get("fee"));
        this.chargeAccount = AccountManager.getInstance().getAccountByIban(map.get("chargeAccount"));
        this.creditAccount = AccountManager.getInstance().getAccountByIban(map.get("creditAccount"));
    }
	
	
	public abstract boolean shouldExecute(LocalDate currentDate);

    public abstract List<Transaction> execute(LocalDate currentDate);
	
	
    
    
    
}