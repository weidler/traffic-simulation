package datastructures;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Intersection {
	
	private int x_coord;
	private int y_coord;
	
	/**
	 * connections are of format (road, intersection)
	 */
	private ArrayList<int[]> connections;
	
	public Intersection(int x, int y) {
		this.x_coord = x;
		this.y_coord = y;
		
		this.connections = new ArrayList<int[]>();
	}
	
	public int getXCoord() {
		return x_coord;
	}
	
	public int getYCoord() {
		return y_coord;
	}
	
	
	public void addConnection(int road_id, int intersection_id) {
		connections.add(new int[]{road_id, intersection_id});
	}
	
	public int removeConnection(int intersection_id) {
		int removed_road = -1;
		for (int i = 0; i < this.connections.size(); i++) {
			if (this.connections.get(i)[1] == intersection_id) {
				removed_road = this.connections.get(i)[0];
				connections.remove(i);
			}
		}
		
		return removed_road;
	}
	
	public ArrayList<Integer> getOutgoingRoadIds() {
		ArrayList<Integer> outgoing = new ArrayList<Integer>();
		for (int[] c : this.connections) {
			outgoing.add(c[0]);
		}
		
		return outgoing;
	}
	
	public ArrayList<Integer> getConnectedIntersectionIds() {
		ArrayList<Integer> intersections = new ArrayList<Integer>();
		for (int[] c : this.connections) {
			intersections.add(c[1]);
		}
		
		return intersections;
	}
	
	public int getRoadTo(int to) {
		for (int[] c : this.connections) {
			if (c[1] == to) {
				return c[0];
			}
		}
		
		return -1;
	}
	
	public ArrayList<int[]> getConnections() {
		return new ArrayList<int[]>(this.connections);
	}
	
	public void adjustConnectionsAfterIntersectionRemoval(int removed_intersection_id) {
		for (int[] c : this.connections) {
			if (c[1] > removed_intersection_id) {
				c[1] = c[1] - 1;
			}
		}
	}
	
	public void adjustConnectionsAfterRoadRemoval(int removed_road_id) {
		for (int[] c : this.connections) {
			if (c[0] > removed_road_id) {
				c[0] = c[0] - 1;
			}
		}
	}
	
	public int numbConnections() {
		return this.connections.size();
	}
	
	public String toString() {
		return "Intersection: (" + this.x_coord + ", " + this.y_coord + ")";
	}
}
