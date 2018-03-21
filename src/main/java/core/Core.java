package core;

import graphical_interface.GraphicalInterface;
import datastructures.*;

/**
 * 
 * @author thomas 
 * main class
 * it calls the interface
 *
 */

public class Core {
	
	public static void main(String[] args) {
		
		StreetMap street_map = new StreetMap();
		Simulation simulation = new Simulation(street_map);		
		GraphicalInterface gui = new GraphicalInterface(simulation);
		simulation.setGUI(gui);
		
		
		gui.setVisible(true);
	}
}
