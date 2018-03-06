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
		GraphicalInterface gui = new GraphicalInterface();
		gui.setVisible(true);
		
		StreetMap map = new StreetMap();
		map.addIntersection(new Intersection(10, 10));
		map.addIntersection(new Intersection(20, 20));
		map.addIntersection(new Intersection(30, 30));
		
		map.addRoad(new Road(10, 10, 20, 20));
		map.addRoad(new Road(20, 20, 30, 30));
		map.addRoad(new Road(10, 10, 30, 30));
				
		System.out.println(map.getIntersections());
		System.out.println(map.getRoads());
		
		map.removeRoadBetween(map.getIntersectionIdByCoordinates(10, 10), map.getIntersectionIdByCoordinates(20, 20));
		
		System.out.println(map.getIntersections());
		System.out.println(map.getRoads());
		
		map.removeIntersection(map.getIntersectionIdByCoordinates(10, 10));
		
		System.out.println(map.getIntersections());
		System.out.println(map.getRoads());
		
		System.out.println(map.getRoadBetween(map.getIntersectionIdByCoordinates(20, 20), map.getIntersectionIdByCoordinates(30, 30)));
	}
}
