package frontend.guiComponents;

import backend.manager.AccountManager;
import backend.manager.StandingOrderManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TimeSimulatorGui {

    private LocalDate currentDate;
    private final AccountManager accountManager;
    private final StandingOrderManager standingOrderManager;

    public TimeSimulatorGui(LocalDate startDate, AccountManager accountManager, StandingOrderManager standingOrderManager) {
        this.currentDate = startDate;
        this.accountManager = accountManager;
        this.standingOrderManager = standingOrderManager;
    }

    public List<String> simulateUntil(LocalDate targetDate) {
        List<String> output = new ArrayList<>();

        if (targetDate.isBefore(currentDate)) {
            output.add("Target date is before current date. Simulation skipped.");
            return output;
        }

        while (!currentDate.isAfter(targetDate)) {
            output.add("----- Simulating day: " + currentDate + " -----");

            accountManager.processDailyInterest(currentDate);

            if (currentDate.getDayOfMonth() == 1) {
                accountManager.chargeMonthlyFees(currentDate);
            }

            standingOrderManager.executeOrdersForDate(currentDate);

            currentDate = currentDate.plusDays(1);
        }

        output.add("----- Simulation complete. Current date: " + currentDate.minusDays(1) + " -----");
        return output;
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }
}
