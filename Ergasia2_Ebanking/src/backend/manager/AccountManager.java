package backend.manager;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import backend.model.account.Account;
import backend.model.account.BusinessAccount;
import backend.model.account.PersonalAccount;
import backend.model.user.Customer;
import backend.storage.StorageManager;


public class AccountManager {
	
	private static AccountManager instance;  // Singleton instance
    private final List<Account> accounts;  // Λίστα με όλους τους λογαριασμούς
    private final StorageManager storageManager;  // Χρησιμοποιούμε τον StorageManager για να αποθηκεύσουμε/φορτώσουμε

    // Ιδιωτικός constructor για singleton
    private AccountManager() {
        this.accounts = new ArrayList<>();
        this.storageManager = StorageManager.getInstance();  // Παίρνουμε το StorageManager
        loadAccountsFromFile();
        updateNextId();
    }

    // Μέθοδος για να πάρουμε το μοναδικό instance του AccountManager
    public static AccountManager getInstance() {
        if (instance == null) {
            instance = new AccountManager();
        }
        return instance;
    }
    
    public List<Account> getAccounts() {
        return accounts;
    }
 
 // Μέθοδος για την ανάκτηση λογαριασμών από το αρχείο
    public void loadAccountsFromFile() {
        // Φορτώνουμε τους λογαριασμούς από το αρχείο (π.χ. data/accounts.csv)
        try {
            List<String> lines = storageManager.load("accounts.csv");
            for (String line : lines) {
                Account acc = parseAccount(line);
                if (acc != null) {
                    accounts.add(acc);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
 // Μέθοδος για την αποθήκευση λογαριασμών στο αρχείο
    public void saveAccountsToFile() {
    	try {
            storageManager.save(accounts, "accounts.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
 // Μέθοδος για να προσθέσουμε ένα νέο λογαριασμό
    public void addAccount(Account account) {
        if (getAccountByIban(account.getIban()) == null) {
            accounts.add(account);
        }
    }
        
 // Μέθοδος για να βρούμε ένα account από το iban
    public Account getAccountByIban(String iban) {
        for (Account account : accounts) {
            if (account.getIban().equals(iban)) {
                return account;
            }
        }
        return null;  // Αν δεν βρεθεί, επιστρέφουμε null
    }
    
    public boolean deleteAccountByIban(String iban) {
        Account account = getAccountByIban(iban);  //χρήση της παραπάνω μεθόδου

        if (account != null) {
            accounts.remove(account);             // Αφαίρεση από τη λίστα
            saveAccountsToFile();                 // Επανεγγραφή του αρχείου
            return true;
        }

        return false; // Αν δεν βρεθεί
    }

    
 // Μέθοδος για την επιστροφή όλων των λογαριασμών
    public List<Account> getAllAccounts() {
        return accounts;
    }
    
 // Μέθοδος για την δημιουργία ενός account από τη γραμμή δεδομένων
    
    private Account parseAccount(String line) {
        try {
            String[] parts = line.split(",");

            String type = parts[0].split(":", 2)[1].trim();
            
            String vat = parts[2].split(":", 2)[1].trim();
            Customer primaryOwner = (Customer) UserManager.getInstance().findUserByVat(vat);

            BigDecimal rate = new BigDecimal(parts[4].split(":", 2)[1].trim());
            BigDecimal balance = new BigDecimal(parts[5].split(":", 2)[1].trim());

            if (type.equals("PersonalAccount")) {
                List<String> coOwners = new ArrayList<>();
                for (int i = 6; i < parts.length; i++) {
                    String[] token = parts[i].split(":", 2);
                    if (token.length == 2 && token[0].trim().equals("coOwner")) {
                        coOwners.add(token[1].trim());
                    }
                }
                PersonalAccount acc = new PersonalAccount(primaryOwner, rate, coOwners);
                acc.setBalance(balance);  // Ανάθεση υπολοίπου
                return acc;

            } else if (type.equals("BusinessAccount")) {
                BusinessAccount acc = new BusinessAccount(primaryOwner, rate);  // Χρησιμοποιούμε τον constructor
                acc.setBalance(balance);  // Ανάθεση υπολοίπου
                return acc;
            }

        } catch (Exception e) {
            System.err.println("Error parsing account line: " + line);
            e.printStackTrace();
        }
        return null;
    }

    public void processDailyInterest(LocalDate today) {
        for (Account acc : accounts) {
            acc.accrueDailyInterest();
            if (isEndOfMonth(today)) {
                acc.applyMonthlyInterest();
            }
        }
    }

    
    private boolean isEndOfMonth(LocalDate date) {
        return date.equals(date.withDayOfMonth(date.lengthOfMonth()));
    }
    
    //Εναλλακτική με 1η ημέρα του μήνα
    /**
     private boolean isStartOfMonth(LocalDate date) {
    return date.getDayOfMonth() == 1;
   }
     */
    
    private void updateNextId() {
        long max = 100000000000000L;
        for (Account acc : accounts) {
            String iban = acc.getIban();
            if (iban.length() == 20) {
                String code = iban.substring(5); // 5 = "GR" + 3-digit type code
                try {
                    long val = Long.parseLong(code);
                    if (val >= max) {
                        max = val + 1;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        Account.setNextId(max); 
    }
	
   
}

