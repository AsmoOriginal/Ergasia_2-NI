package backend.ui;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import backend.manager.BillManager;
import backend.manager.UserManager;
import backend.model.bill.Bill;
import backend.model.user.Customer;

public class CompanyBillAdminMenu {

    private static BillManager billManager;
    private static UserManager userManager;

    public CompanyBillAdminMenu(BillManager billManager, UserManager userManager) {
        this.setBillManager(billManager);
        this.setUserManager(userManager);
    }

    public static void show() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== Company Bills Menu =====");
            System.out.println("1. Show Issued Bills");
            System.out.println("2. Show Paid Bills");
            System.out.println("3. Load Company Bills");
            System.out.println("0. Back");
            System.out.print("Select option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    showIssuedBills(scanner);
                    break;
                case "2":
                    showPaidBills(scanner);
                    break;
                case "3":
                    loadCompanyBills(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }


    private static void showIssuedBills(Scanner scanner) {
        System.out.print("Enter Company VAT (issuer VAT): ");
       

   
        List<Bill> issuedBills = getBillManager().loadAllBillsFromFolder("data/bills/issued");

        if (issuedBills.isEmpty()) {
            System.out.println("No issued bills found for this company.");
            return;
        }

        System.out.println("Issued Bills:");
        for (Bill bill : issuedBills) {
            System.out.printf("Bill ID: %s, RF Code: %s, Amount: %s, Issue Date: %s, Due Date: %s, Paid: %s%n",
                              bill.getBillId(), bill.getRfCode(), bill.getAmount().toPlainString(),
                              bill.getIssueDate(), bill.getDueDate(), bill.isPaid() ? "Yes" : "No");
        }
    }
    
    private static void showPaidBills(Scanner scanner) {
        System.out.print("Enter Company VAT (issuer VAT): ");
       


        List<Bill> bills = billManager.loadAllBillsFromFolder("data/bills/payed");
        List<Bill> paidBills = bills.stream()
                .filter(Bill::isPaid)
                .collect(Collectors.toList());

        if (paidBills.isEmpty()) {
            System.out.println("No paid bills found for this company.");
            return;
        }

        System.out.println("Paid Bills:");
        for (Bill bill : paidBills) {
            System.out.printf("Bill ID: %s, RF Code: %s, Amount: %s, Paid: Yes%n",
                    bill.getBillId(), bill.getRfCode(), bill.getAmount().toPlainString());
        }
    }

    private static void loadCompanyBills(Scanner scanner) {
        System.out.print("Enter Company VAT (issuer VAT): ");
        String issuerVat = scanner.nextLine().trim();

        // Βρες τον Customer με βάση το VAT
        Customer issuerCustomer = UserManager.getInstance().getCustomers().stream()
            .filter(c -> c.getVatNumber().equals(issuerVat))
            .findFirst()
            .orElse(null);

        if (issuerCustomer == null) {
            System.out.println("Company with VAT " + issuerVat + " not found.");
            return;
        }

        // Τώρα περνάμε το Customer (issuerCustomer) στη μέθοδο
        List<Bill> bills = BillManager.getInstance().loadBillsByIssuerVat("data/bills", issuerCustomer);

        if (bills.isEmpty()) {
            System.out.println("No bills found for this company.");
            return;
        }

        System.out.println("All Company Bills:");
        for (Bill bill : bills) {
            System.out.printf("Bill ID: %s, RF Code: %s, Amount: %s, Paid: %s%n",
                    bill.getBillId(),
                    bill.getRfCode(),
                    bill.getAmount().toPlainString(),
                    bill.isPaid() ? "Yes" : "No");
        }
    }


	public static UserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(UserManager userManager) {
		CompanyBillAdminMenu.userManager = userManager;
	}

	public static BillManager getBillManager() {
		return billManager;
	}

	public void setBillManager(BillManager billManager) {
		CompanyBillAdminMenu.billManager = billManager;
	}
}