package nl.saxion.app.factory;

import java.util.ArrayList;
import java.util.List;

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

	public static PrintTask getPrintTask(Printer printer, PrintTask printTask, List<Spool> freeSpools) {
		if (printer instanceof StandardFDM && printTask.getFilamentType() != FilamentType.ABS && printTask.getColors().size() == 1) {
			Spool chosenSpool = null;
			for (Spool spool : freeSpools) {
				if (spool.spoolMatch(printTask.getColors().get(0), printTask.getFilamentType())) {
					chosenSpool = spool;
				}
			}
			if (chosenSpool != null) {
				freeSpools.add(printer.getCurrentSpools()[0]);
				System.out.println("- Spool change: Please place spool " + chosenSpool.getId() + " in printer " + printer.getName());
				freeSpools.remove(chosenSpool);
				((StandardFDM) printer).setCurrentSpool(chosenSpool);
				return printTask;
			}
		} else if (printer instanceof HousedPrinter && printTask.getColors().size() == 1) {
			Spool chosenSpool = null;
			for (Spool spool : freeSpools) {
				if (spool.spoolMatch(printTask.getColors().get(0), printTask.getFilamentType())) {
					chosenSpool = spool;
				}
			}
			if (chosenSpool != null) {
				freeSpools.add(printer.getCurrentSpools()[0]);
				System.out.println("- Spool change: Please place spool " + chosenSpool.getId() + " in printer " + printer.getName());
				freeSpools.remove(chosenSpool);
				((StandardFDM) printer).setCurrentSpool(chosenSpool);
				return printTask;
			}
		} else if (printer instanceof MultiColor && printTask.getFilamentType() != FilamentType.ABS && printTask.getColors().size() <= ((MultiColor) printer).getMaxColors()) {
			ArrayList<Spool> chosenSpools = new ArrayList<>();
			for (int i = 0; i < printTask.getColors().size(); i++) {
				for (Spool spool : freeSpools) {
					if (spool.spoolMatch(printTask.getColors().get(i), printTask.getFilamentType()) && !containsSpool(chosenSpools, printTask.getColors().get(i))) {
						chosenSpools.add(spool);
					}
				}
			}
			// We assume that if they are the same length that there is a match.
			if (chosenSpools.size() == printTask.getColors().size()) {
				for (Spool spool : printer.getCurrentSpools()) {
					freeSpools.add(spool);
				}
				printer.setCurrentSpools(chosenSpools);
				int position = 1;
				for (Spool spool : chosenSpools) {
					System.out.println("- Spool change: Please place spool " + spool.getId() + " in printer " + printer.getName() + " position " + position);
					freeSpools.remove(spool);
					position++;
				}
				return printTask;
			}
		}
		return null;
	}

	private static boolean containsSpool(final List<Spool> list, final String name) {
		return list.stream().anyMatch(o -> o.getColor().equals(name));
	}

}
