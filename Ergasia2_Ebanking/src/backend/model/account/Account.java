package backend.model.account;
import java.math.BigDecimal;
import backend.model.user.Customer;

public abstract class Account {
	
	private String iban; // Μοναδικός 20ψήφιος κωδικός (παράγεται από το σύστημα) 
	private Customer primaryHolder;// Κύριος κάτοχος (Individual ή Company)              
	private BigDecimal balance;// Τρέχον υπόλοιπο     
	private BigDecimal interestRate;
	
	
	
	public Account(String iban, Customer primaryHolder, BigDecimal balance, BigDecimal interestRate) {
		this.iban = iban;
		this.primaryHolder = primaryHolder;
		this.balance = balance;
		this.interestRate = interestRate;
	}
	
	public String getIban() {
		return iban;
	}
	public void setIban(String iban) {
		this.iban = iban;
	}
	public Customer getPrimaryHolder() {
		return primaryHolder;
	}
	public void setPrimaryHolder(Customer primaryHolder) {
		this.primaryHolder = primaryHolder;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public BigDecimal getInterestRate() {
		return interestRate;
	}
	public void setInterestRate(BigDecimal interestRate) {
		this.interestRate = interestRate;
	}  
	
	
}
