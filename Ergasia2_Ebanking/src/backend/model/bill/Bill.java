package backend.model.bill;

import java.math.BigDecimal;
import java.time.LocalDate;

import backend.manager.AccountManager;
import backend.manager.UserManager;
import backend.model.account.Account;
import backend.model.account.PersonalAccount;
import backend.model.user.Customer;
import backend.storage.Storable;

public class Bill implements Storable {
    private String billId;            // Μοναδικός αριθμός λογαριασμού
    private String rfCode;            // Κωδικός πληρωμής RF
    private Account issuer;            // Εκδότης (εταιρεία)
    private BigDecimal amount;        // Ποσό
    private LocalDate issueDate;      // Ημερομηνία έκδοσης
    private LocalDate dueDate;        // Λήξη
    private boolean isPaid;           // Έχει πληρωθεί;
    private boolean isActive;         // Ενεργός RF;
    private Account customerVat;       // ΑΦΜ πελάτη
    
   
    
    public Bill() {
		
	}



	public Bill(String rfCode, String billId,  Account issuer, Account customerVat, BigDecimal amount, LocalDate issueDate,LocalDate dueDate) {
        this.billId = billId;
        this.rfCode = rfCode;
        this.issuer = issuer;
        this.amount = amount;
        this.issueDate = issueDate;
        this.customerVat = customerVat;
        this.dueDate = dueDate;
        
    }

   

    public String getBillId() {
		return billId;
	}



	public void setBillId(String billId) {
		this.billId = billId;
	}



	public String getRfCode() {
		return rfCode;
	}



	public void setRfCode(String rfCode) {
		this.rfCode = rfCode;
	}



	public Account getIssuer() {
		return issuer;
	}



	public void setIssuer(Account issuer) {
		this.issuer = issuer;
	}



	public BigDecimal getAmount() {
		return amount;
	}



	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}



	public LocalDate getIssueDate() {
		return issueDate;
	}



	public void setIssueDate(LocalDate issueDate) {
		this.issueDate = issueDate;
	}



	public LocalDate getDueDate() {
		return dueDate;
	}



	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}



	public boolean isPaid() {
		return isPaid;
	}



	public void setPaid(boolean isPaid) {
		this.isPaid = isPaid;
	}



	public boolean isActive() {
		return !isPaid && (dueDate.isAfter(LocalDate.now()) || dueDate.isEqual(LocalDate.now()));
	}



	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}



	public Account getCustomerVat() {
		return customerVat;
	}



	public void setCustomerVat(Account customerVat) {
		this.customerVat = customerVat;
	}



	

    // Marshal: μετατροπή σε string
	@Override
	public String marshal() {
		String paidStatus = isPaid ? "true" : "false";
	    return "type:Bill," +
	           "paymentCode:" + rfCode + "," +
	           "billNumber:" + billId + "," +
	           "issuer:" + (issuer != null && issuer.getPrimaryOwner() != null ? issuer.getPrimaryOwner().getVatNumber() : "null") + "," +
	           "customer:" + (customerVat != null && customerVat.getPrimaryOwner() != null ? customerVat.getPrimaryOwner().getVatNumber() : "null") + "," +
	           "amount:" + amount.toPlainString() + "," +
	           "issueDate:" + issueDate + "," +
	           "dueDate:" + dueDate
	           + ",paid:" + paidStatus;
	}


    // Unmarshal: από string σε αντικείμενο
	@Override
	public void unmarshal(String data) {
	    try {
	        String[] parts = data.split(",");

	        this.rfCode = parts[1].split(":", 2)[1].trim();
	        this.billId = parts[2].split(":", 2)[1].trim();

	        String issuerId = parts[3].split(":", 2)[1].trim();
	        Account issuerAccount = AccountManager.getInstance().getAccountsByVatNumber(issuerId);
	        if (issuerAccount == null) {
	            System.err.println("WARNING: No issuer account found for VAT " + issuerId + ", creating temporary account.");
	            Customer issuerCustomer = UserManager.getInstance().findUserByVat(issuerId);
	            if (issuerCustomer != null) {
	                issuerAccount = new PersonalAccount();
	                issuerAccount.setPrimaryOwner(issuerCustomer);
	            } else {
	                System.err.println("ERROR: No customer found for VAT " + issuerId + ". Cannot set issuer.");
	            }
	        }

	        String custId = parts[4].split(":", 2)[1].trim();
	        Account custAccount = AccountManager.getInstance().getAccountsByVatNumber(custId);
	        if (custAccount == null) {
	            System.err.println("WARNING: No account found for VAT " + custId + ", creating temporary account.");
	            Customer customer = UserManager.getInstance().findUserByVat(custId);
	            if (customer != null) {
	                custAccount = new PersonalAccount();
	                custAccount.setPrimaryOwner(customer);
	            } else {
	                System.err.println("ERROR: No customer found for VAT " + custId + ". Cannot set customerVat.");
	            }
	        }

	        this.issuer = issuerAccount;
	        this.customerVat = custAccount;
	        this.amount = new BigDecimal(parts[5].split(":", 2)[1].trim());
	        this.issueDate = LocalDate.parse(parts[6].split(":", 2)[1].trim());

	        if (parts.length > 7 && parts[7].startsWith("dueDate:")) {
	            this.dueDate = LocalDate.parse(parts[7].split(":", 2)[1].trim());
	        }

	        // Νέος κώδικας: αναζήτηση και ανάθεση paid
	        this.isPaid = false;  // default
	        for (String part : parts) {
	            if (part.startsWith("paid:")) {
	                this.isPaid = Boolean.parseBoolean(part.split(":", 2)[1].trim());
	                break;
	            }
	        }

	    } catch (Exception e) {
	        System.err.println("Error in Bill.unmarshal: " + data);
	        e.printStackTrace();
	    }
	}

}
