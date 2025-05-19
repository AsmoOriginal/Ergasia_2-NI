package backend.manager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
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
  
    private final TransactionManager transactionManager;
    private final AccountManager accountManager;
    

    //Singleton
    private StandingOrderManager() {
       
        
        this.transactionManager = TransactionManager.getInstance();
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
	private Transaction transaction;
  
	
	public void loadOrders(String filePath, StorageManager storageManager) {
	    List<StandingOrder> loadedOrders = storageManager.load(StandingOrder.class, filePath);

	    OrderGroup active = ordersByStatus.get("active");
	    OrderGroup expired = ordersByStatus.get("expired");
	    OrderGroup failed = ordersByStatus.get("failed");

	    // Καθαρίζεις πρώτα τις λίστες (αν θες)
	    active.payments.clear();
	    active.transfers.clear();
	    expired.payments.clear();
	    expired.transfers.clear();
	    failed.payments.clear();
	    failed.transfers.clear();

	    for (StandingOrder order : loadedOrders) {
	        // Ανάλογα με το status του order, βάλε το στη σωστή λίστα
	        String status = order.isActive(); // πχ "active", "expired", "failed"
	        OrderGroup group = ordersByStatus.get(status);

	        if (order instanceof PaymentOrder) {
	            group.payments.add((PaymentOrder) order);
	        } else if (order instanceof TransferOrder) {
	            group.transfers.add((TransferOrder) order);
	        }
	    }
	}

	
	public void saveOrders(String filePath) {
	    List<Storable> allOrders = new ArrayList<>();

	    OrderGroup active = ordersByStatus.get("active");
	    if (active != null) {
	        allOrders.addAll(active.payments);
	        allOrders.addAll(active.transfers);
	    }

	    OrderGroup expired = ordersByStatus.get("expired");
	    if (expired != null) {
	        allOrders.addAll(expired.payments);
	        allOrders.addAll(expired.transfers);
	    }

	    OrderGroup failed = ordersByStatus.get("failed");
	    if (failed != null) {
	        allOrders.addAll(failed.payments);
	        allOrders.addAll(failed.transfers);
	    }

	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
	        for (Storable order : allOrders) {
	            writer.write(order.marshal());
	            writer.newLine();
	        }
	    } catch (IOException e) {
	        System.err.println("[ERROR] Failed to save orders to file: " + filePath);
	        e.printStackTrace();
	    }
	}



     
    //Kάνε execute τα orders για μια συγκεκριμένη ημερομηνία "date"
     public void executeOrdersForDate(LocalDate date) {
    	 //the array list of  payment and transfer active orders
    	 OrderGroup activeOrders = ordersByStatus.get("active");
    	 
    	 //execute paymentOrders
    	 for(PaymentOrder paymentOrder : activeOrders.payments) {
    		 if(paymentOrder.shouldExecute(date)) {
    			 executeOrderAndRetry(paymentOrder, date);
    		 }
    		 else if(date.compareTo(paymentOrder.getEndDate()) == 1) {
    			 moveToExpired(paymentOrder);
    		 }
    	 }
    	 
    	 //execute  transferOrders
    	 for(TransferOrder transferOrder : activeOrders.transfers) {
    		 if(transferOrder.execute(date) != null) {
    			 executeOrderAndRetry(transferOrder, date);
    		 }
    		 else if(date.compareTo(transferOrder.getEndDate()) == 1) {
    			 moveToExpired(transferOrder);
    		 }
    	 }

     }
     
     public void executeOrderAndRetry(StandingOrder order, LocalDate executionDate) {
    	 int attempts = 0;
    	 boolean success = false;
    	 
    	 while(attempts < 3 && !success) {
    		 try {
    			 setTransaction(null);
    			 if (order instanceof PaymentOrder) {
    				 PaymentOrder paymentOrder = (PaymentOrder) order;

    	                setTransaction(new Payment(
    	                    paymentOrder.getChargeAccount(),
    	                    paymentOrder.getCreditAccount(),
    	                    paymentOrder.getMaxAmount(),
    	                    paymentOrder.getBill()
    	                    
    	                ));
    	            } else if (order instanceof TransferOrder) {
    	            	TransferOrder transferOrder = (TransferOrder) order;

    	                setTransaction(new Transfer(
    	                    transferOrder.getChargeAccount(),
    	                    transferOrder.getCreditAccount(),
    	                    transferOrder.getAmount(),
    	                    transferOrder.getSenderNote(),
    	                    transferOrder.getReceiverNote()
    	                ));
    	            }
    			
    	            success = true;
    	           saveOrders("ordes/active.csv"); // αποθηκεύει επιτυχημένες αλλαγές
    			
			} catch (Exception e) {
				attempts++;
				if (attempts >= 3) {
	                moveToFailed(order);
				}    
			}
    	 }
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
     
     public void simulateTimePassing(LocalDate endDate) {
    	    LocalDate currentDate = LocalDate.now();
    	    
    	    while (!currentDate.isAfter(endDate)) {
    	        System.out.println("Simulating date: " + currentDate);
    	        
    	        //Execute standing orders for current date
    	        executeOrdersForDate(currentDate);
    	        
    	        // Check if it's the last day of month for interest and fees
    	        if (isLastDayOfMonth(currentDate)) {
    	            
    	            accountManager.processDailyInterest(currentDate);
    	        }
    	        
    	        currentDate = currentDate.plusDays(1);
    	    }
    	    
    	   saveOrders("order/active.csv");
    	}

    	private boolean isLastDayOfMonth(LocalDate date) {
    	    return date.getDayOfMonth() == date.lengthOfMonth();
    	}

		public Transaction getTransaction() {
			return transaction;
		}

		public void setTransaction(Transaction transaction) {
			this.transaction = transaction;
		}

		public TransactionManager getTransactionManager() {
			return transactionManager;
		}

}