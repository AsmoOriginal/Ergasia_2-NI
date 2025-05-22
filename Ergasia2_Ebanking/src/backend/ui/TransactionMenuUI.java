package backend.ui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import backend.manager.AccountManager;
import backend.manager.BillManager;
import backend.manager.StatementManager;
import backend.manager.TransactionManager;
import backend.model.account.Account;
import backend.model.bill.Bill;
import backend.model.transaction.Deposit;
import backend.model.transaction.Payment;
import backend.model.transaction.Transfer;
import backend.model.transaction.Withdrawal;
import backend.model.user.Customer;

public class TransactionMenuUI {
	
	private static Scanner scanner = new Scanner(System.in);
    private static TransactionManager transactionManager = TransactionManager.getInstance();
    private static AccountManager accountManager = AccountManager.getInstance();
    private static BillManager billManager = BillManager.getInstance();
    
    public static  void transactionShowMenu(Customer customerCustomeMenu, Account account) {
    	
    	boolean back = false;
    	
    	while(!back) {
    		//show the transaction options
    		transactionMenu();
    		//get an integer from the user in order to select a transaction method
    		int input = readInt();
    		
    		//select a transaction method
    		switch(input) {
    		
    		case 1:
    			handleDeposit(account);
    			break;
    		case 2:
    			handleWithdraw(account);
    			break;
    		case 3:
    			handleTransfer(account);
    			break;
    		case 4:
    			handlePayBill(customerCustomeMenu,account);//the customer is the customer from the previous menu. The one that logged in
    			break;
    		case 5:
    			back = true;
    			break;
    		default:
    			System.out.println("Invalid choice. Try again");
    		
    		}
    	}
    	

    }
    
    //handle the Deposit 
    private static void handleDeposit(Account toAccount) {
    	
    	System.out.println("\n ==== Deposit ====");
    	System.out.println("Enter Amount: ");
    	
    	//check if the input for the amount is valid
    	BigDecimal amount = readInputBigDecimal();
    	
    	//get the depositor name from the user
    	System.out.println("Enter Depositor Name:");
    	String depositorName = scanner.nextLine();
    	
    	Deposit deposit = new Deposit(toAccount, toAccount, amount, depositorName);
    	boolean success = transactionManager.executeDeposit(deposit);
    	
    	if (success) {
    	    System.out.println("Deposit successful!");
    	    System.out.printf("New balance: %.2f€%n", toAccount.getBalance());

    	    // Προσθήκη transaction στο statement και αποθήκευση
    	    StatementManager sm = StatementManager.getInstance();
    	    sm.addTransactionToStatement(deposit);
    	    sm.saveStatements(
    	        sm.getStatementsForAccount(toAccount.getIban()),
    	        "data/statements/" + toAccount.getIban() + ".csv"
    	    );
    	}
    }
    
    //handle the Withdraw
    private static void handleWithdraw(Account fromAccount) {
    	
    	System.out.println("\n ==== Withdrawal ====");
    	System.out.println("Enter the amount you want to withdraw: ");
    	
    	//input for the amount from the user
    	BigDecimal amount = readInputBigDecimal();
    	
    	System.out.println("Enter Withdrawal method(ex. ATM): ");
    	String withdrawalMethod = scanner.nextLine();
    	
    	Withdrawal withdrawal = new Withdrawal(fromAccount, amount, withdrawalMethod);
    	boolean success = transactionManager.executeWithdrawal(withdrawal);
    	
    	if (success) {
    	    System.out.println("Withdrawal successful!");
    	    System.out.printf("New balance: %.2f€%n", fromAccount.getBalance());

    	    StatementManager sm = StatementManager.getInstance();
    	    sm.addTransactionToStatement(withdrawal);
    	    sm.saveStatements(
    	        sm.getStatementsForAccount(fromAccount.getIban()),
    	        "data/statements/" + fromAccount.getIban() + ".csv"
    	    );
    	}
    }
    
