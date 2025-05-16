package backend.manager;

import backend.model.user.*;
import backend.storage.StorageManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
	private static UserManager instance;   // Singleton instance
	private final List<User> users; // Λίστα που αποθηκεύει όλους τους χρήστες
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
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load users: " + e.getMessage());
        }
    }

    // Αποθηκεύει όλους τους χρήστες στο αρχείο users.csv
    public void saveUsersToFile() {
        try {
            storageManager.save(users, "users.csv");
        } catch (IOException e) {
            System.err.println("Failed to save users: " + e.getMessage());
        }
    }

    // Προσθέτει νέο χρήστη
    public void addUser(User user) {
        users.add(user);
    }

    // Επιστρέφει τον χρήστη με βάση το username (ή null αν δεν βρεθεί)
    public User getUserByUsername(String username) {
        for (User user : users) {
            if (user.getUserName().equals(username)) {
                return user;
            }
        }
        return null;
    }
    
    public Customer findUserByVat(String vatNumber) {
        for (Customer c : customers) {
            if (c.getVatNumber().equals(vatNumber)) {
                return c;
            }
        }
        return null; 
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
            String type = parts[0];
            String legalName = parts[1];
            String userName = parts[2];
            String password = parts[3];

            switch (type) {
                case "Individual":
                    return new Individual( legalName, userName, password, parts[4]);
                case "Company":
                    return new Company( legalName, userName, password, parts[4]);
                case "Admin":
                    return new Admin( legalName, userName, password);
                default:
                    System.err.println("Unknown user type: " + type);
            }
        } catch (Exception e) {
            System.err.println("Error parsing user line: " + data);
        }
        return null;
    }
}
