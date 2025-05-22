package backend.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import backend.model.bill.Bill;
import backend.model.user.Customer;

public class BillManager {

    private static BillManager instance;
    
    private Map<String, Bill> billMap;

    public static BillManager getInstance() {
        if (instance == null) {
            instance = new BillManager();
        }
        return instance;
    }

    public Bill getBillByRfCode(String rfCode) {
        return billMap.get(rfCode);
    }
    
   

    public void saveBillsByDate(List<Bill> bills, String folderPath) {
        Path folder = Paths.get(folderPath);
        if (!Files.exists(folder)) {
            try {
                Files.createDirectories(folder);
            } catch (IOException e) {
                System.err.println("[ERROR] Failed to create directory: " + folderPath);
                e.printStackTrace();
                return;
            }
        }

        // Διαγραφή όλων των παλιών CSV αρχείων στον φάκελο
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*.csv")) {
            for (Path file : stream) {
                try {
                    Files.delete(file);
                } catch (IOException e) {
                    System.err.println("[ERROR] Failed to delete file: " + file.toString());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to list files in folder: " + folderPath);
            e.printStackTrace();
            return;
        }

        // Ομαδοποίηση των bills ανά ημερομηνία έκδοσης
        Map<LocalDate, List<Bill>> billsByDate = bills.stream()
            .collect(Collectors.groupingBy(Bill::getIssueDate));

        // Αποθήκευση των νέων αρχείων
        for (Map.Entry<LocalDate, List<Bill>> entry : billsByDate.entrySet()) {
            LocalDate issueDate = entry.getKey();
            List<Bill> billsForDate = entry.getValue();

            String fileName = issueDate.toString() + ".csv";  // π.χ. 2025-05-01.csv
            Path filePath = folder.resolve(fileName);

            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                for (Bill bill : billsForDate) {
                    String line = bill.marshal();
                    writer.write(line);
                    writer.newLine();
                }
            } catch (IOException e) {
                System.err.println("[ERROR] Error saving bills to file: " + fileName);
                e.printStackTrace();
            }
        }
    }

    public List<Bill> loadBillsByIssuerVat(String rootFolderPath, Customer issuer) {
        List<Bill> result = new ArrayList<>();
        // ίδιο με την μέθοδο που είχες, αλλά φιλτράρεις με issuer
        File rootFolder = new File(rootFolderPath);
        if (!rootFolder.exists() || !rootFolder.isDirectory()) return result;

        File[] subfolders = rootFolder.listFiles(File::isDirectory);
        if (subfolders == null) return result;

        for (File subfolder : subfolders) {
            File[] files = subfolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
            if (files == null) continue;

            for (File file : files) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        Bill bill = new Bill();
                        bill.unmarshal(line);
                        if (bill.getIssuer() != null &&
                            bill.getIssuer().getPrimaryOwner() != null &&
                            bill.getIssuer().getPrimaryOwner().getVatNumber().equalsIgnoreCase(issuer.getVatNumber())) {
                            result.add(bill);
                        }
                    }
                } catch (IOException | RuntimeException e) {
                    System.err.println("Failed reading file: " + file.getAbsolutePath());
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public List<Bill> loadBillsByCustomerVat(String rootFolderPath, Customer customer) {
        List<Bill> result = new ArrayList<>();
        // ίδιο με την μέθοδο που είχες, αλλά φιλτράρεις με customer
        File rootFolder = new File(rootFolderPath);
        if (!rootFolder.exists() || !rootFolder.isDirectory()) return result;

        File[] subfolders = rootFolder.listFiles(File::isDirectory);
        if (subfolders == null) return result;

        for (File subfolder : subfolders) {
            File[] files = subfolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
            if (files == null) continue;

            for (File file : files) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        Bill bill = new Bill();
                        bill.unmarshal(line);
                        if (bill.getCustomerVat() != null &&
                            bill.getCustomerVat().getPrimaryOwner() != null &&
                            bill.getCustomerVat().getPrimaryOwner().getVatNumber().equalsIgnoreCase(customer.getVatNumber())) {
                            result.add(bill);
                        }
                    }
                } catch (IOException | RuntimeException e) {
                    System.err.println("Failed reading file: " + file.getAbsolutePath());
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    

    
    public List<Bill> loadAllBillsFromFolder(String folderPath) {
        List<Bill> allBills = new ArrayList<>();

        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("ERROR Folder not found or not a directory: " + folderPath);
            return allBills;
        }

        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
        if (files == null || files.length == 0) {
            System.err.println("ERROR No CSV files found in folder: " + folderPath);
            return allBills;
        }

        for (File file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Bill bill = new Bill();
                    bill.unmarshal(line);
                    allBills.add(bill);
                }
            } catch (IOException | RuntimeException e) {
                System.err.println("ERROR Failed to read/parse file: " + file.getName());
                e.printStackTrace();
            }
        }
        return allBills;
    }

    
    
    
    public void markBillAsPaid(Bill billToPay, Customer customer) {
        billToPay.setPaid(true);

        // 1. Μετακίνησε μόνο το συγκεκριμένο bill από issued → payed

        // Μονοπάτι για issued
        List<Bill> issuedBills = loadAllBillsFromFolder("data/bills/issued");
        issuedBills.removeIf(b -> b.getRfCode().equals(billToPay.getRfCode()));
        saveBillsByDate(issuedBills, "data/bills/issued");

        // 2. Πρόσθεσε το πληρωμένο bill στα paid (χωρίς να σβήσεις τα υπάρχοντα!)
        List<Bill> paidBills = loadAllBillsFromFolder("data/bills/payed");

        // Μην προσθέσεις διπλό
        boolean alreadyPaid = paidBills.stream()
            .anyMatch(b -> b.getRfCode().equals(billToPay.getRfCode()));

        if (!alreadyPaid) {
            paidBills.add(billToPay);
            saveBillsByDate(paidBills, "data/bills/payed");
        }
    }


}