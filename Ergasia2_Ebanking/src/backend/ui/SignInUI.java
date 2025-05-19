package backend.ui;

import java.util.Scanner;

import backend.manager.UserManager;
import backend.model.user.Company;
import backend.model.user.Individual;
import backend.model.user.User;

public class SignInUI {

	private static Scanner scanner = new Scanner(System.in);
	
	//create the user
	public static User signInMenu(UserManager userManager, Scanner scanner) {
		
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
		User newUser = null;
		if(type.equals("Individual")) {
			 newUser = new Individual(legalName, userName, password, vatNumber);
		}
		else if(type.equals("Company")) {
			newUser = new Company(legalName, userName, password, vatNumber);
		}
		
		
		//save the users to file
		if(newUser != null) {
			userManager.addUser(newUser);
	//		userManager.saveUsersToFile();
			System.out.println("New user created");
			return newUser;
		}
		else {
			return null;
		}
		
		
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