package nl.saxion.app.observer;

public interface DashboardObservable {
	void attach(DashboardObserver dOb);
	void detach(DashboardObserver dOb);
	void notifyObservers(int printCount, int spoolCount);
}
