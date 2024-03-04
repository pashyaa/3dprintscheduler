package nl.saxion.app;

import nl.saxion.app.facade.PrinterFacade;

public class Main {

	public static void main(String[] args) {
		new Main().run(args);
	}

	public void run(String[] args) {
		PrinterFacade facade = new PrinterFacade();
		try {
			facade.readFromFiles(args);
			facade.showMenu();
			facade.exit();
		} catch (Exception e) {
			System.out.println("Error running application: " + e.getMessage());
		}
	}
}
