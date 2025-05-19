package backend.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import backend.model.account.Account;
import backend.model.statement.AccountStatement;
import backend.model.transaction.Transaction;

public class StatementManager {

	 private static StatementManager instance;
	    private final Map<String, List<AccountStatement>> statementMap = new HashMap<>();
	    
	    

	   

	    public static StatementManager getInstance() {
	        if (instance == null) {
	            instance = new StatementManager();
	        }
	        return instance;
	    }

	    //Δημιουργία νέας κίνησης μετά από συναλλαγή
	    public void addTransactionToStatement(Transaction transaction) {
	        for (Account account : transaction.getInvolvedAccounts()) {
	            String iban = account.getIban();

	            // Φτιάχνει νέα κίνηση (ή ενημερώνει υπάρχουσα)
	            AccountStatement statement = new AccountStatement(
	                    account,
	                    transaction.getDateTime().toLocalDate(),
	                    transaction.getDateTime().toLocalDate(),
	                    new ArrayList<>(List.of(transaction))
	            );

	            statementMap.computeIfAbsent(iban, k -> new ArrayList<>()).add(0, statement); // αντίστροφη σειρά
	        }
	    }
	    
	    //Ανάκτηση statements για έναν λογαριασμό
	    public List<AccountStatement> getStatementsForAccount(String iban) {
	        return statementMap.getOrDefault(iban, new ArrayList<>());
	    }
	    
	    
}


