package datastructures;

import java.util.ArrayList;
import road.Road;

public class Connection {

	private Road road;
	private Intersection destination;
	private ArrayList<TrafficLight> trafficlights;

	public Connection(Road road, Intersection destination, ArrayList<TrafficLight> trafficlights) {
		this.road = road;
		this.destination = destination;
		this.trafficlights = trafficlights;
	}

	// GETTER / SETTER

	public Road getRoad() {
		return road;
	}

	public void setRoad(Road road) {
		this.road = road;
	}

	public Intersection getDestination() {
		return destination;
	}

	public void setDestination(Intersection destination) {
		this.destination = destination;
	}

	public ArrayList<TrafficLight> getTrafficlights() {
		return trafficlights;
	}

	public void setTrafficlights(ArrayList<TrafficLight> trafficlights) {
		if (trafficlights.size() == this.getLanes()) this.trafficlights = trafficlights;
		else System.out.println("[ERROR] Unallowed Number of Trafficlights for this Road.");
	}

	public int getLanes() {
		return this.road.getLanes();
	}

	@Override
	public String toString() {
		return "Connection [road=" + road + ", destination=" + destination + ", trafficlights=" + trafficlights + "]";
	}
}
