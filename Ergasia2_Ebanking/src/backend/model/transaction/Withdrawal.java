package backend.model.transaction;

import backend.model.account.Account;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

public class Withdrawal extends Transaction {

    private static final Logger logger = Logger.getLogger(Withdrawal.class.getName());
    private String withdrawalMethod;  // Μέθοδος ανάληψης (π.χ. ATM, ταμείο)

    public Withdrawal(Account fromAccount, BigDecimal amount, String withdrawalMethod) {
        super("Withdrawal",fromAccount, null, amount);  // Δεν χρειάζεται το toAccount για την ανάληψη
        this.withdrawalMethod = withdrawalMethod;
    }

    public String getWithdrawalMethod() {
        return withdrawalMethod;
    }

    public void setWithdrawalMethod(String withdrawalMethod) {
        this.withdrawalMethod = withdrawalMethod;
    }

    @Override
    public boolean execute() {
        Account account = getFromAccount();

        // Επικύρωση της συναλλαγής
        if (!isValidTransaction(account)) {
            return false;
        }

        // Εκτέλεση της ανάληψης
        try {
            account.setBalance(account.getBalance().subtract(getAmount()));  // Αφαίρεση από το υπόλοιπο του λογαριασμού
            logger.info("Withdrawal successful: " + getAmount() + " using method: " + withdrawalMethod + " on " + getFormattedDateTime());
            return true;
        } catch (Exception e) {
            // Καταγραφή σφάλματος με περισσότερες λεπτομέρειες
            logger.severe("Withdrawal failed: " + e.getClass().getName() + " - " + e.getMessage());
            return false;
        }
    }

    // Βοηθητική μέθοδος για την επικύρωση της συναλλαγής
    private boolean isValidTransaction(Account account) {
        // Έλεγχος για εγκυρότητα της συναλλαγής
        if (account == null || getAmount() == null || getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            logger.severe("Invalid withdrawal data: Account or amount is invalid. Amount:" +getAmount());
            return false;
        }

        // Έλεγχος αν το υπόλοιπο του λογαριασμού είναι επαρκές
        if (account.getBalance().compareTo(getAmount()) < 0) {
            logger.severe("Insufficient funds for withdrawal. Balance: " + account.getBalance() + ", Requested: " + getAmount());
            return false;
        }

        return true;
    }
    
    @Override
    public Map<String, String> marshal() {
        Map<String, String> data = new HashMap<>();
        data.put("id", getId());
        data.put("type", getType());
        data.put("fromIban", getFromAccount() != null ? getFromAccount().getIban() : "null");
        data.put("toIban", "null"); // Δεν υπάρχει toAccount σε Withdrawal
        data.put("amount", getAmount().toPlainString());
        data.put("dateTime", getDateTime().toString());
        data.put("transactor", getTransactor() != null ? getTransactor() : "null");
        data.put("withdrawalMethod", withdrawalMethod);
        return data;
    }

    @Override
    public Transaction unmarshal(Map<String, String> data) {
        // Δημιουργούμε το αντικείμενο Withdrawal
        Withdrawal withdrawal = new Withdrawal(
            backend.manager.AccountManager.getInstance().getAccountByIban(data.get("fromIban")),
            new BigDecimal(data.get("amount")),
            data.get("withdrawalMethod")
        );

        // Θέτουμε τα υπόλοιπα πεδία
        withdrawal.setId(data.get("id"));
        withdrawal.setDateTime(LocalDateTime.parse(data.get("dateTime")));
        withdrawal.setTransactor(!"null".equals(data.get("transactor")) ? data.get("transactor") : null);

        // Επιστρέφουμε το αντικείμενο Withdrawal
        return withdrawal;
    }


}
