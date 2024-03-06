package nl.saxion.app.singleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.saxion.app.factory.PrintTaskFactory;
import nl.saxion.app.factory.PrinterFactory;
import nl.saxion.app.model.FilamentType;
import nl.saxion.app.model.Print;
import nl.saxion.app.model.PrintTask;
import nl.saxion.app.model.Printer;
import nl.saxion.app.model.Spool;

// Singleton
public class PrinterManager {

	private List<Printer> printers = new ArrayList<Printer>();
	private List<Print> prints = new ArrayList<Print>();
	private List<Spool> spools = new ArrayList<Spool>();
	private List<Spool> freeSpools = new ArrayList<>();
	private List<Printer> freePrinters = new ArrayList<>();
	private List<PrintTask> pendingPrintTasks = new ArrayList<>();
	private Map<Printer, PrintTask> runningPrintTasks = new HashMap<Printer, PrintTask>();

	private static PrinterManager instance;

	private PrinterManager() {
		// Private constructor to prevent instantiation
	}

	public static PrinterManager getInstance() {
		if (instance == null) {
			instance = new PrinterManager();
		}
		return instance;
	}

	public void addPrinter(int id, int type, String name, String manufacturer, int maxX, int maxY, int maxZ, int maxColors) {
		Printer printer = PrinterFactory.getPrinter(id, type, name, manufacturer, maxX, maxY, maxZ, maxColors);
		printers.add(printer);
		freePrinters.add(printer);
	}

	public void selectPrintTask(Printer printer) {

		Spool[] spools = printer.getCurrentSpools();
		PrintTask chosenTask = null;

		// First we look if there's a task that matches the current spool on the printer.
		if(spools[0] != null) {
			for (PrintTask printTask : pendingPrintTasks) {
				if (printer.printFits(printTask.getPrint())) {
					chosenTask = PrintTaskFactory.getPrintTask(printer, printTask, spools);
					runningPrintTasks.put(printer, printTask);
					freePrinters.remove(printer);
					break;
				}
			}
		}

		// If we didn't find a print for the current spool we search for a print with the free spools.
		if(chosenTask == null) {
			for(PrintTask printTask: pendingPrintTasks) {
				if(printer.printFits(printTask.getPrint()) && getPrinterCurrentTask(printer) == null) {
					chosenTask = PrintTaskFactory.getPrintTask(printer, printTask, freeSpools);
					runningPrintTasks.put(printer, printTask);
					freePrinters.remove(printer);
				}
			}
		}

		if(chosenTask != null) {
			pendingPrintTasks.remove(chosenTask);
			System.out.println("- Started task: " + chosenTask + " on printer " + printer.getName());
		}
	}

	public void startInitialQueue() {
		for(Printer printer: printers) {
			selectPrintTask(printer);
		}
	}

	public void addPrint(String name, int height, int width, int length, ArrayList<Double> filamentLength, int printTime) {
		Print p = new Print(name, height, width, length, filamentLength, printTime);
		prints.add(p);
	}

	public List<Print> getPrints() {
		return prints;
	}

	public List<Printer> getPrinters() {
		return printers;
	}

	public PrintTask getPrinterCurrentTask(Printer printer) {
		if(!runningPrintTasks.containsKey(printer)) {
			return null;
		}
		return runningPrintTasks.get(printer);
	}

	public List<PrintTask> getPendingPrintTasks() {return pendingPrintTasks; }

	public void addPrintTask(String printName, List<String> colors, FilamentType type) {
		Print print = findPrint(printName);
		if (print == null) {
			printError("Could not find print with name " + printName);
			return;
		}
		if (colors.size() == 0) {
			printError("Need at least one color, but none given");
			return;
		}
		for (String color : colors) {
			boolean found = false;
			for (Spool spool : spools) {
				if (spool.getColor().equals(color) && spool.getFilamentType() == type) {
					found = true;
					break;
				}
			}
			if (!found) {
				printError("Color " + color + " (" + type +") not found");
				return;
			}
		}

		PrintTask task = new PrintTask(print, colors, type);
		pendingPrintTasks.add(task);
		System.out.println("Added task to queue");

	}

	public Print findPrint(String printName) {
		for (Print p : prints) {
			if (p.getName().equals(printName)) {
				return p;
			}
		}
		return null;
	}

	public Print findPrint(int index) {
		if(index > prints.size() -1) {
			return null;
		}
		return prints.get(index);
	}

	public void addSpool(Spool spool) {
		spools.add(spool);
		freeSpools.add(spool);
	}

	public List<Spool> getSpools() {
		return spools;
	}

	public Spool getSpoolByID(int id) {
		for(Spool s: spools) {
			if(s.getId() == id) {
				return s;
			}
		}
		return null;
	}

	public void registerPrinterFailure(int printerId) {
		Map.Entry<Printer, PrintTask> foundEntry = null;
		for (Map.Entry<Printer, PrintTask> entry : runningPrintTasks.entrySet()) {
			if (entry.getKey().getId() == printerId) {
				foundEntry = entry;
				break;
			}
		}
		if (foundEntry == null) {
			printError("cannot find a running task on printer with ID " + printerId);
			return;
		}
		PrintTask task = foundEntry.getValue();
		pendingPrintTasks.add(task); // add the task back to the queue.
		runningPrintTasks.remove(foundEntry.getKey());

		System.out.println("Task " + task + " removed from printer "
				+ foundEntry.getKey().getName());

		Printer printer = foundEntry.getKey();
		Spool[] spools = printer.getCurrentSpools();
		for(int i=0; i<spools.length && i < task.getColors().size();i++) {
			spools[i].reduceLength(task.getPrint().getFilamentLength().get(i));
		}
		selectPrintTask(printer);
	}

	public void registerCompletion(int printerId) {
		Map.Entry<Printer, PrintTask> foundEntry = null;
		for (Map.Entry<Printer, PrintTask> entry : runningPrintTasks.entrySet()) {
			if (entry.getKey().getId() == printerId) {
				foundEntry = entry;
				break;
			}
		}
		if (foundEntry == null) {
			printError("cannot find a running task on printer with ID " + printerId);
			return;
		}
		PrintTask task = foundEntry.getValue();
		runningPrintTasks.remove(foundEntry.getKey());

		System.out.println("Task " + task + " removed from printer "
				+ foundEntry.getKey().getName());

		Printer printer = foundEntry.getKey();
		Spool[] spools = printer.getCurrentSpools();
		for(int i=0; i<spools.length && i < task.getColors().size();i++) {
			spools[i].reduceLength(task.getPrint().getFilamentLength().get(i));
		}

		selectPrintTask(printer);

	}

	private void printError(String s) {
		System.out.println("---------- Error Message ----------");
		System.out.println("Error: "+s);
		System.out.println("--------------------------------------");
	}

}
