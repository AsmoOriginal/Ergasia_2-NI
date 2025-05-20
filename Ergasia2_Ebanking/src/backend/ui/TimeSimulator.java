package backend.ui;


import java.time.LocalDate;

import backend.manager.AccountManager;
import backend.manager.StandingOrderManager;

public class TimeSimulator {
    private LocalDate currentDate;
    private AccountManager accountManager;
    private StandingOrderManager standingOrderManager;

    public TimeSimulator(LocalDate startDate, AccountManager accountManager, StandingOrderManager standingOrderManager) {
        this.currentDate = startDate;
        this.accountManager = accountManager;
        this.standingOrderManager = standingOrderManager;
    }

    public void simulateUntil(LocalDate targetDate) {
        while (!currentDate.isAfter(targetDate)) {
            System.out.println("Simulating day: " + currentDate);
            accountManager.processDailyInterest(currentDate);

            if (currentDate.getDayOfMonth() == 1) {
               accountManager.chargeMonthlyFees(currentDate);
            }

            standingOrderManager.executeOrdersForDate(currentDate);

            currentDate = currentDate.plusDays(1);
        }
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }
}
