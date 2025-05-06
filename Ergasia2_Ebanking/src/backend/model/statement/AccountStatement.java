package backend.model.statement;

import backend.model.account.Account; 
import backend.model.transaction.Transaction; 
import java.time.LocalDate; 
import java.util.List;

public class AccountStatement {
	private Account account;   // Ο λογαριασμός για τον οποίο αφορά η κατάσταση               
	private LocalDate fromDate;  // Αρχική ημερομηνία του διαστήματος             
	private LocalDate toDate;    // Τελική ημερομηνία του διαστήματος 
	private List<Transaction> transactions;  // Λίστα συναλλαγών για το διάστημα αυτό
}
