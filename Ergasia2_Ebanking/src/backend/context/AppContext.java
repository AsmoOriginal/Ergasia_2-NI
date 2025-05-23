package backend.context;

import backend.manager.AccountManager;
import backend.manager.BillManager;
import backend.manager.StandingOrderManager;
import backend.manager.StatementManager;
import backend.manager.TransactionManager;
import backend.manager.UserManager;


public class AppContext {
	//final so it can't change
    private final UserManager userManager;
    private final AccountManager accountManager;
    private final BillManager billManager;
    private final StatementManager statementManager;
    private final TransactionManager transactionManager;
    private final StandingOrderManager standingOrderManager;

    private static AppContext instance;//AppContext is actualy singleton

    private AppContext() {
        // Αρχικοποίηση singleton instances
        this.userManager = UserManager.getInstance();
        this.accountManager = AccountManager.getInstance();
        this.billManager = BillManager.getInstance();
        this.statementManager = StatementManager.getInstance();
        this.transactionManager = TransactionManager.getInstance();
        this.standingOrderManager = StandingOrderManager.getInstance();

        
        loadData();
    }

    //load the data from the files
    private void loadData() {
        userManager.loadUsersFromFile("data/users/users.csv");
        statementManager.loadAllAccountStatementsFromFolder("data/statements");
        accountManager.loadAccountsFromFile("data/accounts/accounts.csv");
        standingOrderManager.loadStandingOrdersFromFolder("data/orders");

        userManager.bindAccountsToCustomers(userManager.getCustomers(), accountManager.getAccounts());
    }

    public static AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }

    public BillManager getBillManager() {
        return billManager;
    }

    public StatementManager getStatementManager() {
        return statementManager;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public StandingOrderManager getStandingOrderManager() {
        return standingOrderManager;
    }
}