package backend.model.transaction;

import backend.manager.AccountManager;
import backend.model.account.Account;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
            logger.info("Deposit accepted (pending balance update): " + getAmount() +
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
    public String marshal() {
        return String.format(
            "id:%s,type:%s,fromIban:%s,toIban:%s,amount:%s,dateTime:%s,transactor:%s,depositorName:%s",
            getId(),
            getType(),
            getFromAccount() != null ? getFromAccount().getIban() : "null",
            getToAccount() != null ? getToAccount().getIban() : "null",
            getAmount().toPlainString(),
            getDateTime().toString(),
            getTransactor() != null ? getTransactor() : "null",
            depositorName != null ? depositorName : "null"
        );
    }


    @Override
    public void unmarshal(String data) {
        Map<String, String> map = parseStringToMap(data);

        Deposit deposit = new Deposit(
            AccountManager.getInstance().getAccountByIban(map.get("fromIban")),
            AccountManager.getInstance().getAccountByIban(map.get("toIban")),
            new BigDecimal(map.get("amount")),
            map.get("depositorName")
        );

        deposit.setId(map.get("id"));
        deposit.setDateTime(LocalDateTime.parse(map.get("dateTime")));

        String transactor = map.get("transactor");
        deposit.setTransactor("null".equals(transactor) ? null : transactor);

        // Αν χρειάζεται να αποθηκευτεί στο τρέχον αντικείμενο (αν δεν επιστρέφεις το deposit):
        this.setId(deposit.getId());
        this.setFromAccount(deposit.getFromAccount());
        this.setToAccount(deposit.getToAccount());
        this.setAmount(deposit.getAmount());
        this.setDateTime(deposit.getDateTime());
        this.setTransactor(deposit.getTransactor());
        this.setDepositorName(deposit.getDepositorName());
    }

}