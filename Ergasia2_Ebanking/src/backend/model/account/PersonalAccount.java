package backend.model.account;

import java.math.BigDecimal;
import java.util.List;

import backend.model.user.Customer;
import backend.model.user.Individual;

public class PersonalAccount extends Account{
	
	private List<Individual> secondaryHolders; // Δευτερεύοντες κάτοχοι (μόνο φυσικά πρόσωπα)

	public PersonalAccount(String iban, Customer primaryHolder, BigDecimal balance, BigDecimal interestRate, List<Individual> secondaryHolders) {
		super(iban, primaryHolder, balance, interestRate);
		this.secondaryHolders = secondaryHolders;
	}

	public List<Individual> getSecondaryHolders() {
		return secondaryHolders;
	}

	public void setSecondaryHolders(List<Individual> secondaryHolders) {
		this.secondaryHolders = secondaryHolders;
	}
	
	
	
	
	
}
