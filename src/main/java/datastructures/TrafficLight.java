package datastructures;

import road.Road;

public class TrafficLight {
	private String status;
	private Road road;
	private int lanes;
	private Intersection intersection;

	public TrafficLight(Road road, Intersection intersection, int lanes) {
		this.road = road;
		this.intersection = intersection;
		this.lanes = lanes;
		this.status = "R";
		
		if(lanes > 1) {
			for (int i = 0; i < lanes; i++) {
				
			}
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
