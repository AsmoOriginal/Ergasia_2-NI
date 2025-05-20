package backend.model.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import backend.manager.AccountManager;
import backend.model.account.Account;
import backend.model.bill.Bill;
import backend.model.transaction.Payment;
import backend.model.transaction.Transaction;
import backend.model.user.Customer;



public class PaymentOrder extends StandingOrder {
    
    
    private String paymentCode;
    private BigDecimal maxAmount;
    private Bill bill;
    

    

	public PaymentOrder() {
		
	}

	public PaymentOrder( String orderId, String title, String description, Customer customer,
			LocalDate startDate, LocalDate endDate, BigDecimal fee, Account chargeAccount, Account creditAccount,
			boolean isActive, String paymentCode, BigDecimal maxAmount) {
		super("PaymentOrder", orderId, title, description, customer, startDate, endDate, fee, chargeAccount, creditAccount,
				isActive);
		this.paymentCode = paymentCode;
		this.maxAmount = maxAmount;
		
	}
	
	



	//Getters and Setters
    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }
    


    public Bill getBill() {
		return bill;
	}

	public void setBill(Bill bill) {
		this.bill = bill;
	}

	@Override
    public String marshal() {
        return marshal() + String.format("paymentCode:%s, maxAmount:",paymentCode, maxAmount.toPlainString());
    }
	@Override
	public void unmarshal(String line) {
	    // Κλήση της unmarshal της StandingOrder
	    super.unmarshal(line);

	    // Parse για να πιάσουμε τα δικά μας πεδία
	    Map<String, String> map = parseLine(line);

	    this.paymentCode = map.get("paymentCode");
	    this.maxAmount = new BigDecimal(map.get("maxAmount"));
	}

	
	@Override	
	public boolean shouldExecute(LocalDate paymentDate) {
		return isActive() && paymentDate.isEqual(getEndDate());
	}

	@Override
	public  List<Transaction> execute(LocalDate currentDate) {
		 // Ελέγχει αν η εντολή πρέπει να εκτελεστεί
	    if (shouldExecute(currentDate)) {
	    	// Ανακτά τον λογαριασμό με βάση το IBAN
	        Account creditAccountObj = AccountManager.getInstance().getAccountByIban(getCreditAccount().getIban());
	        
	        // Δημιουργία μιας νέας συναλλαγής για τη μεταφορά
	        Payment paymentTransaction = new Payment(
	        		getChargeAccount(),
	        		creditAccountObj,
	        		maxAmount,
	        		getBill()
	        );
	        
	        // Επιστρέφει μια λίστα με τις συναλλαγές που δημιουργήθηκαν
	        List<Transaction> transactions = new ArrayList<>();
	        transactions.add(paymentTransaction);
	        
	        
	        
	        return transactions;
	    } else {
	        return new ArrayList<>(); // Αν δεν πρέπει να εκτελεστεί, επιστρέφει κενή λίστα
	    }
	}
    
 
 
    
    
}