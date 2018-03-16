package datastructures;

import java.util.ArrayList;

public class Intersection {
	
	// Position
	private int x_coord;
	private int y_coord;
	
	// Traffic Lights
	private int tl_phase_length;
	private int time_till_toggle;
	
	// A Star stuff
	private double cost;
	private Intersection parent;
	private double g;
	private double h;
	
	// Connections
	private ArrayList<Connection> connections;
	
	// CONSTRUCTORS
	
	public Intersection(int x, int y) {
		this.x_coord = x;
		this.y_coord = y;
		
		this.tl_phase_length = 120;
		this.time_till_toggle = this.tl_phase_length;
		
		this.connections = new ArrayList<Connection>();
	}
	
	// GETTERS / SETTERS
	
	public int getXCoord() {
		return x_coord;
	}
	
	public int getYCoord() {
		return y_coord;
	}
	
	public int getTlPhaseLength() {
		return tl_phase_length;
	}

	public void setTlPhaseLength(int tl_phase_length) {
		this.tl_phase_length = tl_phase_length;
	}

	public void resetCost()
	{
		cost = 1000000000;
	}
	
	public void resetParent()
	{
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
	
	public Intersection getRoadTo(Intersection destination) {
		for (Connection c : this.connections) {
			if (c.getDestination() == destination) {
				return c.getDestination();
			}
		}
		
		return null;
	}
	
	public ArrayList<Connection> getConnections() {
		return this.connections;
	}
	
	public ArrayList<TrafficLight> getTrafficLights() {
		ArrayList<TrafficLight> traffic_lights = new ArrayList<TrafficLight>();
		for (Connection c : this.connections) {
			traffic_lights.add(c.getTrafficlight());
		}
		
		return traffic_lights;
	}
	
	// MODIFICATION
	
	public void addConnection(Road road, Intersection intersection, TrafficLight trafficlight) {
		connections.add(new Connection(road, intersection, trafficlight));
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
	
	public boolean connectionCanBeAdded() {
		if (numbConnections() < 4) {
			return true;
		}
		
		return false;
	}
	
	public boolean equalCoordinatesWith(Intersection intersection) {
		return (intersection.getXCoord() == this.x_coord && intersection.getYCoord() == this.y_coord);
	}

	// ACTIONS
	
	public void updateTrafficLights() {
		if (this.time_till_toggle == 0) {
			for (TrafficLight tl : this.getTrafficLights()) {
				tl.toggle();
			}
			
			this.time_till_toggle = this.tl_phase_length;
		}
		
		this.time_till_toggle--;
	}
	
	public void initializeTrafficLightSettings() {
		// TODO make it so that facing roads have same initial status etc.
	}

	// OTHER
	
	public int numbConnections() {
		return this.connections.size();
	}
	
	public String toString() {
		return "Intersection: (" + this.x_coord + ", " + this.y_coord + ")";
	}

	
}
