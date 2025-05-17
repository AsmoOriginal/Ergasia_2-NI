package backend.manager;

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
import backend.storage.StorageManager;

public class StandingOrderManager {
	
	private static StandingOrderManager instance;
    private final StorageManager storageManager;
    private final TransactionManager transactionManager;
    private final AccountManager accountManager;
    

    //Singleton
    private StandingOrderManager() {
       
        this.storageManager = StorageManager.getInstance();
        this.transactionManager = TransactionManager.getInstance();
        this.accountManager = AccountManager.getInstance();
        
        ordersByStatus.put("active", new OrderGroup());
        ordersByStatus.put("expired", new OrderGroup());
        ordersByStatus.put("failed", new OrderGroup());
        
        loadInitialData("orders/active.csv", ordersByStatus.get("active"));
        loadInitialData("orders/expired.csv", ordersByStatus.get("expired"));
        loadInitialData("orders/failed.csv", ordersByStatus.get("failed"));
        
    }

    public static StandingOrderManager getInstance() {
        if (instance == null) {
            instance = new StandingOrderManager();
        }
        return instance;
    }
    
    /*Load the initial data from all the files*/
    public void loadInitialData(String fileName, OrderGroup group) { 
    	try {
    		
    		List<String> lines = storageManager.load(fileName); 
            for (String line : lines) {
            	//check if we have empty lines and skip them
            	if(line.trim().isEmpty()) {
            		continue;
            	}
            	
                if (line.contains("PAYMENT")) /* line.contains() method is used to search for a sequence of characters within the list of strings */{
                	PaymentOrder paymentOrder = PaymentOrder.fromLine(line);
                	group.payments.add(paymentOrder);//add it to the array of payments
                } else if (line.contains("TRANSFER")) {
                	TransferOrder transferOrder = TransferOrder.fromLine(line);
                	group.transfers.add(transferOrder);//add it to the array of transfers
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading " + fileName + e.getMessage());
        }
    }
    
    private void saveOrders() {
        StorageManager storage = StorageManager.getInstance();
        /*Iterate to all the entries of the Map*/
        for (Map.Entry<String, OrderGroup> entry : ordersByStatus.entrySet()) {
            String status = entry.getKey(); 
            OrderGroup group = entry.getValue();

            List<String> lines = new ArrayList<>();

            for (PaymentOrder orderPay : group.payments) {
                lines.add(orderPay.marshal());
            }

            for (TransferOrder orderTransfer : group.transfers) {
                lines.add(orderTransfer.marshal());
            }

            try {
                storage.save(lines, "orders/" + status + ".csv");
            } catch (IOException e) {
                System.err.println("Error saving " + status + " orders : " + e.getMessage());
            }
        }
        
       
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
    	            saveOrders(); // αποθηκεύει επιτυχημένες αλλαγές
    			
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
    	    
    	    saveOrders();
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