package backend.model.bill;

import java.math.BigDecimal;
import java.time.LocalDate;

import backend.storage.Storable;

public class Bill implements Storable {
    private String billId;            // Μοναδικός αριθμός λογαριασμού
    private String rfCode;            // Κωδικός πληρωμής RF
    private String issuer;            // Εκδότης (εταιρεία)
    private BigDecimal amount;        // Ποσό
    private LocalDate issueDate;      // Ημερομηνία έκδοσης
    private LocalDate dueDate;        // Λήξη
    private boolean isPaid;           // Έχει πληρωθεί;
    private boolean isActive;         // Ενεργός RF;
    private String customerVat;       // ΑΦΜ πελάτη

   
    
    public Bill() {
		
	}



	public Bill(String rfCode, String billId,  String issuer, String customerVat, BigDecimal amount, LocalDate issueDate,LocalDate dueDate) {
        this.billId = billId;
        this.rfCode = rfCode;
        this.issuer = issuer;
        this.amount = amount;
        this.issueDate = issueDate;
        this.customerVat = customerVat;
        this.dueDate = dueDate;
        
    }

   

    public String getBillId() {
		return billId;
	}



	public void setBillId(String billId) {
		this.billId = billId;
	}



	public String getRfCode() {
		return rfCode;
	}



	public void setRfCode(String rfCode) {
		this.rfCode = rfCode;
	}



	public String getIssuer() {
		return issuer;
	}



	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}



	public BigDecimal getAmount() {
		return amount;
	}



	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}



	public LocalDate getIssueDate() {
		return issueDate;
	}



	public void setIssueDate(LocalDate issueDate) {
		this.issueDate = issueDate;
	}



	public LocalDate getDueDate() {
		return dueDate;
	}



	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}



	public boolean isPaid() {
		return isPaid;
	}



	public void setPaid(boolean isPaid) {
		this.isPaid = isPaid;
	}



	public boolean isActive() {
		return isActive = !isPaid && (dueDate.isAfter(LocalDate.now()) || dueDate.isEqual(LocalDate.now()));
	}



	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}



	public String getCustomerVat() {
		return customerVat;
	}



	public void setCustomerVat(String customerVat) {
		this.customerVat = customerVat;
	}



	

    // Marshal: μετατροπή σε string
    @Override
    public String marshal() {
        return String.format(
            "type:Bill,paymentCode:%s,billNumber:%s,issuer:%s,customer:%s,amount:%s,issueDate:%s,dueDate:%s",
            rfCode, billId, issuer, customerVat, amount.toPlainString(), issueDate, dueDate
        );
    }

    // Unmarshal: από string σε αντικείμενο
    @Override
    public void unmarshal(String data) {
    	try {
            String[] parts = data.split(",");

            this.rfCode = parts[1].split(":", 2)[1].trim();
            this.billId = parts[2].split(":", 2)[1].trim();
            this.issuer = parts[3].split(":", 2)[1].trim();
            this.customerVat = parts[4].split(":", 2)[1].trim();
            this.amount = new BigDecimal(parts[5].split(":", 2)[1].trim());
            this.issueDate = LocalDate.parse(parts[6].split(":", 2)[1].trim());

            if (parts.length > 7 && parts[7].startsWith("dueDate:")) {
                this.dueDate = LocalDate.parse(parts[7].split(":", 2)[1].trim());
            }
              
           
        } catch (Exception e) {
            System.err.println("Error in Bill.unmarshal: " + data);
            e.printStackTrace();
        }
    }
}
