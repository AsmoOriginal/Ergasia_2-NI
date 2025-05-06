package backend.model.bill;

import backend.model.user.Company; 
import java.math.BigDecimal; 
import java.time.LocalDate; 

public class Bill {
	private String billId;              // Μοναδικός αριθμός λογαριασμού (ID λογαριασμού)
    private String rfCode;             // Κωδικός πληρωμής RF (ταυτοποιεί τη συνδρομή πελάτη-εταιρείας)
    private Company issuer;            // Εκδότης του λογαριασμού (Επιχείρηση)
    private BigDecimal amount;         // Ποσό πληρωμής
    private LocalDate issueDate;       // Ημερομηνία έκδοσης
    private LocalDate dueDate;         // Ημερομηνία λήξης
    private boolean isPaid;            // Κατάσταση πληρωμής
    private boolean isActive;          // Αν είναι ο ενεργός RF λογαριασμός

}
