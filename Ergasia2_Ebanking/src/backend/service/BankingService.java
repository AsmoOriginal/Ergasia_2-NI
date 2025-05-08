package backend.service;

import backend.model.account.Account;
import backend.model.transaction.Transaction;
import backend.model.user.User;
import backend.model.bill.Bill;
import backend.model.order.StandingOrder;

import java.util.List;

public class BankingService {
	private List<Account> accounts;
    private List<Transaction> transactions;
    private List<User> users;
    private List<Bill> bills;
    private List<StandingOrder> standingOrders;
    
    
	private List<Account> getAccounts() {
		return accounts;
	}
	private void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}
	private List<Transaction> getTransactions() {
		return transactions;
	}
	private void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}
	private List<User> getUsers() {
		return users;
	}
	private void setUsers(List<User> users) {
		this.users = users;
	}
	private List<Bill> getBills() {
		return bills;
	}
	private void setBills(List<Bill> bills) {
		this.bills = bills;
	}
	private List<StandingOrder> getStandingOrders() {
		return standingOrders;
	}
	private void setStandingOrders(List<StandingOrder> standingOrders) {
		this.standingOrders = standingOrders;
	}

    
}
