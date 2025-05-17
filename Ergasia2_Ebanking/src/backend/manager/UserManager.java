package backend.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import backend.model.user.Admin;
import backend.model.user.Company;
import backend.model.user.Customer;
import backend.model.user.Individual;
import backend.model.user.User;
import backend.storage.StorageManager;

public class UserManager {
	private static UserManager instance;   // Singleton instance
	private final List<User> users; // Λίστα που αποθηκεύει όλους τους χρήστες
	private Map<String, User> usersByUsername = new HashMap<>();
	private Map<String, Customer> usersByVat = new HashMap<>();

	private final List<Customer> customers; // Πάλι Λίστα που αποθηκεύει όλους τους χρήστες (θα την χρειαστουμε γισ το model.account)
	private final StorageManager storageManager; // Χρησιμοποιούμε τον StorageManager για να αποθηκεύσουμε/φορτώσουμε

	// Ιδιωτικός constructor για το Singleton pattern
    private UserManager() {
    	this.customers = new ArrayList<>();
        this.users = new ArrayList<>();
        this.storageManager = StorageManager.getInstance();
    }

    // Μέθοδος για να πάρουμε το μοναδικό instance του UserManager
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    // Επιστρέφει τη λίστα των χρηστών
    public List<User> getUsers() {
        return users;
    }
    public List<Customer> getCustomers() {
		return customers;
	}

    
	// Φορτώνει τους χρήστες από το αρχείο users.csv
    public void loadUsersFromFile() {
        try {
            
            
            List<String> lines = storageManager.load("users/users.csv");
       
            
            
            for (String line : lines) {
                User user = parseUser(line);
                if (user != null) {
                    users.add(user);
                    usersByUsername.put(user.getUserName(), user);
                    if (user instanceof Customer customer) {
                        usersByVat.put(customer.getVatNumber(), customer);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load users: " + e.getMessage());
        }
    }
    
    
    // Αποθηκεύει όλους τους χρήστες στο αρχείο users.csv
    public void saveUsersToFile() {
        try {
        	
        	

        	List<String> lines = users.stream()
                    .map(User::marshal)
                    .collect(Collectors.toList());
            
            File file = new File("data/users/users.csv");
            file.getParentFile().mkdirs();

            

            storageManager.save(lines, file.getPath());
            
        } catch (IOException e) {
            System.err.println("ERROR Failed to save users: " + e.getMessage());
        }
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
    private User parseUser(String data) {
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
