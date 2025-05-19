package backend.ui;

import java.util.Scanner;
import java.math.BigDecimal;
import java.util.List;

import backend.manager.AccountManager;
import backend.model.transaction.Deposit;
import backend.model.transaction.Transaction;
import backend.model.transaction.Transfer;
import backend.model.transaction.Withdrawal;
import backend.model.account.Account;
import backend.manager.TransactionManager;
import backend.manager.BillManager;

public class TransactionMenuUI {
	
	private static Scanner scanner = new Scanner(System.in);
    private static TransactionManager transactionManager = TransactionManager.getInstance();
    private static AccountManager accountManager = AccountManager.getInstance();
    private static BillManager billManager = BillManager.getInstance();
    
    public static  void transactionShowMenu(Account account) {
    	
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
    			handlePayBill(account);
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
    	
    	Deposit deposit = new Deposit(null, toAccount, amount, depositorName);
    	boolean success = transactionManager.executeDeposit(deposit);
    	
    	if(success == true) {
    		System.out.println(success);
    	}
    	else {
    		System.out.println("Deposit failed ");
    	}
    }
    
    //handle the Withdraw
    private static void handleWithdraw(Account fromAccount) {
    	
    	System.out.println("\n ==== Withdrawal ====");
    	System.out.println("Enter the amount you want to insert: ");
    	
    	//input for the amount from the user
    	BigDecimal amount = readInputBigDecimal();
    	
    	System.out.println("Enter Withdrawal method(ex. ATM): ");
    	String withdrawalMethod = scanner.nextLine();
    	
    	Withdrawal withdrawal = new Withdrawal(fromAccount, amount, withdrawalMethod);
    	boolean success = transactionManager.executeWithdrawal(withdrawal);
    	
    	if(success == true) {
    		System.out.println(success);
    	}
    	else {
    		System.out.println("Withdrawal failed ");
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
    	
    }
    
    //handle the  Pay Bill
    private static void handlePayBill(Account fromAccount) {
    	
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