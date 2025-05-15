package backend.manager;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import backend.model.account.Account;
import backend.model.bill.Bill;
import backend.model.transaction.Payment;
import backend.storage.StorageManager;

public class BillManager {

    private static BillManager instance;
    private List<Bill> issuedBills;
    private List<Bill> paidBills;
    private final StorageManager storageManager;
    private final String billsDirectory = "bill";  // Ο φάκελος όπου αποθηκεύονται τα αρχεία CSV
    private Map<String, Bill> billMap;

    
    private BillManager() {
        this.issuedBills = new ArrayList<>();
        this.paidBills = new ArrayList<>();
        this.storageManager = StorageManager.getInstance();
        
        // Φόρτωση των λογαριασμών και των πληρωμών από τα αρχεία κατά την εκκίνηση του BillManager
        loadBillsFromDirectory();
    }
    
    

    public static BillManager getInstance() {
        if (instance == null) {
            instance = new BillManager();
        }
        return instance;
    }

    public Bill getBillByRfCode(String rfCode) {
        return billMap.get(rfCode);
    }
    
    // Μέθοδος για φόρτωση των Bills από όλα τα αρχεία στον φάκελο
    private void loadBillsFromDirectory() {
        File dir = new File(billsDirectory); // Δημιουργούμε το αντικείμενο του φακέλου
        if (dir.exists() && dir.isDirectory()) {
            // Λαμβάνουμε όλα τα αρχεία .csv από τον φάκελο
            File[] files = dir.listFiles((d, name) -> name.endsWith(".csv"));
            if (files != null) {
                for (File file : files) {
                    loadBillsFromFile(file.getName()); // Φορτώνουμε τα δεδομένα από κάθε αρχείο
                }
            }
        } else {
            System.err.println("The directory 'bill' does not exist.");
        }
    }

    // Μέθοδος για φόρτωση των Bills από ένα συγκεκριμένο αρχείο CSV
    private void loadBillsFromFile(String fileName) {
        try {
            List<String> lines = storageManager.load(billsDirectory + "/" + fileName); // Διαβάζουμε τις γραμμές από το αρχείο
            for (String line : lines) {
            	Bill bill = new Bill(null, null, null, BigDecimal.ZERO,null,null,null);
                bill.unmarshal(line); // Μετατρέπουμε το string σε αντικείμενο
                issuedBills.add(bill); // Προσθέτουμε το αντικείμενο στη λίστα των εκδομένων
            }
        } catch (IOException e) {
            System.err.println("Loading error from file " + fileName + ": " + e.getMessage());
        }
    }

    public void payBill(String rfCode, Account fromAccount, Account toAccount) {
        // Find the bill by rfCode
        Bill bill = null;
        for (Bill b : issuedBills) {
            if (b.getRfCode().equals(rfCode) && b.isActive()) {
                bill = b;
                break;
            }
        }

        if (bill == null) {
            System.out.println("Bill not found or is not active!");
            return;
        }

        if (bill.isPaid()) {
            System.out.println("Bill already paid.");
            return;
        }

        // Ensure there is enough balance in the account
        if (fromAccount.getBalance().compareTo(bill.getAmount()) < 0) {
            System.out.println("Insufficient funds in the account.");
            return;
        }

        // Create the payment and execute it
        Payment payment = new Payment(fromAccount, toAccount, bill.getAmount(), bill);
        boolean success = payment.execute();

        if (success) {
            // Mark bill as paid
            bill.markAsPaid();
            System.out.println("Bill paid successfully!");

            // Move bill from issued to paid
            issuedBills.remove(bill);
            paidBills.add(bill);

            // Add the payment transaction
            TransactionManager.getInstance().addTransaction(payment);

            // Save the updated bills list to the file
            saveBills();
        } else {
            System.out.println("Payment failed.");
        }
    }

    // Μέθοδος για αποθήκευση των Bills σε αρχεία ανά ημέρα (ή άλλο κριτήριο)
    private void saveBills() {
        try {
            // Ομαδοποιούμε τα Bills ανά ημερομηνία (ή άλλο κριτήριο) πριν τα αποθηκεύσουμε
            // Δημιουργούμε χάρτη για να αποθηκεύσουμε τα Bills ανά ημερομηνία
            Map<String, List<String>> billsByDate = new HashMap<>();

            // Κάθε Bill το μετατρέπουμε σε string και το τοποθετούμε στην αντίστοιχη ημερομηνία
            for (Bill bill : issuedBills) {
                String dateKey = bill.getIssueDate().toString();  // Εδώ χρησιμοποιούμε την ημερομηνία έκδοσης
                billsByDate.computeIfAbsent(dateKey, k -> new ArrayList<>()).add(bill.marshal());
            }

            // Αποθήκευση όλων των Bills στα αρχεία
            for (Map.Entry<String, List<String>> entry : billsByDate.entrySet()) {
                String fileName = billsDirectory + "/" + entry.getKey() + ".csv";  // Δημιουργία ονόματος αρχείου με βάση την ημερομηνία
                storageManager.save(entry.getValue(), fileName);
            }

            // Επαναλαμβάνουμε την ίδια διαδικασία για τα πληρωμένα Bills
            Map<String, List<String>> paidBillsByDate = new HashMap<>();
            for (Bill bill : paidBills) {
                String dateKey = bill.getIssueDate().toString();
                paidBillsByDate.computeIfAbsent(dateKey, k -> new ArrayList<>()).add(bill.marshal());
            }

            // Αποθήκευση των πληρωμένων Bills στα αρχεία
            for (Map.Entry<String, List<String>> entry : paidBillsByDate.entrySet()) {
                String fileName = billsDirectory + "/paid_" + entry.getKey() + ".csv";  // Δημιουργία ονόματος αρχείου με βάση την ημερομηνία
                storageManager.save(entry.getValue(), fileName);
            }

        } catch (IOException e) {
            System.err.println("Error in storage: " + e.getMessage());
        }
    }

    public List<Bill> getIssuedBills() {
        return issuedBills;
    }

    public List<Bill> getPaidBills() {
        return paidBills;
    }
    
    
}
