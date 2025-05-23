package backend.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import backend.model.order.PaymentOrder;
import backend.model.order.StandingOrder;
import backend.model.order.TransferOrder;
import backend.model.transaction.Payment;
import backend.model.transaction.Transaction;
import backend.model.transaction.Transfer;
import backend.storage.Storable;
import backend.storage.StorageManager;

public class StandingOrderManager {
	
	private static StandingOrderManager instance;
  
    private final AccountManager accountManager;
    private StorageManager storageManager;
    private Transaction transaction;

    
   

	//Singleton
    private StandingOrderManager() {
       
        
        TransactionManager.getInstance();
        this.accountManager = AccountManager.getInstance();
        
        ordersByStatus.put("active", new OrderGroup());
        ordersByStatus.put("expired", new OrderGroup());
        ordersByStatus.put("failed", new OrderGroup());
   
    }

    public static StandingOrderManager getInstance() {
        if (instance == null) {
            instance = new StandingOrderManager();
        }
        return instance;
    }
    
   
    
    
    /*Χρήση inner class ώστε να υπάρχει εύκολη δημιουργία αντικειμένων paymentOrder και transferOrder για τα active, failed και expired
     * χωρίς να εχουμε πολλές διαφορετικές μεθόδους ώστε να κάνουμε load και save */
     public static class OrderGroup {
    	
        List<PaymentOrder> payments = new ArrayList<>();
        List<TransferOrder> transfers = new ArrayList<>();
    }
     
     //φτιάχνουμε το map που θα αντιστοιχίσει strings με τα αντικείμενα της inner class και είναι final για να μη δώσω άλλη αναφορά
     public final Map<String, OrderGroup> ordersByStatus = new HashMap<>();

	private List<StandingOrder> orders;
	
  
	
     public List<StandingOrder> loadStandingOrdersFromFolder(String folderPath) {
    	    List<StandingOrder> standingOrders = new ArrayList<>();
    	    
    	    File folder = new File(folderPath);
    	    
    	    
    	    if (!folder.exists() || !folder.isDirectory()) {
    	        System.err.println("ERROR Folder not found or not a directory: " + folderPath);
    	        return standingOrders;
    	    }

    	    File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
    	    if (files == null || files.length == 0) {
    	        System.err.println("ERROR No CSV files found in folder: " + folderPath);
    	        return standingOrders;
    	    }

    	    for (File file : files) {
    	        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
    	            String line;
    	            while ((line = reader.readLine()) != null) {
    	                StandingOrder order;
    	                if (line.contains("PaymentOrder")) {
    	                    order = new PaymentOrder();
    	                } else if (line.contains("TransferOrder")) {
    	                    order = new TransferOrder();
    	                } else  {
    	                    continue; 
    	                }
    	                order.unmarshal(line);
    	                boolean isActiveFile = file.getAbsolutePath().endsWith("active.csv");
    	               
    	                if (isActiveFile) {
    	                    order.setActive(true);  
    	                }

    	                standingOrders.add(order);
    	                String status = order.isActive() ? "active" : "failed"; // ή expired αν έχεις
    	                OrderGroup group = ordersByStatus.get(status);
    	                
    	                if (order instanceof PaymentOrder) {
    	                	group = ordersByStatus.computeIfAbsent(status, k -> new OrderGroup());
    	                    group.payments.add((PaymentOrder) order);
    	                } else if (order instanceof TransferOrder) {
    	                	group = ordersByStatus.computeIfAbsent(status, k -> new OrderGroup());
    	                    group.transfers.add((TransferOrder) order);
    	                    
    	                }
    	            }
    	            
    	            
    	        } catch (IOException e) {
    	            System.err.println("ERROR Failed to read file: " + file.getName());
    	            e.printStackTrace();
    	        } catch (Exception e) {
    	            System.err.println("ERROR Failed to parse line in file: " + file.getName());
    	            e.printStackTrace();
    	        }
    	    }

