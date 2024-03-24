package nl.saxion.app;

import nl.saxion.app.observer.DashboardObserver;

public class PrinterDashboard implements DashboardObserver {

	@Override
	public void showDashboard(int printCount, int spoolCount) {
		System.out.println("-------------- Dashboard -------------");
		System.out.println("Print Count:        " + printCount);
		System.out.println("Spool Change Count: " + spoolCount);
		System.out.println("--------------------------------------");
	}

}
