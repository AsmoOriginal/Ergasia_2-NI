package backend.model.transaction;

import backend.model.account.Account; 
import java.math.BigDecimal; 
import java.time.LocalDateTime;

public class Deposit extends Transaction{
	private String depositorName;   // Όνομα καταθέτη (αν υπάρχει, π.χ. εξωτερικός)
}
