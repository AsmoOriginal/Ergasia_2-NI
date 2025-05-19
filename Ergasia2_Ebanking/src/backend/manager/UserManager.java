package backend.manager;

import java.io.*;
import java.util.*;

import backend.model.user.Admin;
import backend.model.user.Company;
import backend.model.user.Customer;
import backend.model.user.Individual;
import backend.model.user.User;


public class UserManager  {
	private static UserManager instance;   // Singleton instance
	private final List<User> users; // Λίστα που αποθηκεύει όλους τους χρήστες
	private Map<String, User> usersByUsername = new HashMap<>();
	private Map<String, Customer> usersByVat = new HashMap<>();

	private  List<Customer> customers; 
	

	// Ιδιωτικός constructor για το Singleton pattern
    private UserManager() {
    	this.customers = new ArrayList<>();
        this.users = new ArrayList<>();
    }

    // Μέθοδος για να πάρουμε το μοναδικό instance του UserManager
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    
    public void loadUsersFromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                User user = parseLine(line);
                if (user != null) {
                    users.add(user);
                    usersByUsername.put(user.getUserName().toLowerCase(), user);
                    if (user instanceof Customer customer) {
                        customers.add(customer);
                        usersByVat.put(customer.getVatNumber(), customer);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }
    
    public void saveUsersToFile(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (User user : users) {
                writer.write(user.marshal());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }
    
    // Επιστρέφει τη λίστα των χρηστών
    public List<User> getUsers() {
        return users;
    }
    public List<Customer> getCustomers() {
		return customers;
	}

    

    // Προσθέτει νέο χρήστη
    public void addUser(User user) {
        users.add(user);
    }

    // Επιστρέφει τον χρήστη με βάση το username (ή null αν δεν βρεθεί)
    public User getUserByUsername(String username) {
        if (username == null) return null;
        return usersByUsername.get(username.toLowerCase());
    }
    
    public Customer findUserByVat(String vatNumber) {
    	if (vatNumber == null) return null;
        vatNumber = vatNumber.trim(); // αφαίρεσε τυχόν κενά

        return usersByVat.get(vatNumber);
    }


    // Διαγράφει χρήστη με βάση το username
    public void deleteUserByUsername(String username) {
        users.removeIf(user -> user.getUserName().equals(username));
    }

    // Πραγματοποιεί ταυτοποίηση χρήστη (επιστρέφει τον χρήστη αν το username και password είναι σωστά)
    public User authenticate(String username, String password) {
        return users.stream()
                .filter(u -> u.getUserName().equals(username) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }


 // Δημιουργεί αντικείμενο User από γραμμή αρχείου
    private User parseLine(String data) {
        try {
            String[] parts = data.split(",");
            Map<String, String> fields = new HashMap<>();
            for (String part : parts) {
                String[] kv = part.split(":",2);
                if (kv.length == 2) {
                    fields.put(kv[0].trim().toLowerCase(), kv[1].trim());
                }
            }

            String type = fields.get("type");
            String fullName = fields.get("legalname");
            String username = fields.get("username");
            String password = fields.get("password");
            String vat = fields.get("vatnumber");

            if (type == null || fullName == null || username == null || password == null) {
            	
            	 
                return null;
        }
            switch (type.toLowerCase()) {
                case "individual":
                    return new Individual(fullName, username, password, vat);
                case "company":
                    return new Company(fullName, username, password, vat);
                case "admin":
                    return new Admin(fullName, username, password);
                default:
                    System.err.println("Unknown user type: " + type);
            }
        } catch (Exception e) {
            System.err.println("Error parsing line: " + data);
            e.printStackTrace();
        }
        return null;
    }

}