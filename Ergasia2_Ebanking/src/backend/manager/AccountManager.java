package backend.manager;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import backend.model.account.Account;
import backend.model.account.BusinessAccount;
import backend.model.account.PersonalAccount;
import backend.model.user.Customer;
import backend.storage.StorageManager;


public class AccountManager {
	
	private static AccountManager instance;  // Singleton instance
    private final List<Account> accounts;  // Λίστα με όλους τους λογαριασμούς
   

    // Ιδιωτικός constructor για singleton
    private AccountManager() {
        this.accounts = new ArrayList<>();
       
  //      loadAccountsFromFile();
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
 
 
  /*  
 // Μέθοδος για την αποθήκευση λογαριασμών στο αρχείο
    public void saveAccountsToFile() {
    	try {
            FileStorageManager.save(accounts, "accounts.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */
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
    
   
   /* 
    public boolean deleteAccountByIban(String iban) {
        Account account = getAccountByIban(iban);  //χρήση της παραπάνω μεθόδου

        if (account != null) {
            accounts.remove(account);             // Αφαίρεση από τη λίστα
            saveAccountsToFile();                 // Επανεγγραφή του αρχείου
            return true;
        }

        return false; // Αν δεν βρεθεί
    }
*/
    
 // Μέθοδος για την επιστροφή όλων των λογαριασμών
    public List<Account> getAllAccounts() {
        return accounts;
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
                String code = iban.substring(5);
                
                try {
                    long val = Long.parseLong(code);
                    if (val >= max) {
                        max = val + 1;
                    }
                } catch (NumberFormatException ignored) {
                    System.err.println("ERROR Could not parse IBAN number part: " + code);
                }
            }
        }
        Account.setNextId(max); 
    }
	
   
}