    //handle the Transfer
    private static void handleTransfer(Account fromAccount) {
    	
    	System.out.println("\n ==== Transfer ====");
    	System.out.println("Enter recipient IBAN: ");
    	
    	//input for  the recipients iban from the user
    	String iban = scanner.nextLine();
    	
    	//get the account details using iban the user inserted
    	Account toAccount = accountManager.getAccountByIban(iban);
    	if(toAccount == null) {
    		System.out.println("Account not found");
    		return;
    	}
    	
    	//get the amount that  the user wants to send
    	System.out.println("Enter the amount you want to transfer: ");
    	BigDecimal amount = readInputBigDecimal();
    	
    	//get the senders note from the user
    	System.out.println("Enter a note: ");
    	String sendersNote = scanner.nextLine();
    	
    	//enter the receiver's note
    	System.out.println("Enter the receiver's note: ");
    	String receiversNote = scanner.nextLine();
    	
    	Transfer transfer = new Transfer(fromAccount, toAccount, amount, sendersNote, receiversNote);
    	boolean success = transactionManager.executeTransfer(transfer);
    	
    	if (success) {
    	    System.out.println("Transfer successful!");
    	    System.out.printf("New balance: %.2f€%n", fromAccount.getBalance());

    	    StatementManager sm = StatementManager.getInstance();
    	    sm.addTransactionToStatement(transfer);
    	    sm.saveStatements(
    	        sm.getStatementsForAccount(fromAccount.getIban()),
    	        "data/statements/" + fromAccount.getIban() + ".csv"
    	    );
    	}
    }
    
    //handle the  Pay Bill
    private static void handlePayBill(Customer customer, Account fromAccount) {

    	
    	//load all the customer's bills
    	List<Bill> allBills = billManager.loadBillsByCustomerVat("data/bills", customer); // customer from previous menu
    	//load all the unpaidBills
    	List<Bill> unpaidBills = new ArrayList<Bill>();
    	
    	//find the unpaid bills
    	for (Bill bill : allBills) {
            if (!bill.isPaid()) {
                unpaidBills.add(bill);
            }
        }
        //check if not unpaid bills found
        if (unpaidBills.isEmpty()) {
            System.out.println("No unpaid bills found.");
            return;
        }
        
        // show the list with the unpaid bills
        System.out.println("\n=== Unpaid Bills ===");
        int index = 1;
        for(Bill bill: unpaidBills) {
        	Account issuer = bill.getIssuer();
        	String displayInfo = issuer.getIban();
        	
        	System.out.printf("%d. %s | Amount: %.2f€ | Due: %s | RF: %s%n",
        			index++,
        			displayInfo,
                    bill.getAmount(),
                    bill.getDueDate(),
                    bill.getRfCode());
        }
        
       //get the  bill the customer wants to pay
        System.out.print("\nSelect bill to pay (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            
            if (choice > 0 && choice <= unpaidBills.size()) {
                Bill selectedBill = unpaidBills.get(choice - 1);
                Account issuer = selectedBill.getIssuer();
            	String displayInfo = issuer.getIban();
            //confirm the payment of bill
            System.out.printf("Confirm payment of %.2f€ to %s? (yes/no): ",
                    selectedBill.getAmount(),
                    displayInfo);
                String confirm = scanner.nextLine();
                
                if (!confirm.equalsIgnoreCase("yes")) {
                    System.out.println("Payment cancelled.");
                    return;
                }

                // create the payment 
             
                Account toAccount = accountManager.getAccountByIban(selectedBill.getIssuer().getIban());
                Payment payment = new Payment(
                    fromAccount,
                    toAccount,
                    selectedBill.getAmount(),
                    selectedBill
                );

                if (TransactionManager.getInstance().executePayment(payment)) {
                    System.out.println("Payment successful!");
                    System.out.printf("New balance: %.2f€%n", fromAccount.getBalance());

                    StatementManager sm = StatementManager.getInstance();
                    sm.addTransactionToStatement(payment);
                    sm.saveStatements(
                        sm.getStatementsForAccount(fromAccount.getIban()),
                        "data/statements/" + fromAccount.getIban() + ".csv"
                    );

                    billManager.markBillAsPaid(selectedBill , customer);
                } else {
                    System.out.println("Payment failed!");
                }
             }
        }catch (NumberFormatException e) {
        	System.out.println("Invalid input. Please enter a number.");
		}
    }
    
    private static void transactionMenu() {
    	System.out.println("\n==== Transaction ====");
    	System.out.println("(1) Deposit");
    	System.out.println("(2) Withdrawal");
    	System.out.println("(3) Transfer");
    	System.out.println("(4) Pay Bill");
    	System.out.println("(5) Back to Customer's menu");
    }
    
    private static int readInt() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
    
    private static BigDecimal readInputBigDecimal() {
    	while (true) {
            try {
            	 return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid amount. Enter a number : ");
            }
        }
    }
    

}