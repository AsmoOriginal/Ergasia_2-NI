package backend.ui;

import java.math.BigDecimal;
import java.util.Scanner;

import backend.manager.AccountManager;
import backend.manager.UserManager;
import backend.model.account.Account;
import backend.model.account.BusinessAccount;
import backend.model.account.PersonalAccount;
import backend.model.user.Company;
import backend.model.user.Customer;
import backend.model.user.Individual;
import backend.model.user.User;

public class SignInUI {

	private static Scanner scanner = new Scanner(System.in);
	
	//create the user
	public static User signInMenu(UserManager userManager, AccountManager accountManager,Scanner scanner) {
		
		System.out.println("==== Sign Up ====");
		
		String type = null;
		
		while(type == null) {
			System.out.println("Choose an account type:");
			System.out.println("(1)Individual");
			System.out.println("(2)Company");
			System.out.println("(3) Back to Main Menu");
			System.out.println("Choose between 1-3:");
			
			int input = readInt();
			
			if (input == 1) {
		        type = "Individual";
		    } else if (input == 2) {
		        type = "Company";
		    }else if(input == 3) {
		    	return null;
		    }else {
		        System.out.println("Invalid choice. Try again.");
         }
	  }
		//get the info from the user 
		
		//get the legalName 
		System.out.println("Enter  legal name: ");
		String legalName = scanner.nextLine().trim();
		
		//get the userName
		String userName = null;
		while(true) {
			System.out.println("Enter user name:");
			 userName = scanner.nextLine().trim();
			
			if(userManager.getUserByUsername(userName) != null) {
				System.out.println("the userName already exists. Try another one");
			}else {
				break;
			}
		}
		
		//get the password
		System.out.println("Enter  password: ");
		String password = scanner.nextLine().trim();
		
		//get vat number
		String vatNumber = null;
			//get the vat number
			while(true) {
				System.out.println("Enter vatNumber:");
				 vatNumber = scanner.nextLine().trim(); 
				
				if(userManager.findUserByVat(vatNumber) != null) {
					System.out.println("the vatNumber already exists. Try another one");
				}else {
					break;
				}
		}
		
		//create the user object and save it to file
		Customer newUser = null;
		if(type.equals("Individual")) {
			 newUser = new Individual(legalName, userName, password, vatNumber);
		}
		else if(type.equals("Company")) {
			newUser = new Company(legalName, userName, password, vatNumber);
		}
		
		
		//save the users to file
		if(newUser != null) {
			userManager.addUser(newUser);
			userManager.saveUsersToFile("data/users/users.csv");
			
			 // Επιλογή λογαριασμού
		    String accountType = null;
		    while(accountType == null) {
		        System.out.println("Choose account type:");
		        System.out.println("(1) Personal Account");
		        System.out.println("(2) Business Account");
		        System.out.print("Choose between 1-2: ");
		        
		        int accInput = readInt();
		        if(accInput == 1) accountType = "Personal";
		        else if(accInput == 2) accountType = "Business";
		        else System.out.println("Invalid choice. Try again.");
		    }
		    
		    Account newAccount = null;
		    
		    if(accountType.equals("Personal")) {
		        // Αν θες να προσθέσεις και secondaryHolder:
		        System.out.print("Do you want to add a secondary holder? (y/n): ");
		        String secHolderAnswer = scanner.nextLine().trim().toLowerCase();
		        
		        Customer secondaryHolder = null;
		        if(secHolderAnswer.equals("y")) {
		            System.out.print("Enter secondary holder username: ");
		            
		            secondaryHolder = userManager.findUserByVat(secHolderAnswer);
		            if(secondaryHolder == null) {
		                System.out.println("Secondary holder user not found. Continuing without secondary holder.");
		            }
		        }
		        
		        // Δημιουργία PersonalAccount
		        PersonalAccount  newPAccount = new PersonalAccount(newUser,  new BigDecimal("0.02"));
		     if(secondaryHolder != null) {
		            boolean added = newPAccount.add(secondaryHolder);
		            if (!added) {
		                System.out.println("Secondary holder was not added.");
		            }
		        }
		     newAccount = newPAccount;
		    } else if(accountType.equals("Business")) {
		        // Δημιουργία BusinessAccount
		        newAccount = new BusinessAccount(newUser, new BigDecimal("0.03"));
		    }
		    
		    if(newAccount != null) {
		        accountManager.addAccount(newAccount);
		        System.out.println("New account created with IBAN: " + newAccount.getIban());
		        // Αποθήκευση στο αρχείο
		        accountManager.saveAccountsToFile("data/accounts/accounts.csv");
		    
		    }
		}
		
	    
	    return newUser;
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
	
	
}