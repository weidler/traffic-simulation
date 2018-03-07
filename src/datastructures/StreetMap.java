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
	 * amount of cars in the entire graph.
	 */
	private final int CAR_NUMBER = 50;
	
	public int getCAR_NUMBER() 
	{
		return CAR_NUMBER;
	}
	/**
	 * list of all cars.
	 */
	private ArrayList<Car> cars = new ArrayList();
	public ArrayList<Car> getCarsList()
	{
		return cars;
	}

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

	// CONSTRUCTORS
	public StreetMap() {
		this.intersections = new ArrayList<Intersection>();
		this.roads = new ArrayList<Road>();
	}

	/**
	 * This constructor is risky and probably requires a legality check of the road and intersection objects
	 */
	public StreetMap(ArrayList<Intersection> intersections, ArrayList<Road> roads) {
		this.intersections = intersections;
		this.roads = roads;
	}
	
	// GRAPH MODIFICATION

	/**
	 * Adds a Road between intersections start and end.
	 * 
	 * @param start			ID of the starting intersection
	 * @param end			ID of the ending intersection
	 * @param road			Road object to be added	
	 */
	public void addRoad(int start, int end) {
		// TODO legality check: does road really connect these intersections?
		this.roads.add(new Road(this.intersections.get(start), this.intersections.get(end)));
		
		this.intersections.get(start).addConnection(this.roads.size() - 1, end);
		this.intersections.get(end).addConnection(this.roads.size() - 1, start);
	}
	
	public void addRoad(Road road) {
		this.roads.add(road);
		
		int int_a_id = this.getIntersectionIdByCoordinates(road.getX1(), road.getY1());
		int int_b_id = this.getIntersectionIdByCoordinates(road.getX2(), road.getY2());

		if (int_a_id < 0 || int_b_id < 0) {
			System.out.println("You tried adding a road between at least one missing Intersection. Probably, you should create intersections first, THEN add the road.");
		}
		
		this.intersections.get(int_a_id).addConnection(this.roads.size() - 1, int_b_id);
		this.intersections.get(int_b_id).addConnection(this.roads.size() - 1, int_a_id);
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
		int road_id = this.intersections.get(start).removeConnection(end);
		this.intersections.get(end).removeConnection(start);
		
	
		if (road_id >= 0) {
			this.roads.remove(road_id);
		} else {
			System.out.println("Tried to remove inexistant road!");
		}

		// adjust intersection ids
		for (Intersection intersection : this.intersections) {
			intersection.adjustConnectionsAfterRoadRemoval(road_id);
		}
	}
	
	public void removeRoadBetweenCoordinates(int x1, int y1, int x2, int y2) {
		int intersection_a = this.getIntersectionIdByCoordinates(x1, y1);
		int intersection_b = this.getIntersectionIdByCoordinates(x2, y2);
		
		this.removeRoadBetween(intersection_a, intersection_b);
	}
	
	/**
	 * Removes a road identified by its ID.
	 * 
	 * @param road
	 */
	public void removeRoadById(int road_id) {
		int intersection_a = -1;
		int intersection_b = -1;

		for (int int_id = 0; int_id < this.intersections.size(); int_id++) {
			ArrayList<int[]> connections = this.intersections.get(int_id).getConnections();
			for (int c_id = 0; c_id < connections.size(); c_id++) {
				if (connections.get(c_id)[0] == road_id) {
					intersection_a = int_id;
					intersection_b = connections.get(c_id)[1];
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
		this.intersections.add(intersection);		
	}
	
	/**
	 * Removes an Intersection from the Map, including its column and row inside
	 * the adjacency matrix. **NOTE**, that this is going to shift the indices of ALL
	 * Intersections with a higher index. Didn't come up with a better solution yet, sorry.
	 * @param id
	 */
	public void removeIntersection(int id) {
		Intersection deleted_intersection = this.intersections.remove(id);
		deleted_intersection.adjustConnectionsAfterIntersectionRemoval(id);
		ArrayList<Integer> connected_intersections = deleted_intersection.getConnectedIntersectionIds();
		ArrayList<Integer> connected_roads = deleted_intersection.getOutgoingRoadIds();
		
		// adjust intersection ids
		for (Intersection intersection : this.intersections) {
			intersection.adjustConnectionsAfterIntersectionRemoval(id);
		}
		
		// remove connections from connected intersections
		for (int int_id : connected_intersections) {
			this.intersections.get(int_id).removeConnection(id);
		}
		
		// remove old roads
		for (int road_id : connected_roads) {
			this.roads.remove(road_id);
			for (Intersection intersection : this.intersections) {
				intersection.adjustConnectionsAfterRoadRemoval(road_id);				
			}
		}
	}
	
	public void clearMap() {
		this.intersections.clear();
		this.roads.clear();
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
	public Intersection getIntersection(int ID) {
		return this.intersections.get(ID);
	}
	
	public int getIntersectionIdByCoordinates(int x, int y) {
		for (int intersection_id = 0; intersection_id < this.intersections.size(); intersection_id++) {
			if (this.intersections.get(intersection_id).getXCoord() == x && this.intersections.get(intersection_id).getYCoord() == y) {
				return intersection_id;
			}
		}
		
		return -1;
	}
	
	public Intersection getIntersectionByCoordinates(int x, int y) {
		return this.getIntersection(this.getIntersectionIdByCoordinates(x, y));
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
		return this.roads.get(this.intersections.get(start).getRoadTo(end));
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
	public ArrayList<Integer> getNeighbors(int intersection) {
		return this.intersections.get(intersection).getConnectedIntersectionIds();
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