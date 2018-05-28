package core;

import graphical_interface.GraphicalInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import datastructures.*;

/**
 * 
 * @author thomas main class it calls the interface
 *
 */

public class Core {

	public static void main(String[] args) {

		File configFile = new File("config/config.properties");
		try {
			// LOAD SETTINGS
			FileReader reader = new FileReader(configFile);
			Properties props = new Properties();
			props.load(reader);
			reader.close();

			// START APPLICATION
			StreetMap street_map = new StreetMap();
			Simulation simulation = new Simulation(street_map, props);
			GraphicalInterface gui = new GraphicalInterface(simulation);
			simulation.setGUI(gui);
			gui.setVisible(true);

		} catch (Exception ex) {
			System.out.println("Properties couldn't be loaded.");
			ex.printStackTrace();
		}
	}
}
