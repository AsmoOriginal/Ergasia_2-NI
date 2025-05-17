package backend.ui;

import backend.manager.UserManager;
import backend.model.user.Customer;
import backend.model.user.User;

import java.util.Scanner;

public class LoginUI {
	
 private static Scanner scanner;

		public static User login(UserManager userManager) {
	        scanner = new Scanner(System.in);
	        User user = null;

	        // === Έλεγχος Username ===
	        while (user == null) {
	            System.out.println("Username:");
	            String username = scanner.nextLine();

	            user = userManager.getUserByUsername(username);
	            if (user == null) {
	                System.out.println("Username not found.");
	                System.out.println("Do you want to retrieve your username with the VatNumber? (y/n)");
	                String choice = scanner.nextLine();
	                if (choice.equalsIgnoreCase("y")) {
	                    System.out.println("Type your VatNumber:");
	                    String vat = scanner.nextLine();
	                    user = userManager.findUserByVat(vat);
	                    if (user != null) {
	                        System.out.println("Your username is: " + user.getUserName());
	                    } else {
	                        System.out.println("User not found with this VatNumber.");
	                    }
	                    user = null; // για να επαναλάβουμε το loop
	                }
	            }
	        }

	        //Έλεγχος Password
	        boolean authenticated = false;
	        while (!authenticated) {
	            System.out.println("Password:");
	            String password = scanner.nextLine();

	            if (user.getPassword().equals(password)) {
	                System.out.println("Loggin Saccessful ");
	                return user;
	            } else {
	                System.out.println("Wrong Password.");
	                System.out.println("Do you want to change the password? (y/n)");
	                String choice = scanner.nextLine();
	                if (choice.equalsIgnoreCase("y")) {
	                    // Επαλήθευση ταυτότητας
	                    System.out.println("Enter your legal name:");
	                    String inputName = scanner.nextLine().trim();

	                    System.out.println("Enter your VAT number:");
	                    String inputVat = scanner.nextLine().trim();

	                    boolean identityConfirmed = true;

	                    if (!user.getLegalName().equalsIgnoreCase(inputName)) {
	                        System.out.println("ERROR Legal name does not match.");
	                        identityConfirmed = false;
	                    }

	                    // Αν είναι πελάτης, επαληθεύουμε το VAT
	                    if (user instanceof Customer customer) {
	                        if (!customer.getVatNumber().equals(inputVat)) {
	                            System.out.println("ERROR VAT number does not match.");
	                            identityConfirmed = false;
	                        }
	                    } else {
	                        System.out.println("ERROR Only customers can recover with VAT.");
	                        identityConfirmed = false;
	                    }

	                    if (identityConfirmed) {
	                        System.out.println("Identity confirmed. Enter new password:");
	                        String newPassword = scanner.nextLine();
	                        user.setPassword(newPassword);
	                        userManager.saveUsersToFile();
	                        System.out.println("New password has been set successfully.");
	                        return login(userManager); // ξεκινάει από την αρχή
	                    } else {
	                        System.out.println("Identity verification failed. Password change denied.");
	                    }
	                    return login(userManager);
	                }
	            }
	        }

	        return null; 
	    }
	}

	

