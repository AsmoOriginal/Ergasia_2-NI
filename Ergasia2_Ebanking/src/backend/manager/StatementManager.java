package backend.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import backend.model.account.Account;
import backend.model.statement.AccountStatement;
import backend.model.transaction.Transaction;

public class StatementManager {

	 private static StatementManager instance;
	    private final Map<String, List<AccountStatement>> statementMap = new HashMap<>();
	    private final String filePath = "./data/statements.csv";
	    

	    private StatementManager() {
	        loadStatements();
	    }

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
	    
	    //Φόρτωση από CSV αρχείο
	    private void loadStatements() {
	        File file = new File(filePath);
	        if (!file.exists()) return;

	        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
	            String line;
	            while ((line = reader.readLine()) != null) {
	                AccountStatement statement = AccountStatement.unmarshal(line);
	                if (statement != null) {
	                    statementMap
	                        .computeIfAbsent(statement.getAccount().getIban(), k -> new ArrayList<>())
	                        .add(statement);
	                }
	            }

	            // Ταξινόμηση κάθε λίστας κατά φθίνουσα ημερομηνία
	            for (List<AccountStatement> list : statementMap.values()) {
	                list.sort(Comparator.comparing(AccountStatement::getFromDate).reversed());
	            }

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    
	   // Αποθήκευση όλων των statements
	    public void saveStatements() {
	        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
	            for (List<AccountStatement> list : statementMap.values()) {
	                for (AccountStatement statement : list) {
	                    writer.write(statement.marshal());
	                    writer.newLine();
	                }
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
}


