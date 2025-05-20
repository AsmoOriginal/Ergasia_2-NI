package backend.manager;

import java.io.*;
import java.util.*;

import backend.model.account.Account;
import backend.model.statement.AccountStatement;
import backend.model.transaction.Transaction;

public class StatementManager {

	 private static StatementManager instance;
	    private final Map<String, List<AccountStatement>> statementMap = new HashMap<>();
	    private List<AccountStatement> statements;
	    

	   

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
	    
	    public void saveStatements(List<AccountStatement> statements, String filePath) {
	        File file = new File(filePath);
	        file.getParentFile().mkdirs(); // Δημιουργεί τον φάκελο αν δεν υπάρχει

	        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
	            for (AccountStatement statement : statements) {
	                writer.write(statement.marshal());
	                writer.newLine();
	            }
	        } catch (IOException e) {
	            System.err.println("Error saving AccountStatements to " + filePath);
	            e.printStackTrace();
	        }
	    }

	    public List<AccountStatement> loadAllAccountStatements(String filePath) {
	        List<AccountStatement> statements = new ArrayList<>();
	        File file = new File(filePath);

	        if (!file.exists()) {
	            System.err.println("File not found: " + filePath);
	            return statements;
	        }

	        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
	            String line;
	            while ((line = reader.readLine()) != null) {
	                AccountStatement statement = new AccountStatement();  // Default constructor
	                statement.unmarshal(line);
	                statements.add(statement);
	            }
	        } catch (IOException e) {
	            System.err.println("Error loading AccountStatements: " + e.getMessage());
	            e.printStackTrace();
	        }

	        return statements;
	    }
	    
	    public List<AccountStatement> findStatementsByIban(String iban) {
	        List<AccountStatement> result = new ArrayList<>();

	        if (iban == null || iban.isEmpty()) return result;

	        for (AccountStatement stmt : statements) {
	            Account account = stmt.getAccount();  

	            if (account != null && iban.equalsIgnoreCase(account.getIban())) {
	                result.add(stmt);
	            }
	        }

	        return result;
	    }


		public List<AccountStatement> getStatements() {
			return statements;
		}

		public void setStatements(List<AccountStatement> statements) {
			this.statements = statements;
		}
	    
	    
}


