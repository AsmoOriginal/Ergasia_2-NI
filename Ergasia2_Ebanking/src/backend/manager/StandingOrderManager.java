package backend.manager;

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
	
  
	
	public void loadOrders(String filePath, StorageManager storageManager) {
	    // Χρησιμοποιούμε dummy αντικείμενο απλά για να διαβάσουμε το αρχείο
	    PaymentOrder dummy = new PaymentOrder();  // ή TransferOrder, δεν έχει σημασία
	    storageManager.load(dummy, filePath);

	    String rawData = dummy.getRawData(); // <-- παίρνουμε όλο το αρχείο

	    List<StandingOrder> loadedOrders = new ArrayList<>();

	    if (rawData != null && !rawData.isEmpty()) {
	        String[] lines = rawData.split("\n");

	        for (String line : lines) {
	            StandingOrder order;

	            if (line.startsWith("PaymentOrder")) {
	                order = new PaymentOrder();
	            } else if (line.startsWith("TransferOrder")) {
	                order = new TransferOrder();
	            } else {
	                continue; // skip ή throw
	            }

	            order.unmarshal(line);
	            loadedOrders.add(order);
	        }
	    }

	    // Καθαρίζεις τις λίστες
	    ordersByStatus.get("active").payments.clear();
	    ordersByStatus.get("active").transfers.clear();
	    ordersByStatus.get("expired").payments.clear();
	    ordersByStatus.get("expired").transfers.clear();
	    ordersByStatus.get("failed").payments.clear();
	    ordersByStatus.get("failed").transfers.clear();

	    // Προσθήκη στη σωστή λίστα
	    for (StandingOrder order : loadedOrders) {
	    	String status;
	    	if (order.isActive()) {
	    	    status = "active";
	    	}
	    	 else {
	    	    status = "failed";
	    	}
	        OrderGroup group = ordersByStatus.get(status);

	        if (order instanceof PaymentOrder) {
	            group.payments.add((PaymentOrder) order);
	        } else if (order instanceof TransferOrder) {
	            group.transfers.add((TransferOrder) order);
	        }
	    }
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
                // Δεν το χρειάζεσαι εδώ
            }
        };

        storageManager.save(wrapper, filePath, false); // overwrite το αρχείο
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

	public AccountManager getAccountManager() {
		return accountManager;
	}
     
	 public Transaction getTransaction() {
			return transaction;
		}

		public void setTransaction(Transaction transaction) {
			this.transaction = transaction;
		}
}