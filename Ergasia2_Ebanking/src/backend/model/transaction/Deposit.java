package backend.model.transaction;

import backend.model.account.Account;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Deposit extends Transaction {

    private static final Logger logger = Logger.getLogger(Deposit.class.getName());
    private String depositorName;  // Όνομα καταθέτη (π.χ. εξωτερικός πελάτης)

    
    public Deposit(Account fromAccount, Account toAccount, BigDecimal amount, String depositorName) {
        super("Deposit",fromAccount, toAccount, amount);
        if (depositorName == null || depositorName.isBlank()) {
            throw new IllegalArgumentException("Depositor name cannot be null or blank.");
        }
        this.depositorName = depositorName;
    }

    public String getDepositorName() {
        return depositorName;
    }

    public void setDepositorName(String depositorName) {
        if (depositorName == null || depositorName.isBlank()) {
            throw new IllegalArgumentException("Depositor name cannot be null or blank.");
        }
        this.depositorName = depositorName;
    }

    @Override
    public boolean execute() {
        Account account = getToAccount();

        if (!isValidTransaction(account)) {
            return false;
        }

        try {
            account.setBalance(account.getBalance().add(getAmount()));
            logger.info("Deposit successful: " + getAmount() +
                        " from: " + depositorName +
                        " to account IBAN: " + account.getIban() +
                        " on " + getFormattedDateTime());
            return true;
        } catch (Exception e) {
            logger.severe("Deposit failed: " + e.getClass().getName() + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Έλεγχος εγκυρότητας της συναλλαγής.
     *
     * "account" Ο λογαριασμός που θα λάβει την κατάθεση
     * "return" "true" αν η συναλλαγή είναι έγκυρη, αλλιώς "false"
     */
    private boolean isValidTransaction(Account account) {
        if (account == null || getAmount() == null || getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            logger.severe("Invalid deposit data. Account or amount is invalid. Amount: " + getAmount());
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
        data.put("toIban", getToAccount() != null ? getToAccount().getIban() : "null");
        data.put("amount", getAmount().toPlainString());
        data.put("dateTime", getDateTime().toString());
        data.put("transactor", getTransactor() != null ? getTransactor() : "null");
        data.put("depositorName", depositorName);
        return data;
    }

    @Override
    public Transaction unmarshal(Map<String, String> data) {
        // Δημιουργούμε το αντικείμενο Deposit (Transaction)
        Deposit deposit = new Deposit(
            backend.manager.AccountManager.getInstance().getAccountByIban(data.get("fromIban")),
            backend.manager.AccountManager.getInstance().getAccountByIban(data.get("toIban")),
            new BigDecimal(data.get("amount")),
            data.get("depositorName")
        );

        deposit.setId(data.get("id"));
        deposit.setDateTime(LocalDateTime.parse(data.get("dateTime")));

        // Επιστρέφουμε το αντικείμενο Deposit που υλοποιεί την Transaction
        return deposit;
    }

}
