package nl.saxion.app.factory;

import nl.saxion.app.model.FilamentType;
import nl.saxion.app.model.HousedPrinter;
import nl.saxion.app.model.MultiColor;
import nl.saxion.app.model.PrintTask;
import nl.saxion.app.model.Printer;
import nl.saxion.app.model.Spool;
import nl.saxion.app.model.StandardFDM;

// Factory
public class PrintTaskFactory {

	public static PrintTask getPrintTask(Printer printer, PrintTask printTask, Spool[] spools) {
		if (printer instanceof StandardFDM && printTask.getFilamentType() != FilamentType.ABS && printTask.getColors().size() == 1) {
			if (spools[0].spoolMatch(printTask.getColors().get(0), printTask.getFilamentType())) {
				return printTask;
			}
			// The housed printer is the only one that can print ABS, but it can also print the others.
		} else if (printer instanceof HousedPrinter && printTask.getColors().size() == 1) {
			if (spools[0].spoolMatch(printTask.getColors().get(0), printTask.getFilamentType())) {
				return printTask;
			}
			// For multicolor the order of spools does matter, so they have to match.
		} else if (printer instanceof MultiColor && printTask.getFilamentType() != FilamentType.ABS && printTask.getColors().size() <= ((MultiColor) printer).getMaxColors()) {
			boolean printWorks = true;
			for (int i = 0; i < spools.length && i < printTask.getColors().size(); i++) {
				if (!spools[i].spoolMatch(printTask.getColors().get(i), printTask.getFilamentType())) {
					printWorks = false;
				}
			}
			if (printWorks) {
				return printTask;
			}
		}
		return null;
	}

}
