package datastructures;

import road.Road;
import java.util.ArrayList;

public class TrafficLight {
	private String status;
	private Road road;
	private int lanes;
	private Intersection intersection;
	private ArrayList<TrafficLight> TrafficLights = new ArrayList<TrafficLight>();

	public TrafficLight(Road road, Intersection intersection, int lanes) {
		this.road = road;
		this.intersection = intersection;
		this.lanes = lanes;
		this.status = "R";
		if(lanes > 1) {
			addTrafficLightToLanes(road,intersection,lanes);
		}
	}

	// GETTERS / SETTERS
	public String getStatus() {
		return status;
	}

	public Road getRoad() {
		return road;

	}

	public void setStatus(String status) {
		if (status != "R" && status != "G") {
			System.out.println("Illegal status '" + status + "'");
		}
		this.status = status;
	}

	//If multiple lanes, create an arrayList of traffic lights for the road
	public ArrayList<TrafficLight> addTrafficLightToLanes(Road road, Intersection intersection, int lanes){
		ArrayList<TrafficLight> TrafficLightList = new ArrayList<TrafficLight>();
		for(int i = 1; i < lanes; i++) {
			int tempLanes = lanes - i;
			TrafficLightList.add(new TrafficLight(road,intersection,tempLanes));
		}
		TrafficLights = TrafficLightList;
		return TrafficLightList;
	}

	public ArrayList<TrafficLight> getTrafficLightList(){
		return TrafficLights;
	}

	public Intersection getIntersection()
	{
		return intersection;
	}

	public boolean isRed() {
		if (this.status == "R") {
			return true;
		}

		return false;
	}

	public boolean isGreen() {
		if (this.status == "G") {
			return true;
		}

		return false;
	}

	// ACTIONS

	public void toggle() {
		if (this.status == "R") {
			this.setStatus("G");
		} else if (this.status == "G") {
			this.setStatus("R");
		}
	}

	@Override
	public String toString() {
		return "TrafficLight [status=" + status + ", intersection=" + intersection + "]";
	}
}
