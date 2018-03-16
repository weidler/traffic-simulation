package datastructures;

public class TrafficLight {
	private String status;
	private Road road;
	private Intersection intersection;

	public TrafficLight(Road road, Intersection intersection) {
		this.road = road;
		this.intersection = intersection;
		
		this.status = "R";
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
	
	

	// ACTIONS
	
	public void toggle() {
		if (this.status == "R") {
			this.setStatus("G");
		} else if (this.status == "G") {
			this.setStatus("R");
		}
	}
}
