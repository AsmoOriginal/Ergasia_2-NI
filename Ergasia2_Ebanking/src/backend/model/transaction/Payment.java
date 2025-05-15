package backend.model.transaction;

import backend.model.account.Account;
import backend.model.bill.Bill;

import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class Payment extends Transaction {

    private static final Logger logger = Logger.getLogger(Payment.class.getName());
    private Bill bill; // Ο λογαριασμός που εξοφλείται με την πληρωμή

    public Payment(Account fromAccount, Account toAccount, BigDecimal amount, Bill bill) {
        super("Payment",fromAccount, toAccount, amount);
        this.bill = bill;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    @Override
    public boolean execute() {
        Account fromAccount = getFromAccount();
        Account toAccount = getToAccount();
        BigDecimal amount = getAmount();

        // Βασικοί έλεγχοι εγκυρότητας
        if (fromAccount == null || toAccount == null || bill == null || amount == null) {
            logger.severe("Invalid payment data: missing accounts or bill.");
            return false;
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.severe("Payment amount must be positive.");
            return false;
        }

        if (bill.isPaid()) {
            logger.warning("Bill already paid. RF Code: " + bill.getRfCode());
            return false;
        }

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            logger.warning("Insufficient funds. Balance: " + fromAccount.getBalance());
            return false;
        }

        try {
            fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
            toAccount.setBalance(toAccount.getBalance().add(amount));
            bill.setPaid(true); // Σημειώνεται ως εξοφλημένο
            logger.info("Bill payment executed successfully. RF Code: " + bill.getRfCode()
                        + " | Amount: " + amount
                        + " | Date: " + getFormattedDateTime());
            return true;
        } catch (Exception e) {
            logger.severe("Payment execution error: " + e.getMessage());
            return false;
        }
    }

    public String getFormattedDateTime() {
        return getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
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
        data.put("rfCode", bill != null ? bill.getRfCode() : "null");
        return data;
    }

    @Override
    public Transaction unmarshal(Map<String, String> data) {
        // Δημιουργία του αντικειμένου Payment (ή άλλης συναλλαγής αν είναι διαφορετική)
        Payment payment = new Payment(
            backend.manager.AccountManager.getInstance().getAccountByIban(data.get("fromIban")),
            backend.manager.AccountManager.getInstance().getAccountByIban(data.get("toIban")),
            new BigDecimal(data.get("amount")),
            backend.manager.BillManager.getInstance().getBillByRfCode(data.get("rfCode"))
        );

        payment.setId(data.get("id"));
        payment.setDateTime(LocalDateTime.parse(data.get("dateTime")));
        payment.setTransactor(!"null".equals(data.get("transactor")) ? data.get("transactor") : null);

        // Επιστρέφουμε το αντικείμενο Payment, το οποίο είναι υποκλάση του Transaction
        return payment;
    }

}
