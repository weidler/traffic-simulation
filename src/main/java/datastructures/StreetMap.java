package datastructures;

import java.util.ArrayList;
import java.util.HashMap;

import datatype.Line;
import datatype.Point;
import road.DirtRoad;
import road.Highway;
import road.Road;
import type.RoadType;
import util.Geometry;

/**
 * @author weidler
 *
 *         A StreetMap is an undirected graph with Intersections as its vertices
 *         and Connections as its edges. The graph is represented as an
 *         adjacency matrix. That is, it holds all edges in a matrix where the
 *         columns and rows represent the vertex indices.
 */
public class StreetMap {

	/**
	 * Array List storing Intersections of the map. Index represents ID of the
	 * Intersection inside the AdjacencyMatrix.
	 */
	private ArrayList<Intersection> intersections;
	private static double CurrentTime = 0;
	public void setCurrentTime(double t) {
		CurrentTime = t;
	}
	
	public static double getCurrentTime() 
	{
		return CurrentTime;
	}

	/**
	 * Array List storing Roads of the map.;
	 */
	private ArrayList<Road> roads = new ArrayList<Road>();

	@Override
	public String toString() {

		String result = "";
		for (Intersection in : intersections) {
			result = result.concat(in.toString());
		}

		result = result.concat("#,");

		for (Road r : roads) {
			result = result.concat(r.getX1() + "," + r.getY1() + "," + r.getX2() + "," + r.getY2() + "," + r.getRoadType() + "," + r.getLanes() + ",");
		}
		result = result.concat("p,");
		System.out.println(result);
		return result;
	}

	// CONSTRUCTORS

	public StreetMap() {
		this.intersections = new ArrayList<Intersection>();
		this.roads = new ArrayList<Road>();
	}

	/**
	 * This constructor is risky and probably requires a legality check of the road
	 * and intersection objects
	 */
	public StreetMap(ArrayList<Intersection> intersections, ArrayList<Road> roads) {
		this.intersections = intersections;
		this.roads = roads;
	}

	// GETTER / SETTER
	public ArrayList<Road> getRoads() {
		return roads;
	}

	public Intersection getIntersection(int ID) {
		return this.intersections.get(ID);
	}

	public int getIntersectionIdByCoordinates(int x, int y) {
		for (int intersection_id = 0; intersection_id < this.intersections.size(); intersection_id++) {
			if (this.intersections.get(intersection_id).getXCoord() == x
					&& this.intersections.get(intersection_id).getYCoord() == y) {
				return intersection_id;
			}
		}

		return -1;
	}

	public Intersection getIntersectionByCoordinates(int x, int y) {
		return this.getIntersection(this.getIntersectionIdByCoordinates(x, y));
	}

	public ArrayList<Intersection> getIntersections() {
		return intersections;
	}

	/**
	 * Get the Road object identified by coordinates. If there is no road between
	 * these coordinates, null is returned.
	 */
	public Road getRoadByCoordinates(int x_start, int y_start, int x_end, int y_end) {
		for (Road road : this.roads) {
			if (road.getX1() == x_start && road.getY1() == y_start && road.getX2() == x_end && road.getY2() == y_end
					|| road.getX2() == x_start && road.getY2() == y_start && road.getX1() == x_end
							&& road.getY1() == y_end) {
				return road;
			}
		}

		return null;
	}

	public ArrayList<TrafficLight> getTrafficLights() {
		ArrayList<TrafficLight> all_trafficlights = new ArrayList<TrafficLight>();
		for (Intersection intersection : this.intersections) {
			for (ArrayList<TrafficLight> tls : intersection.getTrafficLights()) {
				all_trafficlights.addAll(tls);
			}
			;
		}

		return all_trafficlights;
	}

