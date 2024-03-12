package nl.saxion.app;

import java.util.ArrayList;
import java.util.List;

import nl.saxion.app.model.FilamentType;
import nl.saxion.app.model.Printer;

public class PrintSchedulerImpl implements PrintScheduler {

	@Override
	public void addPrinter(int id, int type, String name, String manufacturer, int maxX, int maxY, int maxZ,
			int maxColors) {
		// TODO Auto-generated method stub

	}

	@Override
	public void selectPrintTask(Printer printer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addPrint(String name, int height, int width, int length, ArrayList<Double> filamentLength,
			int printTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addPrintTask(String printName, List<String> colors, FilamentType type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerPrinterFailure(int printerId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerCompletion(int printerId) {
		// TODO Auto-generated method stub

	}

}
