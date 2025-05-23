package backend.manager;

import java.io.*;
import java.util.*;

import backend.model.account.Account;
import backend.model.statement.AccountStatement;
import backend.model.transaction.Transaction;

public class StatementManager {

	 private static StatementManager instance;
	    private final Map<String, List<AccountStatement>> statementMap = new HashMap<>();
	    private List<AccountStatement> statements= new ArrayList<>();

	   

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

	    public List<AccountStatement> loadAllAccountStatementsFromFolder(String folderPath) {
	        List<AccountStatement> statements = new ArrayList<>();

	        File folder = new File(folderPath);
	        if (!folder.exists() || !folder.isDirectory()) {
	            System.err.println("ERROR Folder not found or not a directory: " + folderPath);
	            return statements;
	        }

	        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
	        if (files == null || files.length == 0) {
	            System.err.println("ERROR No CSV files found in folder: " + folderPath);
	            return statements;
	        }

	        for (File file : files) {
	            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
	                String line;
	                while ((line = reader.readLine()) != null) {
	                    AccountStatement statement = new AccountStatement();
	                    statement.unmarshal(line);
	                    statements.add(statement);
	                }
	            } catch (IOException e) {
	                System.err.println("ERROR Failed to read file: " + file.getName());
	                e.printStackTrace();
	            } catch (Exception e) {
	                System.err.println("ERROR Failed to parse line in file: " + file.getName());
	                e.printStackTrace();
	            }
	        }
	        this.statements = statements;
	        
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