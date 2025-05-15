package backend.model.bill;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

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

    public Bill(String rfCode, String billId,  String issuer, BigDecimal amount, LocalDate issueDate,LocalDate dueDate,  String customerVat) {
        this.billId = billId;
        this.rfCode = rfCode;
        this.issuer = issuer;
        this.amount = amount;
        this.issueDate = issueDate;
        this.customerVat = customerVat;
        this.dueDate = null;
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
		return isActive;
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



	// Μαρκάρει ως πληρωμένο
    public void markAsPaid() {
        this.isPaid = true;
        this.isActive = false;
    }

    // Marshal: μετατροπή σε string
    @Override
    public String marshal() {
        return String.format(
            "type:Bill,paymentCode:%s,billNumber:%s,issuer:%s,customer:%s,amount:%s,issueDate:%s,dueDate:%s,paid:%s,active:%s",
            rfCode, billId, issuer, customerVat, amount.toPlainString(), issueDate, dueDate, isPaid, isActive
        );
    }

    // Unmarshal: από string σε αντικείμενο
    @Override
    public void unmarshal(String data) {
        String[] keyPairs = data.split(",");
        Map<String, String> map = new HashMap<>();
        for (String pair : keyPairs) {
            String[] entryPair = pair.split(":", 2);  // Εξασφάλιση split μόνο στο πρώτο ':'
            if (entryPair.length == 2) {
                map.put(entryPair[0].trim(), entryPair[1].trim());
            }
        }

        this.rfCode = map.get("paymentCode");
        this.billId = map.get("billNumber");
        this.issuer = map.get("issuer");
        this.customerVat = map.get("customer");
        this.amount = new BigDecimal(map.get("amount"));
        this.issueDate = LocalDate.parse(map.get("issueDate"));
        this.dueDate = LocalDate.parse(map.get("dueDate"));
        this.isPaid = Boolean.parseBoolean(map.getOrDefault("paid", "false"));
        this.isActive = Boolean.parseBoolean(map.getOrDefault("active", "true"));
    }
}
