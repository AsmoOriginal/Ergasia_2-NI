package backend.model.transaction;

import backend.manager.AccountManager;
import backend.manager.BillManager;
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
    public String marshal() {
        return String.format(
            "type:Transaction,id:%s,fromIban:%s,toIban:%s,amount:%s,dateTime:%s,transactor:%s,rfCode:%s",
            getId(),
            getFromAccount() != null ? getFromAccount().getIban() : "null",
            getToAccount() != null ? getToAccount().getIban() : "null",
            getAmount().toPlainString(),
            getDateTime().toString(),
            getTransactor() != null ? getTransactor() : "null",
            bill != null ? bill.getRfCode() : "null"
        );
    }
    

    
    @Override
    public void unmarshal(String data) {
        Map<String, String> map = parseStringToMap(data);

        Account from = AccountManager.getInstance().getAccountByIban(map.get("fromIban"));
        Account to = AccountManager.getInstance().getAccountByIban(map.get("toIban"));
        Bill bill = BillManager.getInstance().getBillByRfCode(map.get("rfCode"));

        if (from == null || to == null) {
            System.err.println("ERROR Missing account(s) for IBANs: " + map.get("fromIban") + " or " + map.get("toIban"));
            return;
        }

        this.setId(map.get("id"));
        this.setDateTime(LocalDateTime.parse(map.get("dateTime")));
        this.setTransactor("null".equals(map.get("transactor")) ? null : map.get("transactor"));
        this.setAmount(new BigDecimal(map.get("amount")));
        this.setFromAccount(from);
        this.setToAccount(to);
        this.setBill(bill);
    }

	

	


}