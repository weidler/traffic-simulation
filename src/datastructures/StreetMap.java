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
	 * Array List Matrix M storing Connection objects. A Connection at Mij 
	 * connects the intersections with the IDs i and j.
	 */
	private ArrayList<ArrayList<Connection>> adjacency_matrix;

	// CONSTRUCTORS
	
	public StreetMap(ArrayList<Intersection> intersections) {
		this.intersections = intersections;
		this.adjacency_matrix = new ArrayList<ArrayList<Connection>>();
	}
	
	public StreetMap(ArrayList<ArrayList<Connection>> adjacency_matrix, ArrayList<Intersection> intersections) {
		this.intersections = intersections;
		this.adjacency_matrix = adjacency_matrix;
	}
	
	public StreetMap() {
		this.intersections = new ArrayList<Intersection>();
		this.adjacency_matrix = new ArrayList<ArrayList<Connection>>();
	}
	
	// GRAPH MODIFICATION

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
		ArrayList<Connection> new_row = new ArrayList<Connection>();
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
	
	/**
	 * Adds a Connection by adding the provided object to the adjacency matrix at
	 * entry (start, end).
	 * 
	 * @param start
	 * @param end
	 * @param connection
	 */
	public void addConnection(int start, int end, Connection connection) {
		// make things predictable
		if (start < end) {
			int tmp = start;
			start = end;
			end = tmp;
		}
		
		this.adjacency_matrix.get(start).set(end, connection);
	}
	
	/**
	 * Removes the connection of two Intersections with the indices start and end
	 * by setting the entry in the Matrix to null.
	 * 
	 * @param start
	 * @param end
	 */
	public void removeConnection(int start, int end) {
		// make things predictable
		if (start < end) {
			int tmp = start;
			start = end;
			end = tmp;
		}
		
		this.adjacency_matrix.get(start).set(end, null);
	}
	
	// GETTER / SETTER
	
	/**
	 * Get the entire adjacency matrix.
	 * @return
	 */
	public ArrayList<ArrayList<Connection>> getAdjacencyMatrix() {
		return this.adjacency_matrix;
	}
	
	/**
	 * Get an Intersection based on its ID.
	 * @param ID
	 * @return
	 */
	public Intersection getIntersection(Integer ID) {
		return this.intersections.get(ID);
	}
	
	/**
	 * Get the Connection object storing information about the edge in the graph
	 * connecting the start and end Intersections (provided as Integers)
	 * @param start (integer)
	 * @param end (integer)
	 * @return Connection (object)
	 */
	public Connection getConnection(Integer start, Integer end) {
		if (start < end) {
			int tmp = start;
			start = end;
			end = tmp;
		}
		
		return this.adjacency_matrix.get(start).get(end);
	}
	
	/**
	 * Returns a list of indices to neighbors (connected) Intersections.
	 * TODO Fix the diagonally symmetric matrix problem.
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