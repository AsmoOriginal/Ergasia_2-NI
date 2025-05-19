package backend.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import backend.manager.BillManager;
import backend.model.bill.Bill;
import backend.model.user.Customer;
public class BillMenuUi {

	
	public static void show(Customer customer) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Bill Menu ===");
            System.out.println("1. Load Issued Bills");
            System.out.println("2. Show Paid Bills");
            System.out.println("3. Back");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    loadIssuedBills(customer);
                    break;
                case "2":
                    showPaidBills(customer);
                    break;
                case "3":
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }
	public static void loadIssuedBills(Customer customer) {
        BillManager billManager = BillManager.getInstance(); // υποθέτουμε singleton
        List<Bill> bills = billManager.loadBillsForCustomerFromFolder("data/bills",customer);

        if (bills.isEmpty()) {
            System.out.println("No bills found for customer " + customer.getVatNumber());
            return;
        }
        
        List<Bill> activeBills = bills.stream()
                .filter(b -> !b.isPaid() && b.getDueDate().isAfter(LocalDate.now()))
                .toList();

            if (activeBills.isEmpty()) {
                System.out.println("No active bills found.");
                return;
            }

        for (Bill bill : activeBills) {
            System.out.println("RF Code: " + bill.getRfCode() +
                               ", Bill ID: " + bill.getBillId() +
                               ", Amount: " + bill.getAmount() +
                               ", Issue Date: " + bill.getIssueDate() +
                               ", Due Date: " + bill.getDueDate());
        }
    }  
	
	 public static void showPaidBills(Customer customer) {
 	    BillManager billManager = BillManager.getInstance();
 	    List<Bill> bills = billManager.loadBillsForCustomerFromFolder("data/bills", customer);

 	    if (bills.isEmpty()) {
 	        System.out.println("No bills found for customer " + customer.getVatNumber());
 	        return;
 	    }

 	    // Φιλτράρουμε μόνο τα paid
 	    List<Bill> paidBills = bills.stream()
 	        .filter(Bill::isPaid)
 	        .toList(); // ή collect(Collectors.toList())

 	    if (paidBills.isEmpty()) {
 	        System.out.println("No paid bills found.");
 	        return;
 	    }

 	    for (Bill bill : paidBills) {
 	        System.out.println(" RF Code: " + bill.getRfCode() +
 	                           ", Bill ID: " + bill.getBillId() +
 	                           ", Amount: " + bill.getAmount() +
 	                           ", Issue Date: " + bill.getIssueDate() +
 	                           ", Due Date: " + bill.getDueDate());
 	    }
 	}


}
