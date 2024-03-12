package nl.saxion.app;

import java.util.ArrayList;
import java.util.List;

import nl.saxion.app.model.FilamentType;
import nl.saxion.app.model.Printer;

public interface PrintScheduler {

	void addPrinter(int id, int type, String name, String manufacturer, int maxX, int maxY, int maxZ, int maxColors);

	void selectPrintTask(Printer printer);

	void addPrint(String name, int height, int width, int length, ArrayList<Double> filamentLength, int printTime);

	void addPrintTask(String printName, List<String> colors, FilamentType type);

	void registerPrinterFailure(int printerId);

	void registerCompletion(int printerId);

}
