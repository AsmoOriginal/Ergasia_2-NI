package backend.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
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

        // Ομαδοποίηση των bills ανά ημερομηνία έκδοσης
        Map<LocalDate, List<Bill>> billsByDate = bills.stream()
            .collect(Collectors.groupingBy(Bill::getIssueDate));

        for (Map.Entry<LocalDate, List<Bill>> entry : billsByDate.entrySet()) {
            LocalDate issueDate = entry.getKey();
            List<Bill> billsForDate = entry.getValue();

            String fileName = issueDate.toString() + ".csv";  // π.χ. 2025-05-01.csv
            Path filePath = folder.resolve(fileName);

           ;

            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                for (Bill bill : billsForDate) {
                    String line = bill.marshal();
                    writer.write(line);
                    writer.newLine();
                }
                
            } catch (IOException e) {
                System.err.println(" Error saving bills" );
                e.printStackTrace();
            }
        }
    }

    public List<Bill> loadBillsForCustomerFromFolder(String rootFolderPath, Customer customer) {
        List<Bill> customerBills = new ArrayList<>();

        if (customer == null || customer.getVatNumber() == null) {
            System.err.println("ERROR: Customer or VAT number is null");
            return customerBills;
        }

        File rootFolder = new File(rootFolderPath);
        if (!rootFolder.exists() || !rootFolder.isDirectory()) {
            System.err.println("ERROR: Root folder not found or not a directory: " + rootFolderPath);
            return customerBills;
        }

        // Αναζήτηση σε όλους τους υποφακέλους (issued, payed, κ.λπ.)
        File[] subfolders = rootFolder.listFiles(File::isDirectory);
        if (subfolders == null || subfolders.length == 0) {
            System.err.println("ERROR: No subfolders found in: " + rootFolderPath);
            return customerBills;
        }

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
                            bill.getCustomerVat().getPrimaryOwner().getVatNumber().equalsIgnoreCase(customer.getVatNumber())) {
                            customerBills.add(bill);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("ERROR: Failed to read file: " + file.getAbsolutePath());
                    e.printStackTrace();
                } catch (Exception e) {
                    System.err.println("ERROR: Failed to parse line in file: " + file.getAbsolutePath());
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Total Bills (issued + payed) for " + customer.getVatNumber() + ": " + customerBills.size());
        return customerBills;
    }

    
    public List<Bill> loadBillsFromSingleFolder(String folderPath, Customer customer) {
        List<Bill> customerBills = new ArrayList<>();

        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("ERROR: Folder not found or not a directory: " + folderPath);
            return customerBills;
        }

        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
        if (files == null) return customerBills;

        for (File file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Bill bill = new Bill();
                    bill.unmarshal(line);
                    if (bill.getCustomerVat() != null &&
                        bill.getCustomerVat().getPrimaryOwner().getVatNumber().equalsIgnoreCase(customer.getVatNumber())) {
                        customerBills.add(bill);
                    }
                }
            } catch (Exception e) {
                System.err.println("ERROR reading file " + file.getName());
                e.printStackTrace();
            }
        }

        return customerBills;
    }

    
    
    
    public void markBillAsPaid(Bill billToPay, Customer customer) {
        billToPay.setPaid(true);

        // 1. Φόρτωσε όλα τα issued bills του πελάτη
        List<Bill> issuedBills = loadBillsFromSingleFolder("data/bills/issued", customer);

        // 2. Αφαίρεσε το bill που πληρώθηκε
        Iterator<Bill> iterator = issuedBills.iterator();
        while (iterator.hasNext()) {
            Bill bill = iterator.next();
            if (bill.getRfCode().equals(billToPay.getRfCode())) {
                iterator.remove();  // remove from issued
                break;
            }
        }

        // 3. Αποθήκευσε ξανά τα υπόλοιπα issued bills (χωρίς το πληρωμένο)
        saveBillsByDate(issuedBills, "data/bills/issued");

        // 4. Φόρτωσε τα paid bills και πρόσθεσε το νέο
        List<Bill> paidBills = loadBillsFromSingleFolder("data/bills/payed", customer);
        paidBills.add(billToPay);

        // 5. Αποθήκευσε όλα τα paid bills ξανά
        saveBillsByDate(paidBills, "data/bills/payed");
    }


    



}