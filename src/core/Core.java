package core;

import graphical_interface.GraphicalInterface;
/**
 * 
 * @author thomas 
 * main class
 * it calls the interface
 *
 */

public class Core {
	
	public static void main(String[] args) {
		GraphicalInterface gui = new GraphicalInterface();
		gui.setVisible(true);
	}
}