	// GRAPH MODIFICATION
	public void addRoad(Road road) {
		Intersection int_a = this.getIntersectionByCoordinates(road.getX1(), road.getY1());
		Intersection int_b = this.getIntersectionByCoordinates(road.getX2(), road.getY2());

		road.setStreetMap(this);

		if (int_a == null || int_b == null) {
			System.out.println("You tried adding a road between at least one missing Intersection."
					+ "Probably, you should create intersections first, THEN add the road.");
		} else if (this.roadAlreadyOccupied(road)) {
			System.out
					.println("There already exists a road between these coordinates/intersections. Skipping addition.");
		} else {
			// Check if road intersects other road
			Road crossed_road = null;
			for (Road r : this.roads) {
				if (this.roadsIntersect(r, road)) {
					crossed_road = r;
					break;
				}
			}

			if (crossed_road != null) {
				Intersection new_intersection = new Intersection(
					Geometry.intersection(
							new Line(road.getPointA(), road.getPointB()),
							new Line(crossed_road.getPointA(), crossed_road.getPointB())
					)
				);

				this.addIntersection(new_intersection);

				// remove crossed road
				this.removeRoad(crossed_road);

				// add four new roads; do this recursively to allow multiple intersections
				Road new_road_part_a = new Road(int_a, new_intersection, this, road.getLanes());
				Road new_road_part_b = new Road(int_b, new_intersection, this, road.getLanes());
				if (road.getRoadType() == RoadType.HIGHWAY) {
					new_road_part_a = new Highway(int_a, new_intersection, this, road.getLanes());
					new_road_part_b = new Highway(int_b, new_intersection, this, road.getLanes());
				} else if (road.getRoadType() == RoadType.HIGHWAY) {
					new_road_part_a = new DirtRoad(int_a, new_intersection, this, road.getLanes());
					new_road_part_b = new DirtRoad(int_b, new_intersection, this, road.getLanes());
				}

				Road old_road_part_a = new Road(crossed_road.getIntersections(this)[0], new_intersection,
						this, crossed_road.getLanes());
				Road old_road_part_b = new Road(crossed_road.getIntersections(this)[1], new_intersection,
						this, crossed_road.getLanes());
				if (road.getRoadType() == RoadType.DIRT_ROAD) {
					old_road_part_a = new Highway(int_a, new_intersection, this, crossed_road.getLanes());
					old_road_part_b = new Highway(int_b, new_intersection, this, crossed_road.getLanes());
				} else if (road.getRoadType() == RoadType.DIRT_ROAD) {
					old_road_part_a = new DirtRoad(int_a, new_intersection, this, crossed_road.getLanes());
					old_road_part_b = new DirtRoad(int_b, new_intersection, this, crossed_road.getLanes());
				}

				this.addRoad(new_road_part_a);
				this.addRoad(new_road_part_b);
				this.addRoad(old_road_part_a);
				this.addRoad(old_road_part_b);
			} else {
				this.roads.add(road);
				int_a.addConnection(road, int_b, null);
				int_b.addConnection(road, int_a, null);				
			}
		}
	}

	public void removeRoad(Road road) {
		this.roads.remove(road);
		for (Intersection intersection : this.intersections) {
			intersection.adjustConnectionsAfterRoadRemoval(road);
		}
	}

	public void removeRoadBetween(Intersection a, Intersection b) {
		Road road = a.removeConnectionTo(b);
		b.removeConnectionTo(a);

		if (road != null) {
			this.removeRoad(road);
		} else {
			System.out.println("Tried to remove inexistant road!");
		}
	}

	public void removeRoadBetweenCoordinates(int x1, int y1, int x2, int y2) {
		Intersection intersection_a = this.getIntersectionByCoordinates(x1, y1);
		Intersection intersection_b = this.getIntersectionByCoordinates(x2, y2);

		this.removeRoadBetween(intersection_a, intersection_b);
	}

	public Intersection addIntersection(Intersection intersection) {
		if (this.intersectionAlreadyExists(intersection)) {
			System.out.println("Intersection already exists at these coordinates. Skipping addition. x: "
					+ intersection.getXCoord() + ", y: " + intersection.getYCoord());
		} else {
			this.intersections.add(intersection);
			return intersection;
		}
		
		return null;
	}

	public Intersection addIntersection(int x, int y) {
		Intersection inter = new Intersection(x, y);
		return this.addIntersection(inter);
	}
	
	public Intersection addIntersection(Point p) {
		Intersection inter = new Intersection((int) p.x, (int) p.y);
		return this.addIntersection(inter);
	}
	
	public void removeIntersection(Intersection removed_intersection) {
		this.intersections.remove(removed_intersection);

		ArrayList<Intersection> connected_intersections = removed_intersection.getConnectedIntersections();
		ArrayList<Road> connected_roads = removed_intersection.getOutgoingRoads();

		// adjust intersections
		for (Intersection adjusted_intersection : connected_intersections) {
			adjusted_intersection.adjustConnectionsAfterIntersectionRemoval(removed_intersection);
			if (adjusted_intersection.numbConnections() == 0) {
				this.intersections.remove(adjusted_intersection);
			}
		}

		// remove old roads
		for (Road road : connected_roads) {
			this.roads.remove(road);
			for (Intersection intersection : this.intersections) {
				intersection.adjustConnectionsAfterRoadRemoval(road);
			}
		}
	}

	public void clearMap() {
		this.intersections.clear();
		this.roads.clear();
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

	// ACTIONS

	public void update(double delta_t) {
		for (Intersection intersection : this.intersections) {
			intersection.updateTrafficLights(delta_t);
		}
	}

	// CHECKS

	private boolean roadAlreadyOccupied(Road road) {
		for (Road r : this.roads) {
			if (road.equalCoordinatesWith(r)) {
				return true;
			}
		}

		return false;
	}

	private boolean intersectionAlreadyExists(Intersection intersection) {
		for (Intersection is : this.intersections) {
			if (intersection.equalCoordinatesWith(is)) {
				return true;
			}
		}

		return false;
	}
	
	private boolean roadsIntersect(Road a, Road b) {
		return Geometry.lineSegmentsIntersect(
				a.getPointA(),
				a.getPointB(),
				b.getPointA(),
				b.getPointB()
		);
	}
}