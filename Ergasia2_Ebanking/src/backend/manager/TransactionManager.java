package backend.manager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import backend.model.account.Account;
import backend.model.account.BusinessAccount;
import backend.model.transaction.Deposit;
import backend.model.transaction.Payment;
import backend.model.transaction.Transaction;
import backend.model.transaction.Transfer;
import backend.model.transaction.Withdrawal;

public class TransactionManager {

    private static TransactionManager instance;
    private final StatementManager statementManager;
    private final List<Transaction> transactions; // Λίστα για αποθήκευση συναλλαγών
    private  List<Account> accounts;  // Λίστα με όλους τους λογαριασμούς

    // Singleton
    private TransactionManager() {
        this.statementManager = StatementManager.getInstance();
        BillManager.getInstance();
        this.transactions = new ArrayList<>(); // Αρχικοποίηση της λίστας συναλλαγών
    }

    public static TransactionManager getInstance() {
        if (instance == null) {
            instance = new TransactionManager();
        }
        return instance;
    }

    // Execute generic transaction
    private boolean executeTransaction(Transaction transaction) {
        boolean success = transaction.execute();

        if (success) {
            System.out.println(transaction.getClass().getSimpleName() + " executed successfully.");
            addTransaction(transaction); // μόνο logging
            return true;
        } else {
            System.out.println(transaction.getClass().getSimpleName() + " failed.");
            return false;
        }
    }


    // Execute Deposit and return true or false based on success
    public boolean executeDeposit(Deposit deposit) {
        return executeTransaction(deposit);
    }

    // Execute Withdrawal and return true or false based on success
    public boolean executeWithdrawal(Withdrawal withdrawal) {
        return executeTransaction(withdrawal);
    }

    // Execute Transfer and return true or false based on success
    public boolean executeTransfer(Transfer transfer) {
        return executeTransaction(transfer);
    }

    // Execute Payment and return true or false based on success
    public boolean executePayment(Payment payment) {
        return executeTransaction(payment);
    }

    
    // Μέθοδος για την προσθήκη συναλλαγής στη λίστα συναλλαγών
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        System.out.println("Transaction added: " + transaction.getId());
    }

    // Method to update the account balance after a transaction
    public void updateAccountBalance(Account account, BigDecimal amount) {
        account.setBalance(account.getBalance().add(amount)); // Update the account's balance
        System.out.println("Account balance updated: " + account.getBalance());
    }

    // Μέθοδος για την απόκτηση όλων των συναλλαγών
    public List<Transaction> getTransactions() {
        return transactions;
    }
    
    

	public StatementManager getStatementManager() {
		return statementManager;
	}
}
