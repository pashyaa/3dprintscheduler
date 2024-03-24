package nl.saxion.app;

import java.util.ArrayList;
import java.util.List;

import nl.saxion.app.observer.DashboardObservable;
import nl.saxion.app.observer.DashboardObserver;

public class DashboardManager implements DashboardObservable {

	private List<DashboardObserver> dObservers;

	public DashboardManager() {
		this.dObservers = new ArrayList<>();
	}

	@Override
	public void attach(DashboardObserver dOb) {
		dObservers.add(dOb);
	}

	@Override
	public void detach(DashboardObserver dOb) {
		dObservers.remove(dOb);
	}

	@Override
	public void notifyObservers(int printCount, int spoolCount) {
		for (DashboardObserver observer : dObservers) {
			observer.showDashboard(printCount, spoolCount);
		}
	}

	public void showDashboard(int printCount, int spoolCount) {
		notifyObservers(printCount, spoolCount);
	}

}
