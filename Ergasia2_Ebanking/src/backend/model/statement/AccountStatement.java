package backend.model.statement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import backend.manager.AccountManager;
import backend.manager.BillManager;
import backend.model.account.Account;
import backend.model.account.PersonalAccount;
import backend.model.bill.Bill;
import backend.model.transaction.Deposit;
import backend.model.transaction.Payment;
import backend.model.transaction.Transaction;
import backend.model.transaction.Transfer;
import backend.model.transaction.Withdrawal;

public class AccountStatement {
	private Account account;   // Ο λογαριασμός για τον οποίο αφορά η κατάσταση               
	private LocalDate fromDate;  // Αρχική ημερομηνία του διαστήματος             
	private LocalDate toDate;    // Τελική ημερομηνία του διαστήματος 
	private List<Transaction> transactions;  // Λίστα συναλλαγών για το διάστημα αυτό
	
	public AccountStatement(Account account, LocalDate fromDate, LocalDate toDate, List<Transaction> transactions) {
		this.account = account;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.transactions = transactions;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public LocalDate getFromDate() {
		return fromDate;
	}

	public void setFromDate(LocalDate fromDate) {
		this.fromDate = fromDate;
	}

	public LocalDate getToDate() {
		return toDate;
	}

	public void setToDate(LocalDate toDate) {
		this.toDate = toDate;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}
	
	// Μέθοδος για να προσθέσουμε μια συναλλαγή στο statement
    public void addTransaction(Transaction transaction) {
        // Ελέγχουμε αν η συναλλαγή εμπίπτει στο διάστημα
        if (!transaction.getDateTime().toLocalDate().isBefore(fromDate) &&
            !transaction.getDateTime().toLocalDate().isAfter(toDate)) {
            transactions.add(transaction);
        }
    }
    
    // Μέθοδος για να επιστρέψουμε το υπόλοιπο του λογαριασμού για το διάστημα
    public BigDecimal getBalanceAtEndOfPeriod() {
        BigDecimal balance = account.getBalance(); // Ξεκινάμε με το τρέχον υπόλοιπο
        for (Transaction transaction : transactions) {
            if (transaction.getFromAccount().equals(account)) {
                balance = balance.subtract(transaction.getAmount()); // Αφαίρεση για συναλλαγές από τον λογαριασμό
            }
            if (transaction.getToAccount().equals(account)) {
                balance = balance.add(transaction.getAmount()); // Πρόσθεση για συναλλαγές προς τον λογαριασμό
            }
        }
        return balance;
    }
	
 // Μέθοδος marshal για να μετατρέψουμε το AccountStatement σε συμβολοσειρά
    public String marshal() {
        StringBuilder sb = new StringBuilder();
        
        // Χρησιμοποιούμε DateTimeFormatter για να μετατρέψουμε τις ημερομηνίες σε string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // Προσθήκη δεδομένων στο string
        sb.append(account instanceof PersonalAccount ? "Personal" : "Business").append(",");
        sb.append(account.getIban()).append(","); 
        sb.append(fromDate.format(formatter)).append(",");
        sb.append(toDate.format(formatter)).append(",");
        
        
        for (Transaction transaction : transactions) {
            sb.append(transaction.marshal()).append(";"); // πρέπει κάθε transaction να έχει marshal μέθοδο για να λειτουργήσει
        }
        
        if (!transactions.isEmpty()) {
            sb.setLength(sb.length() - 1); // Αφαίρεση του τελευταίου ;
        }

        
        return sb.toString();
    }
    
 // Μέθοδος unmarshal για να αναδημιουργήσουμε το AccountStatement από δεδομένα
    public static AccountStatement unmarshal(String data) {
        if (data == null || data.trim().isEmpty()) return null;

        try {
            // Χωρίζουμε το data σε ζεύγη "key:value"
            String[] pairs = data.split(",");
            Map<String, String> map = new HashMap<>();

            for (String pair : pairs) {
                String[] keyValue = pair.split(":", 2);
                if (keyValue.length == 2) {
                    map.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }

            String iban = map.get("iban");
            LocalDate fromDate = LocalDate.parse(map.get("from"));
            LocalDate toDate = LocalDate.parse(map.get("to"));

            Account account = AccountManager.getInstance().getAccountByIban(iban);
            if (account == null) {
                throw new IllegalArgumentException("Account with IBAN " + iban + " not found.");
            }

            List<Transaction> transactions = new ArrayList<>();
            String transactionsRaw = map.get("transactions");
            if (transactionsRaw != null && !transactionsRaw.isEmpty()) {
                // Διαχωρισμός συναλλαγών αν χωρίζονται με ','
                String[] transactionStrings = transactionsRaw.split(",");
                for (String txStr : transactionStrings) {
                    // Δημιουργούμε το Map<String, String> από τα δεδομένα της συναλλαγής
                    Map<String, String> txMap = new HashMap<>();
                    txMap.put("transactionData", txStr); // Το transactionStr μπορεί να είναι όλο το string της συναλλαγής

                    // Εδώ θα πρέπει να ξέρουμε ποια υποκλάση να καλέσουμε για να αναγνωρίσουμε τη συναλλαγή.
                    // Αν το 'txStr' περιλαμβάνει κάποια πληροφορία που δείχνει τον τύπο της συναλλαγής (π.χ. "Withdrawl", "Payment", κ.λπ.),
                    // μπορούμε να το χρησιμοποιήσουμε για να δημιουργήσουμε το κατάλληλο αντικείμενο

                    String type = txMap.get("transactionData").split(":")[0]; // Αν υπάρχει πεδίο type στον string

                    Transaction tx = null;
                    if ("Deposit".equalsIgnoreCase(type)) {
                        String fromIban = map.get("fromIban");
                        String toIban = map.get("toIban");
                        BigDecimal amount = new BigDecimal(map.get("amount"));
                        String depositorName = map.get("depositorName");
                        tx = new Deposit(AccountManager.getInstance().getAccountByIban(fromIban),
                                         AccountManager.getInstance().getAccountByIban(toIban),
                                         amount, depositorName);
                    } else if ("Payment".equalsIgnoreCase(type)) {
                        String fromIban = map.get("fromIban");
                        String toIban = map.get("toIban");
                        BigDecimal amount = new BigDecimal(map.get("amount"));
                        String rfCode = map.get("rfCode");
                        Bill bill = BillManager.getInstance().getBillByRfCode(rfCode);
                        tx = new Payment(AccountManager.getInstance().getAccountByIban(fromIban),
                                         AccountManager.getInstance().getAccountByIban(toIban),
                                         amount, bill);
                    } else if ("Transfer".equalsIgnoreCase(type)) {
                        String fromIban = map.get("fromIban");
                        String toIban = map.get("toIban");
                        BigDecimal amount = new BigDecimal(map.get("amount"));
                        String senderNote = map.get("senderNote");
                        String receiverNote = map.get("receiverNote");
                        tx = new Transfer(AccountManager.getInstance().getAccountByIban(fromIban),
                                          AccountManager.getInstance().getAccountByIban(toIban),
                                          amount, senderNote, receiverNote);
                    } else if ("Withdrawal".equalsIgnoreCase(type)) {  // Προσοχή στο "Withdrawal" με το λάθος spelling!
                        String fromIban = map.get("fromIban");
                        BigDecimal amount = new BigDecimal(map.get("amount"));
                        String withdrawalMethod = map.get("withdrawalMethod");
                        tx = new Withdrawal(AccountManager.getInstance().getAccountByIban(fromIban), amount, withdrawalMethod);
                    }

                    if (tx != null) {
                        transactions.add(tx);
                    }
                }
            }

            return new AccountStatement(account, fromDate, toDate, transactions);

        } catch (Exception e) {
            System.err.println("Error while unmarshalling AccountStatement: " + e.getMessage());
            return null;
        }
    }


}

            
