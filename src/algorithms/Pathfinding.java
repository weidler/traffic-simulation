package algorithms;

import java.util.ArrayList;

import datastructures.Intersection;

public class Pathfinding {
	
	ArrayList<Integer> distance = new ArrayList<Integer>();
	
	public int[] neighborDistance(int xCoordinate, int yCoordinate) {
		Intersection intersections = new Intersection(xCoordinate, yCoordinate);
		ArrayList<int[]> neighbors = intersections.getConnections();
		neighbors1 = Intersection;
	}
	
	
	public ArrayList<Integer> aStar(int start, int end) {
	
	}
	
	public ArrayList<Integer> dijkstra(int start, int end) {
		ArrayList<Integer> path = new ArrayList<Integer>();
		
		
		return path;
	}
}
}
