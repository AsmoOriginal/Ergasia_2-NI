package backend.model.transaction;

import backend.manager.AccountManager;
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
    public String marshal() {
        return String.join(",",
            "id:" + getId(),
            "type:" + getType(),
            "fromIban:" + (getFromAccount() != null ? getFromAccount().getIban() : "null"),
            "toIban:null", // Σταθερό null, αφού δεν υπάρχει toAccount
            "amount:" + getAmount().toPlainString(),
            "dateTime:" + getDateTime().toString(),
            "transactor:" + (getTransactor() != null ? getTransactor() : "null"),
            "withdrawalMethod:" + (withdrawalMethod != null ? withdrawalMethod : "null")
        );
    }

    @Override
    public void unmarshal(String data) {
        Map<String, String> map = parseStringToMap(data);

        Withdrawal withdrawal = new Withdrawal(
            AccountManager.getInstance().getAccountByIban(map.get("fromIban")),
            new BigDecimal(map.get("amount")),
            !"null".equals(map.get("withdrawalMethod")) ? map.get("withdrawalMethod") : null
        );

        withdrawal.setId(map.get("id"));
        withdrawal.setDateTime(LocalDateTime.parse(map.get("dateTime")));
        withdrawal.setTransactor(!"null".equals(map.get("transactor")) ? map.get("transactor") : null);

        // Αν γεμίζεις το ίδιο object:
        this.setId(withdrawal.getId());
        this.setFromAccount(withdrawal.getFromAccount());
        this.setAmount(withdrawal.getAmount());
        this.setDateTime(withdrawal.getDateTime());
        this.setTransactor(withdrawal.getTransactor());
        this.setWithdrawalMethod(withdrawal.getWithdrawalMethod());
    }

}
