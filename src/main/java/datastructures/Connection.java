package datastructures;

import java.util.ArrayList;
import road.Road;

public class Connection {

	private Road road;
	private Intersection destination;
	private TrafficLight trafficlight;
	private int lanes;
	private ArrayList<TrafficLight> TrafficLights = new ArrayList<TrafficLight>();
	
	public Connection(Road road, Intersection destination, TrafficLight trafficlight, int lanes) {
		this.road = road;
		this.destination = destination;
		this.trafficlight = trafficlight;
		this.lanes = lanes;
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

	public TrafficLight getTrafficlight() {
		return trafficlight;
	}

	public void setTrafficlight(TrafficLight trafficlight) {
		this.trafficlight = trafficlight;
	}

	
	@Override
	public String toString() {
		return "Connection [road=" + road + ", destination=" + destination + ", trafficlight=" + trafficlight + "]";
	}
}
