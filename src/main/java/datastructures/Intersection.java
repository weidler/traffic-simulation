package datastructures;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import datatype.Point;
import road.Road;
import type.IntersectionTypes;

public class Intersection {

	// Position
	private int x_coord;
	private int y_coord;

	// Traffic Lights
	private double tl_phase_length;
	private double time_till_toggle;

	// A Star stuff
	private double cost;
	private Intersection parent;
	private double g;
	private double h;

	private int active_light = 0;
	private IntersectionTypes type;

	public IntersectionTypes getType() {
		return type;
	}

	public void setType(IntersectionTypes t) {
		type = t;
	}

	// Connections
	private ArrayList<Connection> connections;

	// CONSTRUCTORS

	public Intersection(int x, int y) {
		this.x_coord = x;
		this.y_coord = y;

		this.tl_phase_length = 5;
		this.time_till_toggle = this.tl_phase_length;

		this.connections = new ArrayList<Connection>();
	}
	
	public Intersection(Point p) {
		this((int) p.x, (int) p.y);
	}

	// GETTERS / SETTERS

	public int getXCoord() {
		return x_coord;
	}

	public int getYCoord() {
		return y_coord;
	}

	public double getTlPhaseLength() {
		return tl_phase_length;
	}

	public void setTlPhaseLength(double tl_phase_length) {
		this.tl_phase_length = tl_phase_length;
	}

	public void resetCost() {
		cost = 1000000000;
	}

	public void resetParent() {
		parent = null;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double distance) {
		this.cost = distance;
	}

	public void setG(double g) {
		this.g = g;
	}

	public double getG() {
		return g;
	}

	public void setH(double h) {
		this.h = h;
	}

	public double getH() {
		return h;
	}

	public Intersection getParent() {
		return parent;
	}

	public void setParent(Intersection parent) {
		this.parent = parent;
	}

	public ArrayList<Road> getOutgoingRoads() {
		ArrayList<Road> outgoing = new ArrayList<Road>();
		for (Connection c : this.connections) {
			outgoing.add(c.getRoad());
		}

		return outgoing;
	}

	public ArrayList<Intersection> getConnectedIntersections() {
		ArrayList<Intersection> intersections = new ArrayList<Intersection>();
		for (Connection c : this.connections) {
			intersections.add(c.getDestination());
		}

		return intersections;
	}

	public Road getRoadTo(Intersection destination) {
		for (Connection c : this.connections) {
			if (c.getDestination() == destination) {
				return c.getRoad();
			}
		}

		return null;
	}

	public ArrayList<Connection> getConnections() {
		return this.connections;
	}

	public ArrayList<ArrayList<TrafficLight>> getTrafficLights() {
		ArrayList<ArrayList<TrafficLight>> traffic_lights = new ArrayList<ArrayList<TrafficLight>>();
		for (Connection c : this.connections) {
			traffic_lights.add(c.getTrafficlights());
		}

		return traffic_lights;
	}

	public ArrayList<TrafficLight> getTrafficLightsApproachingFrom(Intersection origin_intersection) {
		if (this.getConnectedIntersections().contains(origin_intersection)) {
			for (Connection c : this.connections) {
				if (c.getDestination() == origin_intersection) {
					return c.getTrafficlights();
				}
			}
		}

		return null;
	}

	public TrafficLight getTrafficLightsApproachingFrom(Intersection origin_intersection, int lane) {
		for (TrafficLight t : this.getTrafficLightsApproachingFrom(origin_intersection)) {
			if (t.getLane() == lane) return t;
		}

		return null;
	}

	// MODIFICATION

	/**
	 * 
	 * @param road
	 * @param intersection
	 *            the intersection which this intersection has a new connection to
	 *            and from
	 * @param trafficlight
	 *            the trafficlight that regulates cars coming INTO this intersection
	 *            FROM the parameter intersection
	 */
	public void addConnection(Road road, Intersection intersection, ArrayList<TrafficLight> trafficlights) {
		if (trafficlights == null) {
			trafficlights = new ArrayList<TrafficLight>();
			for (int i = 1; i <= road.getLanes(); i++) {
				trafficlights.add(new TrafficLight(road, this, i));
			}
		}

		connections.add(new Connection(road, intersection, trafficlights));
	}

	public Road removeConnectionTo(Intersection intersection) {
		Road removed_road = null;
		for (int i = 0; i < this.connections.size(); i++) {
			if (this.connections.get(i).getDestination() == intersection) {
				removed_road = this.connections.get(i).getRoad();
				this.connections.remove(i);
			}
		}

		return removed_road;
	}

	public void adjustConnectionsAfterIntersectionRemoval(Intersection removed_intersection) {
		for (Connection c : this.connections) {
			if (c.getDestination() == removed_intersection) {
				this.connections.remove(c);
			}
		}
	}

	public void adjustConnectionsAfterRoadRemoval(Road removed_road) {
		for (Connection c : this.connections) {
			if (c.getRoad() == removed_road) {
				this.connections.remove(c);
			}
		}
	}

	// CHECKS

	public boolean equalCoordinatesWith(Intersection intersection) {
		return (intersection.getXCoord() == this.x_coord && intersection.getYCoord() == this.y_coord);
	}

	public boolean isAt(int x1, int y1) {
		if (this.getXCoord() == x1 && this.getYCoord() == y1) return true;
		return false;
	}

	// ACTIONS

	public void setTrafficLightActivity() {
		if (getTrafficLights().size() <= 2) {
			for (ArrayList<TrafficLight> tls : getTrafficLights()) {
				for (TrafficLight t : tls) {
					t.setStatus("G");
				}
			}
		} else {
			for (int i = 0; i < getTrafficLights().size(); i++) {
				if (i == active_light) {
					for (TrafficLight t : getTrafficLights().get(i)) {
						t.setStatus("G");
					}
				} else {
					for (TrafficLight t : getTrafficLights().get(i)) {
						t.setStatus("R");
					}
				}
			}
		}

		active_light++;
		if (active_light >= getTrafficLights().size()) {
			active_light = 0;
		}

	}

	public void updateTrafficLights(double delta_t) {
		this.time_till_toggle = this.time_till_toggle - delta_t;

		if (this.time_till_toggle <= 0) {
			this.setTrafficLightActivity();
			this.time_till_toggle = this.tl_phase_length;
		}
	}

	public void initializeTrafficLightSettings() {
		// TODO make it so that facing roads have same initial status etc.
		if (this.getTrafficLights().size() > 2) {
			this.active_light = ThreadLocalRandom.current().nextInt(0, this.getTrafficLights().size());
		}
		this.setTrafficLightActivity();
	}

	// OTHER

	public int numbConnections() {
		return this.connections.size();
	}

	@Override
	public String toString() {
		return this.x_coord + "," + this.y_coord + ",";
	}

	public Point getPoint() {
		return new Point(this.getXCoord(), this.getYCoord());
	}
}
