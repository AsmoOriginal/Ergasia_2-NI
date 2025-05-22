package backend.model.statement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import backend.manager.AccountManager;
import backend.manager.BillManager;
import backend.model.account.Account;
import backend.model.account.PersonalAccount;
import backend.model.transaction.Deposit;
import backend.model.transaction.Payment;
import backend.model.transaction.Transaction;
import backend.model.transaction.Transfer;
import backend.model.transaction.Withdrawal;
import backend.storage.Storable;

public class AccountStatement implements Storable {
	private Account account;   // Ο λογαριασμός για τον οποίο αφορά η κατάσταση               
	private LocalDate fromDate;  // Αρχική ημερομηνία του διαστήματος             
	private LocalDate toDate;    // Τελική ημερομηνία του διαστήματος 
	private List<Transaction> transactions;  // Λίστα συναλλαγών για το διάστημα αυτό
	
	
	
	public AccountStatement() {
		
	}

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
    @Override
    public String marshal() {
        StringBuilder sb = new StringBuilder();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        sb.append("type:").append(account instanceof PersonalAccount ? "Personal" : "Business").append(",");
        sb.append("iban:").append(account.getIban()).append(",");
        sb.append("from:").append(fromDate.format(formatter)).append(",");
        sb.append("to:").append(toDate.format(formatter)).append(",");
        
        sb.append("transactions:");

        for (int i = 0; i < transactions.size(); i++) {
            sb.append("{").append(transactions.get(i).marshal()).append("}");
            if (i < transactions.size() - 1) {
                sb.append(";");  // separator for multiple transactions
            }
        }

        return sb.toString();
    }

    

        // Μέθοδος που ενημερώνει το τρέχον αντικείμενο με δεδομένα
        @Override
        public void unmarshal(String data) {
            AccountStatement newObj = fromString(data);
            if (newObj == null) {
                System.err.println("Failed to unmarshal AccountStatement.");
                return;
            }

            this.account = newObj.account;
            this.fromDate = newObj.fromDate;
            this.toDate = newObj.toDate;
            this.transactions = newObj.transactions;
        }
        
     // Static μέθοδος που φτιάχνει AccountStatement από String
        public static AccountStatement fromString(String data) {
            if (data == null || data.trim().isEmpty()) return null;

            try {
                String[] parts = data.split(",", 5); 
                // 5 μέρη: type, iban, fromDate, toDate, transactionsString

                if (parts.length < 5) {
                    System.err.println("Invalid data format for AccountStatement.");
                    return null;
                }

                //[0] = type
                String iban = parts[1];
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate fromDate = LocalDate.parse(parts[2], formatter);
                LocalDate toDate = LocalDate.parse(parts[3], formatter);

                Account account = AccountManager.getInstance().getAccountByIban(iban);
                if (account == null) {
                    System.err.println("Account with IBAN " + iban + " not found.");
                    return null;
                }

                List<Transaction> transactions = new ArrayList<>();
                String transactionsRaw = parts[4];
                if (!transactionsRaw.isEmpty()) {
                    // Κάθε transaction είναι χωρισμένο με ';' (όπως στο marshal)
                    String[] txStrings = transactionsRaw.split(";");

                    for (String txStr : txStrings) {
                        
                        Map<String, String> txData = parseStringToMap(txStr);

                        // Παίρνεις τον τύπο
                        String txType = txData.get("type");
                        Transaction tx = null;

                        if ("Payment".equalsIgnoreCase(txType)) {
                            tx = new Payment(
                                AccountManager.getInstance().getAccountByIban(txData.get("fromIban")),
                                AccountManager.getInstance().getAccountByIban(txData.get("toIban")),
                                new BigDecimal(txData.get("amount")),
                                BillManager.getInstance().getBillByRfCode(txData.get("rfCode"))
                            );
                        } else if ("Deposit".equalsIgnoreCase(txType)) {
                            tx = new Deposit(
                                AccountManager.getInstance().getAccountByIban(txData.get("fromIban")),
                                AccountManager.getInstance().getAccountByIban(txData.get("toIban")),
                                new BigDecimal(txData.get("amount")),
                                txData.get("depositorName")
                            );
                        } else if ("Transfer".equalsIgnoreCase(txType)) {
                            tx = new Transfer(
                                AccountManager.getInstance().getAccountByIban(txData.get("fromIban")),
                                AccountManager.getInstance().getAccountByIban(txData.get("toIban")),
                                new BigDecimal(txData.get("amount")),
                                "null".equals(txData.get("senderNote")) ? null : txData.get("senderNote"),
                                "null".equals(txData.get("receiverNote")) ? null : txData.get("receiverNote")
                            );
                        } else if ("Withdrawal".equalsIgnoreCase(txType)) {
                            tx = new Withdrawal(
                                AccountManager.getInstance().getAccountByIban(txData.get("fromIban")),
                                new BigDecimal(txData.get("amount")),
                                txData.get("withdrawalMethod")
                            );
                        }

                        if (tx != null) {
                            tx.setId(txData.get("id"));
                            tx.setDateTime(LocalDateTime.parse(txData.get("dateTime")));
                            String transactor = txData.get("transactor");
                            tx.setTransactor("null".equals(transactor) ? null : transactor);

                            transactions.add(tx);
                        }
                    }
                }

                return new AccountStatement(account, fromDate, toDate, transactions);

            } catch (Exception e) {
                System.err.println("Error unmarshalling AccountStatement: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }

    
    private static Map<String, String> parseStringToMap(String data) {
        Map<String, String> map = new HashMap<>();
        String[] pairs = data.split(",");
        for (String pair : pairs) {
            String[] kv = pair.split(":", 2);
            if (kv.length == 2) {
                map.put(kv[0].trim(), kv[1].trim());
            }
        }
        return map;
    }


}

            
