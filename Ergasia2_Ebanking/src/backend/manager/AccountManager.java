package backend.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import backend.model.account.Account;
import backend.model.account.BusinessAccount;
import backend.model.account.PersonalAccount;
import backend.model.order.StandingOrder;
import backend.model.user.Customer;


public class AccountManager {
	
	private static AccountManager instance;  // Singleton instance
    private final List<Account> accounts;  // Λίστα με όλους τους λογαριασμούς
    private Map<String, Account> accountsByVat = new HashMap<>();
    
    
    // Ιδιωτικός constructor για singleton
    private AccountManager() {
        this.accounts = new ArrayList<>();
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
    public void loadAccountsFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))){
        	String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
            
            
            String[] parts = line.split(",");
            if (parts.length == 0) continue;

            String[] typePart = parts[0].split(":", 2);
            if (typePart.length != 2) continue;

            String type = typePart[1].trim();
            Account account = null;

            if (type.equals("PersonalAccount")) {
                account = new PersonalAccount(); // uses the default constructor from the class PersonalAccount
            } else if (type.equals("BusinessAccount")) {
                account = new BusinessAccount(); // uses the default constructor from the class BusinessAccount
            }

            if (account != null) {
                account.unmarshal(line);
                accounts.add(account);
                String vat = account.getPrimaryOwner().getVatNumber();
                accountsByVat.put(vat, account);

            }
           }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    public void saveAccountsToFile(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for(Account account : accounts) {
                writer.write(account.marshal());
                writer.newLine();
            }
           
        } catch (IOException e) {
            System.err.println("Error saving accounts:  "  + e.getMessage());
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
    
    public void updateNextId() {
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
                } catch (NumberFormatException e) {}
            }
        }
        Account.setNextId(max); 
    }
    
  //get the accounts by vatNumber and use lists for the users with the same vatNumber that have more than one accounts
    public Account getAccountsByVatNumber(String vatNumber) {
        return accountsByVat.get(vatNumber);
    }
    
    public void chargeMonthlyFees(LocalDate date) {
        List<StandingOrder> orders = StandingOrderManager.getInstance().getAllOrders();

        for (StandingOrder order : orders) {
            BigDecimal fee = order.getFee();


            Account account = order.getChargeAccount();
            if (account != null && fee != null && fee.compareTo(BigDecimal.ZERO) > 0) {
                account.setBalance(account.getBalance()
                        .subtract(fee)
                        .setScale(2, RoundingMode.HALF_UP));

                if (account.getBalance().compareTo(fee) >= 0) {
                    System.out.println("Fee: " + fee + " for account: " + account.getIban());
                    account.setBalance(account.getBalance().subtract(fee));
                    System.out.println("Charged monthly fee of " + fee + " for account " + account.getIban() );
                } else {
                    System.out.println("Insufficient balance for monthly fee in account " + account.getIban() );
                }
            }
        }

        saveAccountsToFile("data/accounts/accounts.csv");
    }
    
    
}