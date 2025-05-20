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

    public List<Bill> loadBillsForCustomerFromFolder(String folderPath, Customer customer) {
        List<Bill> customerBills = new ArrayList<>();

        if (customer == null || customer.getVatNumber() == null) {
            System.err.println("ERROR Customer or VAT number is null");
            return customerBills;
        }

        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("ERROR Folder not found or not a directory: " + folderPath);
            return customerBills;
        }

        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
        if (files == null || files.length == 0) {
            System.err.println("ERROR No CSV files found in folder: " + folderPath);
            return customerBills;
        }

        for (File file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Bill bill = new Bill();
                    bill.unmarshal(line);

                    if (bill.getCustomerVat().equalsIgnoreCase(customer.getVatNumber())) {
                        customerBills.add(bill);
                    }
                }
            } catch (IOException e) {
                System.err.println("ERROR Failed to read file: " + file.getName());
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("ERROR Failed to parse line in file: " + file.getName());
                e.printStackTrace();
            }
        }

        System.out.println("Issued Bills for " + customer.getVatNumber() + ": " + customerBills.size());
        return customerBills;
    }

    
    public List<Bill> loadBillsByIssuerFromFolder(String folderPath, String issuerVat) {
        List<Bill> issuedBills = new ArrayList<>();

        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("ERROR Folder not found or not a directory: " + folderPath);
            return issuedBills;
        }

        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
        if (files == null || files.length == 0) {
            System.err.println("ERROR No CSV files found in folder: " + folderPath);
            return issuedBills;
        }

        for (File file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Bill bill = new Bill();
                    bill.unmarshal(line);

                    if (bill.getIssuer().equals(issuerVat)) {
                        issuedBills.add(bill);
                    }
                }
            } catch (IOException e) {
                System.err.println("ERROR Failed to read file: " + file.getName());
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("ERROR Failed to parse line in file: " + file.getName());
                e.printStackTrace();
            }
        }

        System.out.println("Issued Bills for issuer VAT " + issuerVat + ": " + issuedBills.size());
        return issuedBills;
    }

    
    
    public void markBillAsPaid(Bill billToPay, Customer customer) {
        // set the bill to paid
    	billToPay.setPaid(true);

        // load all the bills from the csv
        List<Bill> allBills = loadBillsForCustomerFromFolder("data/bills", customer);

        // find the right bill and pay it
        for (Bill bill : allBills) {
            if (bill.getRfCode().equals(billToPay.getRfCode())) {
                bill.setPaid(true); // change the bill to paid
                break;
            }
        }

        // Save to the csv file
        saveBillsByDate(allBills , "data/bills");	
    }

    



}