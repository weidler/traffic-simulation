package algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import datastructures.Intersection;
import datastructures.Node;
import datastructures.StreetMap;

public class Pathfinding {
	
	private ArrayList<Integer> distance = new ArrayList<Integer>();
	private Set<Node> nodes = new HashSet<>();
	
	public int[] neighborDistance(int xCoordinate, int yCoordinate) {
		Intersection intersections = new Intersection(xCoordinate, yCoordinate);
		ArrayList<int[]> neighbors = intersections.getConnections();
		System.out.println(neighbors[1]);
	}
	
	
	public ArrayList<Integer> aStar(int start, int end) {
	
	}
	
	public ArrayList<Integer> dijkstra(int start, int end) {
		ArrayList<Integer> path = new ArrayList<Integer>();
		
		
		return path;
	}
}
}
