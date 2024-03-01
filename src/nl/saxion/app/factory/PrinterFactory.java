package nl.saxion.app.factory;

import nl.saxion.app.model.HousedPrinter;
import nl.saxion.app.model.MultiColor;
import nl.saxion.app.model.Printer;
import nl.saxion.app.model.StandardFDM;

// Factory
public class PrinterFactory {

	public static Printer getPrinter(int id, int type, String name, String manufacturer, int maxX, int maxY, int maxZ, int maxColors) {

		if (type == 1) {
			return new StandardFDM(id, name, manufacturer, maxX, maxY, maxZ);
		} else if (type == 2) {
			return new HousedPrinter(id, name, manufacturer, maxX, maxY, maxZ);
		} else if (type == 3) {
			return new MultiColor(id, name, manufacturer, maxX, maxY, maxZ, maxColors);
		}

		return null;

	}

}
