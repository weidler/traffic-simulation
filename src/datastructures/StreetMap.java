package datastructures;

import java.util.ArrayList;

/**
 * 
 * @author weidler
 *
 * A StreetMap is an undirected graph with Intersections as its vertices and 
 * Connections as its edges. The graph is represented as an adjacency matrix.
 * That is, it holds all edges in a matrix where the columns and rows represent
 * the vertex indices.
 *
 */
public class StreetMap {
	
	/**
	 * Array List storing Intersections of the map. Index represents ID of the Intersection
	 * inside the AdjacencyMatrix.
	 */
	private ArrayList<Intersection> intersections;
	/**
	 * 
	 * Array List storing Roads of the map.;
	 */
	private ArrayList<Road> roads = new ArrayList<Road>();
	/**
	 * Array List Matrix M storing Connection objects. A Connection at Mij 
	 * connects the intersections with the IDs i and j.
	 */
	private ArrayList<ArrayList<Integer>> adjacency_matrix;

	// CONSTRUCTORS
	public StreetMap() {
		this.intersections = new ArrayList<Intersection>();
		this.roads = new ArrayList<Road>();
		this.adjacency_matrix = new ArrayList<ArrayList<Integer>>();
	}

	public StreetMap(ArrayList<Intersection> intersections, ArrayList<Road> roads, ArrayList<ArrayList<Integer>> adjacency_matrix) {
		this.intersections = intersections;
		this.roads = roads;
		this.adjacency_matrix = adjacency_matrix;
	}
	
	// GRAPH MODIFICATION

	/**
	 * Adds a Road between intersections start and end.
	 * 
	 * @param start			ID of the starting intersection
	 * @param end			ID of the ending intersection
	 * @param road			Road object to be added	
	 */
	public void addRoad(int start, int end, Road road) {
		this.roads.add(road);
		this.adjacency_matrix.get(start).set(end, this.roads.size() - 1);
		this.adjacency_matrix.get(end).set(start, this.roads.size() - 1);
	}
	
	/**
	 * Removes a road identified by the connected intersections. Always choose this over
	 * removing by ID if possible, since this is faster!
	 * 
	 * @param start		ID of one of the intersections
	 * @param end		ID of the other intersection
	 * @param road		ID of the road to be removed
	 */
	public void removeRoadBetween(int start, int end) {
		int road = this.adjacency_matrix.get(start).get(end);
		
		this.adjacency_matrix.get(start).set(end, null);
		this.adjacency_matrix.get(end).set(start, null);
		
		this.roads.remove(road);
	}
	
	/**
	 * Removes a road identified by its ID.
	 * 
	 * @param road
	 */
	public void removeRoadById(int road) {
		int intersection_a = -1;
		int intersection_b = -1;

		for (int row = 0; row < this.adjacency_matrix.size(); row++) {
			for (int col = 0; row < this.adjacency_matrix.get(row).size(); col++) {
				if (this.adjacency_matrix.get(row).get(col) == road) {
					intersection_a = row;
					intersection_b = col;
					break;
				}
			}
			
			if (intersection_a >= 0 && intersection_b >= 0) {
				break;
			}
		}
		
		this.removeRoadBetween(intersection_a, intersection_b);
	}
	
	/**
	 * Adds an Intersection by appending the provided object to the lookup
	 * table and inserting row and column to the adjacency matrix.
	 * 
	 * @param intersection
	 */
	public void addIntersection(Intersection intersection) {
		// first add new intersection to lookup table -> easy
		this.intersections.add(intersection);
		
		// now add new column and row to adjacency matrix -> a bit more chaotic
		ArrayList<Integer> new_row = new ArrayList<Integer>();
		for (int i = 0; i < this.intersections.size() - 1; i++) {
			new_row.add(null);
		}
		this.adjacency_matrix.add(new_row);
		
		for (int i = 0; i < this.intersections.size(); i++) {
			this.adjacency_matrix.get(i).add(null);
		}
	}
	
	/**
	 * Removes an Intersection from the Map, including its column and row inside
	 * the adjacency matrix. Note, that this is going to shift the indices of ALL
	 * Intersections with a higher index. Didn't come up with a better solution yet, sorry.
	 * @param id
	 */
	public void removeIntersection(int id) {
		// lets start easy
		this.intersections.remove(id);
		
		// this is messy. sorry.
		this.adjacency_matrix.remove(id);
		for (int i = 0; i < this.intersections.size(); i++) {
			this.adjacency_matrix.get(i).remove(id);
		}
	}
	
	public void clearMap() {
		this.intersections.clear();
		this.roads.clear();
		this.adjacency_matrix.clear();
	}
	
	// GETTER / SETTER
	public ArrayList<Road> getRoads()
	{
		return roads;
	}
	
	/**
	 * Get an Intersection based on its ID.
	 * @param ID
	 * @return
	 */
	public Intersection getIntersection(Integer ID) {
		return this.intersections.get(ID);
	}
	
	public ArrayList<Intersection> getIntersections ()
	{
		return intersections;
	}

	/**
	 * Get the Road object connecting the start and end Intersections (provided as Integers)
	 * @param start (integer)
	 * @param end (integer)
	 * @return Road (object)
	 */
	public Road getRoadBetween(int start, int end) {
		return this.roads.get(this.adjacency_matrix.get(start).get(end));
	}
	
	/**
	 * Get the Road object identified by coordinates. If there is no road between these coordinates,
	 * null is returned.
	 * 
	 * @param start (integer)
	 * @param end (integer)
	 * @return Road (object)
	 */
	public Road getRoadByCoordinates(int x_start, int y_start, int x_end, int y_end) {
		for (Road road : this.roads) {
			if (road.getX1() == x_start && road.getY1() == y_start && road.getX2() == x_end && road.getY2() == y_end) {
				return road;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns a list of indices to neighbors (connected) Intersections.
	 * 
	 * @param base_intersection
	 * @return
	 */
	public ArrayList<Integer> getNeighbors(Integer base_intersection) {
		ArrayList<Integer> neighbors = new ArrayList<Integer>();
		int currentID = 0;
		for (Object intersection : this.adjacency_matrix.get(base_intersection)) {
			if (intersection != null) {
				neighbors.add(currentID);
			}
			
			currentID++;
		}
		
		return neighbors;
	}
	
	// META INFORMATION
	public int roadCount() {
		return this.roads.size();
	}
	
	public int intersectionCount() {
		return this.intersections.size();
	}
	
	// PATH FINDING
	
	public ArrayList<Integer> shortestPathAStar(int start, int end) {
		ArrayList<Integer> path = new ArrayList<Integer>();
		
		return path;
	}
	
	public ArrayList<Integer> shortestPathDijkstra(int start, int end) {
		ArrayList<Integer> path = new ArrayList<Integer>();
		
		return path;
	}
}