    	    this.orders = standingOrders;
    	    
    	    
    	    return standingOrders;
    	}


	
	public void saveOrders(String filePath) {
        StringBuilder sb = new StringBuilder();

        // Περνάς όλα τα orders (payments & transfers) από όλα τα groups
        for (OrderGroup group : ordersByStatus.values()) {
            for (PaymentOrder po : group.payments) {
                sb.append(po.marshal()).append("\n");
            }
            for (TransferOrder to : group.transfers) {
                sb.append(to.marshal()).append("\n");
            }
        }

        // Φτιάχνουμε το προσωρινό Storable wrapper
        Storable wrapper = new Storable() {
            private final String data = sb.toString();

            @Override
            public String marshal() {
                return data;
            }

            @Override
            public void unmarshal(String data) {
                
            }
        };

        storageManager.save(wrapper, filePath, false); // overwrite το αρχείο
    }

     
   
	public void executeOrdersForDate(LocalDate date) {
	    OrderGroup activeOrders = ordersByStatus.get("active");
	    System.out.println("Transfer orders count: " + activeOrders.transfers.size());

	    // Payment Orders
	    for (PaymentOrder paymentOrder : activeOrders.payments) {
	        System.out.println("Checking PaymentOrder: " + paymentOrder);
	        if (paymentOrder.shouldExecute(date)) {
	            System.out.println("Executing PaymentOrder");
	            executeOrderAndRetry(paymentOrder, date);
	        } else if (date.isAfter(paymentOrder.getEndDate())) {
	            System.out.println("PaymentOrder expired, moving to expired");
	            moveToExpired(paymentOrder);
	        } else {
	            System.out.println("Not time to execute yet");
	        }
	    }
	    
	    List<TransferOrder> toRemove = new ArrayList<>();

	    Iterator<TransferOrder> iterator = activeOrders.transfers.iterator();
	    while (iterator.hasNext()) {
	        TransferOrder transferOrder = iterator.next();

	        System.out.println("Checking TransferOrder: " + transferOrder);
	        if (transferOrder.shouldExecute(date)) {
	            System.out.println("-> Executing TransferOrder");
	            boolean success = executeOrderAndRetry(transferOrder, date);
	            if (!success) {
	                System.out.println("[FAILED] Order " + transferOrder.getOrderId() + " failed after retries");
	                               
	                moveToFailed(transferOrder);
	            }
	        } else if (date.isAfter(transferOrder.getEndDate())) {
	            System.out.println("-> TransferOrder expired, moving to expired");
	                         
	            moveToExpired(transferOrder);
	        } else {
	            System.out.println("-> Not time to execute yet");
	        }
	    }

	    
	}


     
	public boolean executeOrderAndRetry(StandingOrder order, LocalDate executionDate) {
	    int attempts = 0;
	    boolean success = false;

	    while (attempts < 3 && !success) {
	        try {
	            List<Transaction> transactions = order.execute(executionDate);  
	            if (!transactions.isEmpty()) {
	                System.out.println("[EXECUTED] Order " + order.getOrderId() + " created " + transactions.size() + " transaction(s)");
	                this.setTransaction(transactions.get(0));  
	            } else {
	                System.out.println("[SKIPPED] Order " + order.getOrderId() + " returned no transactions");
	            }

	            saveOrders("orders/active.csv");
	            success = true;

	        } catch (Exception e) {
	            attempts++;
	            if (attempts >= 3) {
	                System.err.println("[FAILED] Order " + order.getOrderId() + " failed after 3 attempts");
	                moveToFailed(order);
	            }
	        }
	    }

	    return success;
	}


     
     //method that removes the active order and add it to the failed(use that method for more uses if needed and cleaner code)
     private void moveToFailed(StandingOrder order) {
    	    OrderGroup active = ordersByStatus.get("active");
    	    OrderGroup failed = ordersByStatus.get("failed");

    	    if (order instanceof TransferOrder) {
    	        active.transfers.remove(order);
    	        failed.transfers.add((TransferOrder) order);
    	    } else if (order instanceof PaymentOrder) {
    	        active.payments.remove(order);
    	        failed.payments.add((PaymentOrder) order);
    	    }
    	}
   //method that removes the active order and add it to the expired(use that method for more uses if needed and cleaner code)
     private void moveToExpired(StandingOrder order) {
    	 OrderGroup active = ordersByStatus.get("active");
    	 OrderGroup expired = ordersByStatus.get("expired");
    	 
    	 if (order instanceof TransferOrder) {
 	        active.transfers.remove(order);
 	        expired.transfers.add((TransferOrder) order);
 	    } else if (order instanceof PaymentOrder) {
 	        active.payments.remove(order);
 	        expired.payments.add((PaymentOrder) order);
 	    }
     }

	public AccountManager getAccountManager() {
		return accountManager;
	}
     
	 public Transaction getTransaction() {
			return transaction;
		}

		public void setTransaction(Transaction transaction) {
			this.transaction = transaction;
		}
		
		public List<StandingOrder> listStandingOrdersForCustomer(String vatNumber) {
		    List<StandingOrder> result = new ArrayList<>();

		    for (String status : ordersByStatus.keySet()) {
		        OrderGroup group = ordersByStatus.get(status);
		        
		        for (PaymentOrder paymentOrder : group.payments) {
		        	if (paymentOrder.getCustomer() != null && paymentOrder.getCustomer().getVatNumber().equals(vatNumber)) {
		        	    result.add(paymentOrder);
		        	
		            }
		        }

		        for (TransferOrder transferOrder : group.transfers) {
		            if (transferOrder.getCustomer() !=null && transferOrder.getCustomer().getVatNumber().equals(vatNumber)) {
		                result.add(transferOrder);
		            }
		        }
		    }
		    return result;
         }
		
		
		public List<StandingOrder> getAllOrders() {
		    return this.orders; 
		}
		
		

}