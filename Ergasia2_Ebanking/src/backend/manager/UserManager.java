package backend.manager;

import java.io.*;
import java.util.*;

import backend.model.account.Account;
import backend.model.user.*;


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

        // Αν το αρχείο δεν υπάρχει, δεν κάνουμε τίποτα
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            // Διαβάζουμε κάθε γραμμή του αρχείου
            while ((line = reader.readLine()) != null) {
                try {
                    // split each line from the csv file
                    String[] parts = line.split(",");
                    String type = null;

                    for (String part : parts) {
                        String[] keyValue = part.split(":", 2); // split the first part before :
                        if (keyValue.length == 2 && keyValue[0].trim().equalsIgnoreCase("type")) {
                            type = keyValue[1].trim().toLowerCase(); //get the second value that is after :
                            break;
                        }
                    }

                    if (type == null) {
                        System.err.println("Missing user type in line: " + line);
                        continue;
                    }

                    // create the correct type of object
                    User user = null;

                    switch (type) {
                        case "individual":
                            user = new Individual(); //uses the default constructor from the class Individual
                            break;
                        case "company":
                            user = new Company();  //uses the default constructor from the class Company
                            break;
                        case "admin":
                            user = new Admin();  //uses the default constructor from the class Admin
                            break;
                        default:
                            System.err.println("Unknown user type: " + type);
                            break;
                    }
                 
                    if (user == null) continue;
                    // summon the unmarshal in order to fill and create objects
                    user.unmarshal(line);
                    // add the user to the collection
                    users.add(user);
                    usersByUsername.put(user.getUserName().toLowerCase(), user);

                    if (user instanceof Customer customer) {
                        customers.add(customer);
                        usersByVat.put(customer.getVatNumber(), customer);
                    }

                } catch (Exception e) {
                    System.err.println("Error parsing user line: " + line);
                    e.printStackTrace();
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

    public Customer findCustomerByUsernameOrVat(String input, List<Customer> customers) {
        for (Customer customer : customers) {
            if (customer.getUserName().equalsIgnoreCase(input) ||
                customer.getVatNumber().equalsIgnoreCase(input)) {
                return customer;
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

    public Customer findUserByVat(String vatNumber) {
        for (Customer c : customers) {
            if (c.getVatNumber().equals(vatNumber)) {
                return c;
            }
        }
        return null;
    }
    
 // Επιστρέφει τον χρήστη με βάση το username (ή null αν δεν βρεθεί)
    public User getUserByUsername(String username) {
        if (username == null) return null;
        return usersByUsername.get(username.toLowerCase());
    }
    
 // Μέθοδος για να ελέγξει αν ένα ΑΦΜ ανήκει σε εταιρία
    public boolean isCompany(String vatNumber) {
        for (Customer customer : customers) {
            if (customer.getVatNumber().equals(vatNumber)) {
                return (customer instanceof Company);
            }
        }
        return false;
    }
    
    public void bindAccountsToCustomers(List<Customer> customers, List<Account> accounts) {
	    Map<String, List<Account>> accountsByVat = new HashMap<>();

	    // Ομαδοποιείς accounts βάσει ΑΦΜ
	    for (Account acc : accounts) {
	        accountsByVat.computeIfAbsent(acc.getPrimaryOwner().getVatNumber(), k -> new ArrayList<>()).add(acc);
	    }

	    // Συνδέεις κάθε πελάτη με τους λογαριασμούς του
	    for (Customer customer : customers) {
	        List<Account> customerAccounts = accountsByVat.getOrDefault(customer.getVatNumber(), new ArrayList<>());
	        customer.setAccounts(customerAccounts);
	    }
	}
}