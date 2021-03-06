package datastructures;

import road.Road;
import java.util.ArrayList;

public class TrafficLight {
	private String status;
	private Road road;
	private Intersection intersection;
	private int lane;

	public TrafficLight(Road road, Intersection intersection, int lane) {
		this.road = road;
		this.intersection = intersection;
		this.lane = lane;
		this.status = "R";
	}

	// GETTERS / SETTERS
	public String getStatus() {
		return status;
	}

	public Road getRoad() {
		return road;
	}

	public int getLane() {
		return lane;
	}

	public void setLane(int lane) {
		this.lane = lane;
	}

	public void setStatus(String status) {
		if (status != "R" && status != "G") {
			System.out.println("Illegal status '" + status + "'");
		}

		this.status = status;
	}

	public Intersection getIntersection() {
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
